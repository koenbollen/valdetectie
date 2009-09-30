package com.tigam.valdetectie.streams.filters;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;

import com.tigam.valdetectie.streams.ImageStream;

/**
 * InvertImageStream.
 * Wraps an {@link ImageStream} with a inverted stream, that'll return each image inverted (also known as negative).
 *
 * @since 23 September, 2009
 * @author Koen Bollen
 */
public class InvertImageStream implements ImageStream
{
	private final ImageStream stream;
	private final LookupOp lop;

	/**
	 * Create a wrapper for an {@link ImageStream} that inverts it.
	 * @param stream
	 */
	public InvertImageStream( ImageStream stream )
	{
		this.stream = stream;
		byte[] bytes = new byte[256];
	    for( int i = 0; i < 256; i++ )
	    	bytes[i] = (byte)( 255 - i );
		lop = new LookupOp( new ByteLookupTable( 0, bytes ), null );
	}
	
	/**
	 * Return the next image from the wrapped ImageStream but color inverted.
	 * 
	 * @return The next image.
	 */
	@Override
	public Image read()
	{
		BufferedImage img = (BufferedImage)this.stream.read();
		if( img == null )
			return null;
		lop.filter( img, img );		
		return img;
	}

}
