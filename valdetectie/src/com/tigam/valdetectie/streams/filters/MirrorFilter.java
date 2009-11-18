package com.tigam.valdetectie.streams.filters;

/**
 * Filter to mirror the image from left to right
 * 
 * @author nils
 *
 */
public class MirrorFilter implements ImageFilter
{
	public static final MirrorFilter instance = new MirrorFilter();
	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( img == null )
			return null;
		int[] res = new int[img.length];
		for( int y = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				res[ y * width + width-x-1 ] = img[ y * width + x ];
		return res;
	}

}
