package com.tigam.valdetectie.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Utilities class, contains some nifty static methods.
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class Utils
{

	/**
	 * Show's an image in a dialog and blocks the program. Method returns when
	 * the dialog is closed.
	 * 
	 * @param image The image to display
	 */
	public static void showImage(Image image)
	{
		showImage(image, true);
	}
	
	/**
	 * Show's an image in a dialog and blocks the program. Method returns when
	 * the dialog is closed.
	 * 
	 * @param image The image to display
	 * @param modal If true this method is blocking.
	 */
	public static void showImage(Image image, boolean modal)
	{
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		JDialog frame = new JDialog();
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setModal(modal);
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}

	public static int[] image2data( Image image )
	{
		BufferedImage bi;
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		if (!(image instanceof BufferedImage) || ((BufferedImage)image).getType() != BufferedImage.TYPE_INT_RGB)
		{
		    bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		    Graphics g = bi.createGraphics();
		    g.drawImage(image, 0, 0, null);
		    g.dispose();
		}
		else
		{
			bi = (BufferedImage)image;
		}
		return bi.getRGB(0, 0, w, h, null, 0, w);
	}

	public static BufferedImage data2image( int[] data, int width, int height )
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, width, height, data, 0, width);
		return img;
	}
	
	/**
	 * Flash the screen for `millis' milliseconds with given color.
	 * 
	 * @author Koen Bollen
	 * @param millis Number of milliseconds to so the flashscreen.
	 * @param color The color that the screen should flash.
	 */
	public static void flash( long millis, Color color )
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();

		Frame frame = new Frame(gs.getDefaultConfiguration());
		Window win = new Window(frame); 
		win.setBackground( color );

		try {
			gs.setFullScreenWindow(win);
			win.validate(); 
			Thread.sleep(millis);
		} catch( InterruptedException e ) {
		} finally {
			gs.setFullScreenWindow(null);
		}
		win.dispose();
		frame.dispose();
	}
	
	/**
	 * Flash the screen for `millis' milliseconds with default color red.
	 * 
	 * @author Koen Bollen
	 * @param millis Number of milliseconds to so the flashscreen.
	 */
	public static void flash( long millis )
	{
		flash( millis, Color.RED );
	}
	
	/**
	 * Flash the screen for 300 milliseconds with default color red.
	 * 
	 * @author Koen Bollen
	 */
	public static void flash()
	{
		flash( 300 );
	}
	
	public static <T> T sortedFirst(Iterable<T> e, Comparator<T> c){
		T max = null;
		for (T t:e)
			if (max == null || c.compare(t, max) > 0)
				max = t;
		return max;
	}
	
}