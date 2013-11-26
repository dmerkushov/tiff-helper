/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.tiffhelper;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
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
public class TiffViewPaneTest {
	
	public TiffViewPaneTest () {
	}
	
	public static TiffViewPane tiffViewPaneCreatedWithName;
	public static TiffViewPane tiffViewPaneCreatedEmpty;
	public static TiffViewPane tiffViewPaneCreatedWithNonexistentFile;
	
	public static String mainImageFilename = "example1.tiff";
	public static String nonExistentFilename = "i_dont_exist.tiff";
	
	public static TiffImageIcon tiffImageIcon;
	
	@BeforeClass
	public static void beforeClass () {
		try {
			tiffImageIcon = new TiffImageIcon (mainImageFilename);
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffViewPaneTest.class.getName()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		try {
			tiffViewPaneCreatedEmpty = new TiffViewPane ();
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffViewPaneTest.class.getName()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
			return;
		}
		try {
			tiffViewPaneCreatedWithName = new TiffViewPane (mainImageFilename);
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffViewPaneTest.class.getName()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
	}

	@AfterClass
	public static void tearDownClass () throws Exception {
	}

	@Before
	public void setUp () throws Exception {
	}

	@After
	public void tearDown () throws Exception {
	}

	/**
	 * Test of class TiffViewPane.
	 */
	@Test
	public void testTiffViewPaneEmpty () {
		System.out.println ("TiffViewPane () - no parameters");
		
		try {
			tiffViewPaneCreatedEmpty = new TiffViewPane ();
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffViewPaneTest.class.getName()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
			return;
		}
		performChecks (tiffViewPaneCreatedEmpty, "tiffViewPaneCreatedEmpty");
	}
	
	@Test
	public void testTiffViewPaneWithName () {
		System.out.println ("TiffViewPane (filename)");
		
		try {
			tiffViewPaneCreatedWithName = new TiffViewPane (mainImageFilename);
		} catch (TiffHelperException ex) {
			Logger.getLogger (TiffViewPaneTest.class.getName()).log (Level.SEVERE, null, ex);
			fail (ex.getMessage ());
		}
		performChecks (tiffViewPaneCreatedWithName, "tiffViewPaneCreatedWithName");
	}
	
	@Test
	public void testTiffViewPaneNonexistent () {
		System.out.println ("TiffViewPane (filename) - non-existent file");
		
		boolean exceptionThrown = false;
		
		try {
			tiffViewPaneCreatedWithNonexistentFile = new TiffViewPane (nonExistentFilename);
		} catch (TiffHelperException ex) {
			String exMessage = ex.getMessage ();
			
			if (exMessage.startsWith ("Image file ") && exMessage.endsWith (" does not exist")) {
				exceptionThrown = true;
				System.out.println ("Exception thrown, it's ok: " + ex.getMessage ());
			} else {
				Logger.getLogger (TiffViewPaneTest.class.getName()).log (Level.SEVERE, null, ex);
				fail (exMessage);
			}
		}
		
		if (!exceptionThrown) {
			fail ("Managed to create a TiffViewPane with non-existent file: it's not ok");
		}
	}
	
	private void performChecks (TiffViewPane tiffViewPane, String tiffViewPaneName) {
		if (tiffViewPane.getHorizontalScrollBarPolicy () != JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS) {
			fail (tiffViewPaneName + ": Horizontal scrollbar policy is not HORIZONTAL_SCROLLBAR_ALWAYS");
		}
		if (tiffViewPane.getVerticalScrollBarPolicy () != JScrollPane.VERTICAL_SCROLLBAR_ALWAYS) {
			fail (tiffViewPaneName + ": Vertical scrollbar policy is not VERTICAL_SCROLLBAR_ALWAYS");
		}
		
		JLabel imageLabel = tiffViewPane.getImageLabel ();
		if (!(imageLabel.getIcon () instanceof TiffImageIcon)) {
			fail (tiffViewPaneName + ": Image label's icon is not an ancestor of TiffImageIcon");
		}
	}

	/**
	 * Test of setTiffImageIcon method, of class TiffViewPane.
	 */
	@Test
	public void testSetTiffImageIcon () {
		System.out.println ("setTiffImageIcon ()");
		
		tiffViewPaneCreatedEmpty.setTiffImageIcon (tiffImageIcon);
		performChecks (tiffViewPaneCreatedEmpty, "tiffViewPaneCreatedEmpty");
		
		tiffViewPaneCreatedWithName.setTiffImageIcon (tiffImageIcon);
		performChecks (tiffViewPaneCreatedWithName, "tiffViewPaneCreatedWithName");
	}

	/**
	 * Test of getTiffImageIcon method, of class TiffViewPane.
	 */
	@Test
	public void testGetTiffImageIcon () {
		System.out.println ("getTiffImageIcon ()");
		
		tiffViewPaneCreatedEmpty.setTiffImageIcon (tiffImageIcon);
		assertEquals (tiffViewPaneCreatedEmpty.getTiffImageIcon (), tiffImageIcon);
		tiffViewPaneCreatedWithName.setTiffImageIcon (tiffImageIcon);
		assertEquals (tiffViewPaneCreatedWithName.getTiffImageIcon (), tiffImageIcon);
	}
}
