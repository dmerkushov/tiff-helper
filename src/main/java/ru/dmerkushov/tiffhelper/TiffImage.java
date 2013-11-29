/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.tiffhelper;

import com.multiconn.fop.codec.ByteArraySeekableStream;
import com.multiconn.fop.codec.TIFFDecodeParam;
import com.multiconn.fop.codec.TIFFDirectory;
import com.multiconn.fop.codec.TIFFEncodeParam;
import com.multiconn.fop.codec.TIFFField;
import com.multiconn.fop.codec.TIFFImageDecoder;
import com.multiconn.fop.codec.TIFFImageEncoder;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import ru.dmerkushov.oshelper.OSHelper;
import ru.dmerkushov.oshelper.OSHelperException;

/**
 *
 * @author Dmitriy Merkushov
 */
public class TiffImage {

	/**
	 * Number of samples per pixel
	 */
	public static final int INTERNAL_NUM_SAMPLES = 3;
	/**
	 * Red sample index in the RGB array of a pixel
	 */
	public static final int RED = 0;
	/**
	 * Green sample index in the RGB array of a pixel
	 */
	public static final int GREEN = 1;
	/**
	 * Blue sample index in the RGB array of a pixel
	 */
	public static final int BLUE = 2;
	/**
	 * Resolution unit: none
	 */
	public static final char RESOLUTION_UNIT_NONE = 1;
	/**
	 * Resolution unit: inch
	 */
	public static final char RESOLUTION_UNIT_INCH = 2;
	/**
	 * Resolution unit: centimeter
	 */
	public static final char RESOLUTION_UNIT_CENTIMETER = 3;
	/**
	 * Pixels array
	 */
	int[] pixels;
	/**
	 * X resolution: default is 300
	 * @see TiffImage#getXResolution() 
	 * @see TiffImage#setXResolution() 
	 * @see TiffImage#getResolutionUnit() 
	 */
	long[][] xResolution = {{300, 1}};
	/**
	 * Y resolution: default is 300
	 * @see TiffImage#getYResolution() 
	 * @see TiffImage#setYResolution() 
	 * @see TiffImage#getResolutionUnit() 
	 */
	long[][] yResolution = {{300, 1}};
	/**
	 * Resolution unit: default is inch
	 * @see TiffImage#getResolutionUnit() 
	 */
	char[] resolutionUnit = {RESOLUTION_UNIT_INCH};
	/**
	 * Image width in pixels
	 */
	int width;
	/**
	 * Image height in pixels
	 */
	int height;

	/**
	 * Create an empty image with width and height both 0
	 *
	 * @throws TiffHelperException
	 */
	public TiffImage () throws TiffHelperException {
		this (0, 0);
	}

	/**
	 * Create an empty (white) image
	 *
	 * @param width
	 * @param height
	 * @throws TiffHelperException
	 */
	public TiffImage (int width, int height) throws TiffHelperException {
		if (width < 0) {
			if (height < 0) {
				throw new TiffHelperException ("width and height are both < 0");
			} else {
				throw new TiffHelperException ("width < 0");
			}
		}
		if (height < 0) {
			throw new TiffHelperException ("height < 0");
		}

		this.width = width;
		this.height = height;
		long pixelsSize = width * height * INTERNAL_NUM_SAMPLES;

		if (pixelsSize > Integer.MAX_VALUE) {
			throw new TiffHelperException ("Image too big");
		}

		pixels = new int[(int) pixelsSize];
		initializePixelsArray (pixels);
	}

	/**
	 * Load this TIFF image from a TIFF file
	 *
	 * @param tiffImageFilename
	 * @throws TiffHelperException
	 */
	public TiffImage (String tiffImageFilename) throws TiffHelperException {
		this (new File (tiffImageFilename));
	}

