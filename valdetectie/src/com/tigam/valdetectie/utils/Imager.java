package com.tigam.valdetectie.utils;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This is a simple JFrame that display's one image. The image can be changed/updated with
 * the {@link #setImage(Image)} method.
 * 
 * @author Koen Bollen
 * @deprecated Use {@link ImageDisplay}
 */
@Deprecated
public class Imager extends JFrame
{
	private static final long serialVersionUID = -4362164667953944983L;
	
	private ImageIcon icon;
	private JLabel label;
	
	public Imager()
	{
		setTitle("Imager");
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		setMinimumSize( new Dimension( 100, 100 ) );
		addKeyListener( new KeyListener() {
			public void keyReleased( KeyEvent e ) {}
			public void keyPressed( KeyEvent e ) {}
			public void keyTyped( KeyEvent e ) {
				if( e.getKeyChar() == KeyEvent.VK_ESCAPE )
					System.exit(0);
			}
		});

		this.icon = new ImageIcon();
		this.label = new JLabel( this.icon );
		getContentPane().add(label);
	}
	
	/**
	 * Update the image of this frame.
	 * @param image The image to update to.
	 */
	public void setImage( Image image )
	{
		if( image == null )
		{
			this.label.setIcon( null );
			this.label.setText( "eof" );
			return;
		}
		this.icon.setImage(image);
		this.label.repaint();
		pack();
	}
}
