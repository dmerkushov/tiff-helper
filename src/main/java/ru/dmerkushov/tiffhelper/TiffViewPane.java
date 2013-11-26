/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.tiffhelper;

import java.io.File;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Dmitriy Merkushov
 */
public class TiffViewPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	private TiffImageIcon tiffImageIcon;
	private JLabel imageLabel;
	
	public TiffViewPane (TiffImageIcon tiffImageIcon) {
		super ();
		setTiffImageIcon (tiffImageIcon);
	}
	
	public TiffViewPane (String tiffImageFilename) throws TiffHelperException {
		super ();
		TiffImageIcon tiffImageIcon = new TiffImageIcon (tiffImageFilename);
		setTiffImageIcon (tiffImageIcon);
	}
	
	public TiffViewPane (File tiffImageFile) throws TiffHelperException {
		super ();
		TiffImageIcon tiffImageIcon = new TiffImageIcon (tiffImageFile);
		setTiffImageIcon (tiffImageIcon);
	}
	
	public TiffViewPane () throws TiffHelperException {
		super ();
		TiffImageIcon tiffImageIcon = new TiffImageIcon ();
		setTiffImageIcon (tiffImageIcon);
	}

	public TiffImageIcon getTiffImageIcon () {
		return tiffImageIcon;
	}

	public final void setTiffImageIcon (TiffImageIcon tiffImageIcon) {
		setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.imageLabel = new JLabel (tiffImageIcon);
		this.setViewportView (imageLabel);
		this.tiffImageIcon = tiffImageIcon;
	}

	public JLabel getImageLabel () {
		return imageLabel;
	}
}
