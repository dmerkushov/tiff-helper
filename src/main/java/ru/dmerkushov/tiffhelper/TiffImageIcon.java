/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.tiffhelper;

import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import javax.swing.Icon;

/**
 *
 * @author Dmitriy Merkushov
 */
public class TiffImageIcon extends TiffImage implements Icon {
	
	public TiffImageIcon (File imageFile) throws TiffHelperException {
		super (imageFile);
	}

	public TiffImageIcon (String imageFilename) throws TiffHelperException {
		super (imageFilename);
	}
	
	public TiffImageIcon () throws TiffHelperException {
		super ();
	}

	public TiffImageIcon (int width, int height) throws TiffHelperException {
		super (width, height);
	}

	@Override
	public int getIconWidth () {
		return getWidth ();
	}

	@Override
	public int getIconHeight () {
		return getHeight ();
	}

	@Override
	public void paintIcon (Component c, Graphics g, int posx, int posy) {
		int[] rgb;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				rgb = getRGB (x, y);
				g.setColor (new java.awt.Color (rgb[RED], rgb[GREEN], rgb[BLUE]));
				g.drawLine (x + posx, y + posy, x + posx, y + posy);
			}
		}
	}
}
