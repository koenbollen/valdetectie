package com.tigam.valdetectie.utils;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This is a simple JFrame that display's one image. The image can be changed/updated with
 * the {@link #setImage(Image)} method.
 * 
 * @author Koen Bollen
 */
public class Imager extends JFrame
{
	private static final long serialVersionUID = -4362164667953944983L;
	
	private ImageIcon icon;
	private JLabel label;
	public Imager()
	{
		setTitle("Imager");
		this.icon = new ImageIcon();
		this.label = new JLabel( this.icon );
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		getContentPane().add(label);
		pack();
		
	}
	public void setImage( Image image )
	{
		this.icon.setImage(image);
		this.label.repaint();
		pack();
	}
}
