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
	public int[] applyFilter(int[] orig, int width, int height)
	{
		if( orig == null )
			return null;
		int[] img = new int[orig.length];
		for( int y = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				img[ y * width + width-x-1 ] = orig[ y * width + x ];
		return img;
	}

}
