package com.tigam.valdetectie.utils;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Utils class, contains some nifty static methods.
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

}
