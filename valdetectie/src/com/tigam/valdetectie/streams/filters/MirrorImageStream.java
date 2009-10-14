package com.tigam.valdetectie.streams.filters;

import com.tigam.valdetectie.streams.ImageStream;

/**
 * This is a wrapper for any {@link ImageStream} that makes every image mirrored.
 * 
 * @since 23 September, 2009
 * @author Koen Bollen
 */
@Deprecated
public class MirrorImageStream implements ImageStream
{
	private final ImageStream stream;

	private final int width;
	private final int height;
	
	public MirrorImageStream(ImageStream stream){
		this.stream = stream;
		this.width = stream.width();
		this.height = stream.height();
	}
	
	/**
	 * Return the next image from the wrapped ImageStream but then mirrored.
	 * 
	 * @return The next image.
	 */
	@Override
	public int[] read()
	{
		int[] orig = this.stream.read();
		if( orig == null )
			return null;
		int[] img = new int[orig.length];
		for( int y = 0; y < this.height; y++ )
			for( int x = 0; x < this.width; x++ )
				img[ y * this.width + this.width-x-1 ] = orig[ y * this.width + x ];
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
