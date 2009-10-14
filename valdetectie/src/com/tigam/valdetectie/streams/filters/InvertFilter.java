package com.tigam.valdetectie.streams.filters;

/**
 * A filter to invert the color of an image 
 * @author nils
 *
 */
public class InvertFilter implements ImageFilter
{
	
	public static final InvertFilter instance = new InvertFilter();

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( img == null )
			return null;
		for( int i = 0; i < img.length; i++ )
			img[i] ^= 0x00FFFFFF;
		return img;
	}

}