	/**
	 * Load this TIFF image from a TIFF file
	 *
	 * @param tiffImageFile
	 * @throws TiffHelperException
	 */
	public TiffImage (File tiffImageFile) throws TiffHelperException {
		if (tiffImageFile == null) {
			throw new TiffHelperException ("Image file is set to null");
		}
		if (!tiffImageFile.exists ()) {
			throw new TiffHelperException ("Image file " + tiffImageFile.getAbsolutePath () + " does not exist");
		}
		if (!tiffImageFile.canRead ()) {
			throw new TiffHelperException ("Can not read from image file " + tiffImageFile.getAbsolutePath () + " ! Check access rights");
		}

		byte[] imageFileBytes;
		try {
			imageFileBytes = OSHelper.readFile (tiffImageFile.getAbsolutePath ());
		} catch (OSHelperException ex) {
			throw new TiffHelperException (ex);
		}

		ByteArraySeekableStream bass;
		try {
			bass = new ByteArraySeekableStream (imageFileBytes);
		} catch (IOException ex) {
			throw new TiffHelperException (ex);
		}

		TIFFDirectory tiffDirectory;
		try {
			tiffDirectory = new TIFFDirectory (bass, 0);
		} catch (IOException ex) {
			throw new TiffHelperException (ex);
		}
		// Now reading resolution data from the original image, since it can be not 300 dpi
		if ((tiffDirectory != null) && (tiffDirectory.isTagPresent (TIFFImageDecoder.TIFF_X_RESOLUTION))) {
			TIFFField xResField = tiffDirectory.getField (TIFFImageDecoder.TIFF_X_RESOLUTION);
			xResolution = xResField.getAsRationals ();
		}
		if ((tiffDirectory != null) && (tiffDirectory.isTagPresent (TIFFImageDecoder.TIFF_Y_RESOLUTION))) {
			TIFFField yResField = tiffDirectory.getField (TIFFImageDecoder.TIFF_Y_RESOLUTION);
			yResolution = yResField.getAsRationals ();
		}
		if ((tiffDirectory != null) && (tiffDirectory.isTagPresent (TIFFImageDecoder.TIFF_RESOLUTION_UNIT))) {
			TIFFField resUnitField = tiffDirectory.getField (TIFFImageDecoder.TIFF_RESOLUTION_UNIT);
			resolutionUnit = resUnitField.getAsChars ();
		}

		TIFFDecodeParam tiffDecodeParam = new TIFFDecodeParam ();

		TIFFImageDecoder tiffImageDecoder = new TIFFImageDecoder (bass, tiffDecodeParam);
		RenderedImage renderedImage;
		try {
			renderedImage = tiffImageDecoder.decodeAsRenderedImage ();
		} catch (IOException ex) {
			throw new TiffHelperException (ex);
		}

		loadPixelsFromRenderedImage (renderedImage);
	}

	/**
	 * Create this TIFF image from a rendered image, specifying resolution in
	 * dpi
	 *
	 * @param renderedImage
	 * @param resolutionDpi
	 * @throws TiffHelperException
	 */
	public TiffImage (RenderedImage renderedImage, long resolutionDpi) throws TiffHelperException {
		this (renderedImage, resolutionDpi, resolutionDpi, TiffImage.RESOLUTION_UNIT_INCH);
	}

	/**
	 * Create this TIFF image from a rendered image, specifying exact resolution
	 *
	 * @param renderedImage
	 * @param xResolution
	 * @param yResolution
	 * @param resolutionUnit
	 * @throws TiffHelperException
	 * @see TiffImage#RESOLUTION_UNIT_INCH
	 * @see TiffImage#RESOLUTION_UNIT_CENTIMETER
	 */
	public TiffImage (RenderedImage renderedImage, long xResolution, long yResolution, char resolutionUnit) throws TiffHelperException {
		setXResolution (xResolution);
		setYResolution (yResolution);
		setResolutionUnit (resolutionUnit);
		loadPixelsFromRenderedImage (renderedImage);
	}

	/**
	 * Get the RGB array of a pixel
	 *
	 * @param x
	 * @param y
	 * @return
	 * @see TiffImage#RED
	 * @see TiffImage#GREEN
	 * @see TiffImage#BLUE
	 */
	public int[] getRGB (int x, int y) {
		int[] rgb = new int[INTERNAL_NUM_SAMPLES];
		System.arraycopy (pixels, (x * height + y) * INTERNAL_NUM_SAMPLES, rgb, 0, INTERNAL_NUM_SAMPLES);
		return rgb;
	}

	/**
	 * Set the RGB value of a specified pixel
	 *
	 * <b>WARNING!</b> Since this method is often called in cycles, no check is
	 * done
	 *
	 * @param x
	 * @param y
	 * @param rgb array of values (values 0-255)
	 * @see TiffImage#setRGBSafe(int, int, int[])
	 * @see TiffImage#RED
	 * @see TiffImage#GREEN
	 * @see TiffImage#BLUE
	 */
	public void setRGB (int x, int y, int[] rgb) {
		setRGBInPixelsArray (x, y, rgb, pixels, height);
	}

