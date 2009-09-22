package com.tigam.valdetectie.streams;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * This is a wrapper for any {@link ImageStream} that makes every image mirrored.
 * 
 * @since 23 September, 2009
 * @author Koen Bollen
 */
public class MirrorImageStream implements ImageStream
{
	private final ImageStream stream;
	private BufferedImage buffer;
	private Graphics2D graphics;
	
	public MirrorImageStream(ImageStream stream){
		this.stream = stream;
		this.buffer = null;
		this.graphics = null;
	}
	
	/**
	 * Return the next image from the wrapped ImageStream but then mirrored.
	 * 
	 * @return The next image.
	 */
	@Override
	public Image read()
	{
		Image img = this.stream.read();
		if (img == null)
		{
			if( this.graphics != null )
				this.graphics.dispose();
			this.buffer = null;
			this.graphics = null;
			return null;
		}

		if( this.buffer == null )
		{
			int w = img.getWidth( null );
			int h = img.getHeight( null );
			this.buffer = new BufferedImage( w, h, BufferedImage.TYPE_4BYTE_ABGR );
			this.graphics = (Graphics2D)this.buffer.getGraphics();
			this.graphics.translate( w/2, 0 );
			this.graphics.scale(-1,1);
			this.graphics.translate( -(w/2), 0 );
		}
		
		this.graphics.drawImage( img, 0, 0, null );
		
		return this.buffer;
	}

}
