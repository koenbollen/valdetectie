package com.tigam.valdetectie.streams.filters;

/**
 * A filter to invert the color of an image 
 *
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class InvertFilter implements ImageFilter
{
	
	public static final InvertFilter instance = new InvertFilter();

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( img == null )
			return null;
		
		int [] res = new int[img.length];
		for( int i = 0; i < img.length; i++ )
			res[i] = img[i]^0x00FFFFFF;
		
		return res;
	}

}