	private void setRGBInPixelsArray (int x, int y, int[] rgb, int[] pixelsArray, int height) {
		System.arraycopy (rgb, 0, pixelsArray, (x * height + y) * INTERNAL_NUM_SAMPLES, INTERNAL_NUM_SAMPLES);
	}

	/**
	 * Safely set the RGB value of a specified pixel
	 *
	 * <b>WARNING!</b> Since a number of checks is done, this method is slower
	 * than setRGB ()
	 *
	 * @param x
	 * @param y
	 * @param rgb array of values: RED - item 0, GREEN - item 1, BLUE - item 2
	 * (values 0-255)
	 * @throws TiffHelperException
	 * @see TiffImage#setRGB(int, int, int[])
	 */
	public void setRGBSafe (int x, int y, int[] rgb) throws TiffHelperException {
		if (x < 0) {
			throw new TiffHelperException ("x < 0: " + x);
		}
		if (y < 0) {
			throw new TiffHelperException ("y < 0: " + y);
		}
		int width = getWidth ();
		if (x >= width) {
			throw new TiffHelperException ("x >= width: " + x + ">=" + width);
		}
		int height = getHeight ();
		if (y >= height) {
			throw new TiffHelperException ("y >= height: " + y + ">=" + height);
		}
		if (rgb.length != 3) {
			throw new TiffHelperException ("RGB array length is not 3: " + rgb.length);
		}
		if (rgb[RED] < 0) {
			throw new TiffHelperException ("RED < 0: " + rgb[RED]);
		}
		if (rgb[RED] > 255) {
			throw new TiffHelperException ("RED > 255: " + rgb[RED]);
		}
		if (rgb[GREEN] < 0) {
			throw new TiffHelperException ("GREEN < 0: " + rgb[GREEN]);
		}
		if (rgb[GREEN] > 255) {
			throw new TiffHelperException ("GREEN > 255: " + rgb[GREEN]);
		}
		if (rgb[BLUE] < 0) {
			throw new TiffHelperException ("BLUE < 0: " + rgb[BLUE]);
		}
		if (rgb[BLUE] > 255) {
			throw new TiffHelperException ("BLUE > 255: " + rgb[BLUE]);
		}

		setRGB (x, y, rgb);
	}

	/**
	 * Get width of the image in pixels
	 *
	 * @return
	 */
	public int getWidth () {
		return width;
	}

	/**
	 * Get height of the image in pixels
	 *
	 * @return
	 */
	public int getHeight () {
		return height;
	}

	/**
	 * Set a new width for the image
	 *
	 * @param newWidth
	 */
	public void setWidth (int newWidth) throws TiffHelperException {
		int newHeight = getHeight ();
		setWidthHeight (newWidth, newHeight);
	}

	/**
	 * Set a new height for the image
	 *
	 * @param newHeight
	 */
	public void setHeight (int newHeight) throws TiffHelperException {
		int newWidth = getWidth ();
		setWidthHeight (newWidth, newHeight);
	}

	/**
	 * Set new width and height for the image. The old image data is saved at
	 * position (0,0) of the new image in its bounds. The old image data that
	 * exceeds the new bounds is lost.
	 *
	 * @param newWidth
	 * @param newHeight
	 */
	public void setWidthHeight (int newWidth, int newHeight) throws TiffHelperException {
		if (newWidth < 0) {
			if (newHeight < 0) {
				throw new TiffHelperException ("new width and height are both < 0");
			} else {
				throw new TiffHelperException ("new width < 0");
			}
		} else if (height < 0) {
			throw new TiffHelperException ("new height < 0");
		}

		long newPixelsSize = newWidth * newHeight * INTERNAL_NUM_SAMPLES;

		if (newPixelsSize > Integer.MAX_VALUE) {
			throw new TiffHelperException ("Image too big");
		}

		int[] newPixels = new int[(int) newPixelsSize];
		initializePixelsArray (newPixels);

		int[] thisRGB;
		int minWidth = Math.min (width, newWidth);
		int minHeight = Math.min (height, newHeight);
		for (int x = 0; x < minWidth; x++) {
			for (int y = 0; y < minHeight; y++) {
				thisRGB = getRGB (x, y);
				setRGBInPixelsArray (x, y, thisRGB, newPixels, newHeight);
			}
		}

		this.width = newWidth;
		this.height = newHeight;
		this.pixels = newPixels;
	}

