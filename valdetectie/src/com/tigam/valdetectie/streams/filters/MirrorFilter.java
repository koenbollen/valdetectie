package com.tigam.valdetectie.streams.filters;

/**
 * Filter to mirror the image from left to right
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
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
