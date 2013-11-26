/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.tiffhelper;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Dmitriy Merkushov
 */
public class TiffImageTest {

	public TiffImageTest () {
	}

	@BeforeClass
	public static void setUpClass () {
	}

	@AfterClass
	public static void tearDownClass () {
	}

	@Before
	public void setUp () {
	}

	@After
	public void tearDown () {
	}

	/**
	 * Test of class TiffImage.
	 */
	@Test
	public void testTiffImage () {
		String mainImageFilename = "example1.tiff";
		String stampFilename = "stamp.tiff";
		String g4Filename = "example1-g4.tiff";
		String grayscaleFilename = "example1-grayscale.tiff";
		String colorFilename = "example1-color.tiff";
		String negStampedFilename = "example1-stamped-negativecoords.tiff";
		String greatCoordsStampedFilename = "example1-stamped-greatcoords.tiff";

		System.out.println ("Beginning");

		long began = new java.util.Date ().getTime ();

		TiffImage exampleTiffImage;
		try {
			exampleTiffImage = new TiffImage (new File (mainImageFilename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
			return;
		}

		long loadedMainImage = new java.util.Date ().getTime ();
		System.out.println ("Loaded the main image: " + mainImageFilename + ", " + (loadedMainImage - began) + " millis");

		TiffImage stampTiffImage;
		try {
			stampTiffImage = new TiffImage (new File (stampFilename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
			return;
		}

		long loadedStamp = new java.util.Date ().getTime ();
		System.out.println ("Loaded the stamp: " + stampFilename + ", " + (loadedStamp - loadedMainImage) + " millis");
		try {
			exampleTiffImage.stamp (stampTiffImage, 100, 50);
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
			return;
		}

		long stamped = new java.util.Date ().getTime ();
		System.out.println ("Stamped: " + (stamped - loadedStamp) + " millis");

		try {
			exampleTiffImage.saveTiffBWG4 (new File (g4Filename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		long savedG4 = new java.util.Date ().getTime ();
		System.out.println ("Saved CCITT G4 tiff: " + g4Filename + ", " + (savedG4 - stamped) + " millis");

		try {
			exampleTiffImage.saveTiffGrayscalePackbits (new File (grayscaleFilename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		long savedGrayscale = new java.util.Date ().getTime ();
		System.out.println ("Saved Grayscale tiff: " + grayscaleFilename + ", " + (savedGrayscale - savedG4) + " millis");

		try {
			exampleTiffImage.saveTiffColorPackbits (new File (colorFilename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		long savedColor = new java.util.Date ().getTime ();
		System.out.println ("Saved color tiff: " + colorFilename + ", " + (savedColor - savedGrayscale) + " millis");
		System.out.println ("Saved the results total time: " + (savedColor - stamped) + " millis");

		try {
			exampleTiffImage.stamp (stampTiffImage, -300, -300);
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		try {
			exampleTiffImage.saveTiffColorPackbits (new File (negStampedFilename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		System.out.println ("Stamped in negative position: " + negStampedFilename);
		
		try {
			exampleTiffImage.stamp (stampTiffImage, 2350, 3350);
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		try {
			exampleTiffImage.saveTiffColorPackbits (new File (greatCoordsStampedFilename));
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffImageTest.class.getName ()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		System.out.println ("Stamped in great coords position: " + greatCoordsStampedFilename);
	}
}