	/**
	 * Making all the pixels array white
	 */
	public void initializePixelsArray () {
		initializePixelsArray (pixels);
	}

	private void initializePixelsArray (int[] pixels) {
		int pixelsLength = pixels.length;
		for (int i = 0; i < pixelsLength; i++) {
			pixels[i] = 255;
		}
	}

	/**
	 * Get X resolution in resolution units
	 *
	 * @return
	 * @see TiffImage#getResolutionUnit()
	 * @see TiffImage#setResolutionUnit()
	 */
	public long getXResolution () {
		return xResolution[0][0];
	}

	/**
	 * Get Y resolution in resolution units
	 *
	 * @return
	 * @see TiffImage#getResolutionUnit()
	 * @see TiffImage#setResolutionUnit()
	 */
	public long getYResolution () {
		return yResolution[0][0];
	}

	/**
	 * Set X resolution in resolution units
	 * @param xResolution 
	 * @see TiffImage#getResolutionUnit()
	 * @see TiffImage#setResolutionUnit()
	 */
	public final void setXResolution (long xResolution) {
		this.xResolution[0][0] = xResolution;
	}

	/**
	 * Set Y resolution in resolution units
	 * @param yResolution 
	 * @see TiffImage#getResolutionUnit()
	 * @see TiffImage#setResolutionUnit()
	 */
	public final void setYResolution (long yResolution) {
		this.yResolution[0][0] = yResolution;
	}

	/**
	 * Get the resulution unit for this image
	 * @return 
	 * @see TiffImage#RESOLUTION_UNIT_CENTIMETER
	 * @see TiffImage#RESOLUTION_UNIT_INCH
	 * @see TiffImage#RESOLUTION_UNIT_NONE
	 */
	public char getResolutionUnit () {
		return resolutionUnit[0];
	}

	/**
	 * Set the resulution unit for this image
	 * @return 
	 * @see TiffImage#RESOLUTION_UNIT_CENTIMETER
	 * @see TiffImage#RESOLUTION_UNIT_INCH
	 * @see TiffImage#RESOLUTION_UNIT_NONE
	 */
	public final void setResolutionUnit (char resolutionUnit) throws TiffHelperException {
		if (resolutionUnit != RESOLUTION_UNIT_INCH && resolutionUnit != RESOLUTION_UNIT_CENTIMETER && resolutionUnit != RESOLUTION_UNIT_NONE) {
			throw new TiffHelperException ("Unknown resolution unit: " + resolutionUnit + ". Must be RESOLUTION_UNIT_NONE (1), or RESOLUTION_UNIT_INCH (2), or RESOLUTION_UNIT_CENTIMETER (3)");
		}
		this.resolutionUnit[0] = resolutionUnit;
	}

