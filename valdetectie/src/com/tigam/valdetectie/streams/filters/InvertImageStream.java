package com.tigam.valdetectie.streams.filters;

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

	private final int width;
	private final int height;
	
	/**
	 * Create a wrapper for an {@link ImageStream} that inverts it.
	 * @param stream
	 */
	public InvertImageStream( ImageStream stream )
	{
		this.stream = stream;
		this.width = stream.width();
		this.height = stream.height();
	}
	
	/**
	 * Return the next image from the wrapped ImageStream but color inverted.
	 * 
	 * @return The next image.
	 */
	@Override
	public int[] read()
	{
		int[] img = this.stream.read();
		if( img == null )
			return null;
		for( int i = 0; i < img.length; i++ )
			img[i] ^= 0x00FFFFFF;
		return img;
	}

	@Override
	public int width()
	{
		return this.width;
	}
	
	@Override
	public int height()
	{
		return this.height;
	}
}
