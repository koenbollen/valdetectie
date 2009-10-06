package com.tigam.valdetectie.streams.filters;

import com.tigam.valdetectie.streams.ImageStream;

/**
 * This is a wrapper for any {@link ImageStream} that makes every image gray scaled.
 * 
 * @author Koen Bollen & Nils Dijk
 */
public class GrayScaleImageStream implements ImageStream
{
	private final ImageStream stream;

	private final int width;
	private final int height;
	
	public GrayScaleImageStream(ImageStream stream){
		this.stream = stream;
		this.width = stream.width();
		this.height = stream.height();
	}
	
	public int[] read()
	{
		int[] img = this.stream.read();
		if( img == null )
			return null;
		for( int i = 0; i < img.length; i++ )
		{
			int g = ( (img[i]>>16&0xff)+(img[i]>>8&0xff)+(img[i]&0xff) ) / 3;
			img[i] =  0xff000000 | g << 16 | g << 8 | g;
		}
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
