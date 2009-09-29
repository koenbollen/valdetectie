package com.tigam.valdetectie.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Utilities class, contains some nifty static methods.
 * 
 * @author Koen Bollen
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
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		JDialog frame = new JDialog();
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setModal(true);
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
}