	/**
	 * Load the image data from a RenderedImage instance
	 * @param renderedImage
	 * @throws TiffHelperException 
	 * @see RenderedImage
	 */
	public final void loadPixelsFromRenderedImage (RenderedImage renderedImage) throws TiffHelperException {
		Raster raster = renderedImage.getData ();

		width = raster.getWidth ();
		height = raster.getHeight ();

		pixels = new int[width * height * 3];

		ColorModel colorModel = renderedImage.getColorModel ();

		int numBands = raster.getNumBands ();
		int[] rasterPixelInts = new int[numBands];
		byte[] rasterPixelBytes = new byte[numBands];

		if (colorModel.getTransferType () == DataBuffer.TYPE_BYTE) {
			if (colorModel.hasAlpha () && !colorModel.isAlphaPremultiplied ()) {	// If alpha channel means anything
				int alpha;
				int indexInInternalPixels = 0;

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						rasterPixelInts = raster.getPixel (x, y, rasterPixelInts);

						for (int i = 0; i < numBands; i++) {
							rasterPixelBytes[i] = (byte) rasterPixelInts[i];
						}

						alpha = colorModel.getAlpha (rasterPixelBytes);

						pixels[indexInInternalPixels++] = colorModel.getRed (rasterPixelBytes) * alpha / 256;
						pixels[indexInInternalPixels++] = colorModel.getGreen (rasterPixelBytes) * alpha / 256;
						pixels[indexInInternalPixels++] = colorModel.getBlue (rasterPixelBytes) * alpha / 256;
					}
				}
			} else {		// If alpha channel does not exist or exists, but means nothing - we ignore it
				int indexInInternalPixels = 0;

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						rasterPixelInts = raster.getPixel (x, y, rasterPixelInts);

						for (int i = 0; i < numBands; i++) {
							rasterPixelBytes[i] = (byte) rasterPixelInts[i];
						}

						pixels[indexInInternalPixels++] = colorModel.getRed (rasterPixelBytes);
						pixels[indexInInternalPixels++] = colorModel.getGreen (rasterPixelBytes);
						pixels[indexInInternalPixels++] = colorModel.getBlue (rasterPixelBytes);
					}
				}
			}
		} else if (colorModel.getTransferType () == DataBuffer.TYPE_INT) {
			if (colorModel.hasAlpha () && !colorModel.isAlphaPremultiplied ()) {	// If alpha channel means anything
				int alpha;
				int indexInInternalPixels = 0;

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						rasterPixelInts = raster.getPixel (x, y, rasterPixelInts);

						alpha = colorModel.getAlpha (rasterPixelBytes);

						pixels[indexInInternalPixels++] = colorModel.getRed (rasterPixelInts) * alpha / 256;
						pixels[indexInInternalPixels++] = colorModel.getGreen (rasterPixelInts) * alpha / 256;
						pixels[indexInInternalPixels++] = colorModel.getBlue (rasterPixelInts) * alpha / 256;
					}
				}
			} else {		// If alpha channel does not exist or exists, but means nothing - we ignore it
				int indexInInternalPixels = 0;

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						rasterPixelInts = raster.getPixel (x, y, rasterPixelInts);

						pixels[indexInInternalPixels++] = rasterPixelInts[0]; //colorModel.getRed (rasterPixelInts);
						pixels[indexInInternalPixels++] = rasterPixelInts[1];// colorModel.getGreen (rasterPixelInts);
						pixels[indexInInternalPixels++] = rasterPixelInts[2];// colorModel.getBlue (rasterPixelInts);
					}
				}
			}
		}
	}

	/**
	 * Stamp the image. The stamp will be truncated if it goes beyond the
	 * borders of the image
	 *
	 * @param stampTiffImage
	 * @param posx
	 * @param posy
	 * @throws TiffHelperException
	 */
	public void stamp (TiffImage stampTiffImage, int posx, int posy) throws TiffHelperException {
		if (stampTiffImage == null) {
			throw new TiffHelperException ("stampTiffImage is null");
		}

		int stampWidth = stampTiffImage.getWidth ();
		int stampHeight = stampTiffImage.getHeight ();

		int[] thisRGB;
		int[] stampRGB;

		int xToReach = Math.min (posx + stampWidth, width - 1);
		int yToReach = Math.min (posy + stampHeight, height - 1);

		for (int x = posx; x < xToReach; x++) {
			if (x >= 0) {
				for (int y = posy; y < yToReach; y++) {
					if (y >= 0) {
						thisRGB = getRGB (x, y);
						stampRGB = stampTiffImage.getRGB (x - posx, y - posy);

						if (thisRGB[RED] > stampRGB[RED]) {
							thisRGB[RED] = stampRGB[RED];
						}
						if (thisRGB[GREEN] > stampRGB[GREEN]) {
							thisRGB[GREEN] = stampRGB[GREEN];
						}
						if (thisRGB[BLUE] > stampRGB[BLUE]) {
							thisRGB[BLUE] = stampRGB[BLUE];
						}

						setRGB (x, y, thisRGB);
					}
				}
			}
		}
	}

	/**
	 * Save the image as color TIFF with PACKNITS compression
	 * @param file
	 * @throws TiffHelperException 
	 */
	public void saveTiffColorPackbits (File file) throws TiffHelperException {
		if (file == null) {
			throw new TiffHelperException ("To save file object is set to null");
		}

		BufferedImage bufferedImage = new java.awt.image.BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster writableRaster = bufferedImage.getRaster ();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int[] rgb = getRGB (x, y);
				writableRaster.setPixel (x, y, rgb);
			}
		}

		saveTiff (file, bufferedImage, TIFFEncodeParam.COMPRESSION_PACKBITS);
	}

	/**
	 * Save the image as grayscale TIFF with PACKBITS compression
	 * @param file
	 * @throws TiffHelperException 
	 */
	public void saveTiffGrayscalePackbits (File file) throws TiffHelperException {
		if (file == null) {
			throw new TiffHelperException ("To save file object is set to null");
		}

		BufferedImage bufferedImage = new java.awt.image.BufferedImage (width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster writableRaster = bufferedImage.getRaster ();

		int[] avgColor = new int[1];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int[] rgb = getRGB (x, y);
				avgColor[0] = (rgb[RED] + rgb[GREEN] + rgb[BLUE]) / 3;
				writableRaster.setPixel (x, y, avgColor);
			}
		}

		saveTiff (file, bufferedImage, TIFFEncodeParam.COMPRESSION_PACKBITS);
	}

	/**
	 * Save the image as B/W TIFF with CCITT G4 compression (single-strip)
	 * @param file
	 * @throws TiffHelperException 
	 */
	public void saveTiffBWG4 (File file) throws TiffHelperException {
		if (file == null) {
			throw new TiffHelperException ("To save file object is set to null");
		}

		byte[] map = new byte[]{(byte) (255), (byte) (0)};
		IndexColorModel bwColorModel = new IndexColorModel (1, 2, map, map, map);
		BufferedImage bufferedImage = new BufferedImage (getWidth (), getHeight (), BufferedImage.TYPE_BYTE_BINARY, bwColorModel);
		WritableRaster writableRaster = bufferedImage.getRaster ();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int[] rgb = getRGB (x, y);
				int avgColor = (rgb[RED] + rgb[GREEN] + rgb[BLUE]) / 3;
				int[] indexedColor = new int[1];
				if (avgColor >= 153) {
					indexedColor[0] = 0;		// white
				} else {
					indexedColor[0] = 1;		// black
				}
				writableRaster.setPixel (x, y, indexedColor);
			}
		}

		saveTiff (file, bufferedImage, TIFFEncodeParam.COMPRESSION_GROUP4);
	}

	private void saveTiff (File file, BufferedImage bufferedImage, int compression) throws TiffHelperException {
		TIFFEncodeParam tiffEncodeParam = new TIFFEncodeParam ();
		tiffEncodeParam.setCompression (compression);
		tiffEncodeParam.setWriteTiled (false);

		// From Javadoc:
		// setTileSize
		// public void setTileSize(int tileWidth, int tileHeight)
		// Sets the dimensions of the tiles to be written. If either value is non-positive, the encoder will use a default value.
		// If the data are being written as tiles, i.e., getWriteTiled() returns true, then the default tile dimensions used by the encoder are those of the tiles of the image being encoded.
		// If the data are being written as strips, i.e., getWriteTiled() returns false, the width of each strip is always the width of the image and the default number of rows per strip is 8.
		// If JPEG compession is being used, the dimensions of the strips or tiles may be modified to conform to the JPEG-in-TIFF specification.
		// Parameters:
		// tileWidth - The tile width; ignored if strips are used.
		// tileHeight - The tile height or number of rows per strip.
		tiffEncodeParam.setTileSize (0, getHeight ());

		TIFFField[] extraFields = new TIFFField[3];
		extraFields[0] = new TIFFField (TIFFImageDecoder.TIFF_X_RESOLUTION, TIFFField.TIFF_RATIONAL, 1, xResolution);
		extraFields[1] = new TIFFField (TIFFImageDecoder.TIFF_Y_RESOLUTION, TIFFField.TIFF_RATIONAL, 1, yResolution);
		extraFields[2] = new TIFFField (TIFFImageDecoder.TIFF_RESOLUTION_UNIT, TIFFField.TIFF_SHORT, 1, resolutionUnit);
		tiffEncodeParam.setExtraFields (extraFields);

		TIFFImageEncoder tiffImageEncoder;
		try {
			tiffImageEncoder = new TIFFImageEncoder (new FileOutputStream (file), tiffEncodeParam);
		} catch (FileNotFoundException ex) {
			throw new TiffHelperException (ex);
		}
		try {
			tiffImageEncoder.encode (bufferedImage);
		} catch (IOException ex) {
			throw new TiffHelperException (ex);
		}
	}
}
