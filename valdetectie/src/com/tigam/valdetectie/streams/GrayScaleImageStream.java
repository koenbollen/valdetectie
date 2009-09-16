package com.tigam.valdetectie.streams;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class GrayScaleImageStream implements ImageStream
{
	private final ImageStream stream;
	private BufferedImage image;
	private Graphics graphics;
	
	public GrayScaleImageStream(ImageStream stream){
		this.stream = stream;
		image = null;
		graphics = null;
	}
	
	public synchronized Image read()
	{
		Image img = this.stream.read();
		if (img == null){
			if( image != null )
				image = null;
			if( graphics != null )
			{
				graphics.dispose();
				graphics = null;
			}
			return null;
		}
		
		if (image == null) 
		{
			image = new BufferedImage( img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_GRAY );
			graphics = image.getGraphics();
		}
		
		graphics.drawImage(img, 0, 0, null);
		
		return image;
	}

}
