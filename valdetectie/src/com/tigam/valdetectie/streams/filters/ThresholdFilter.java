package com.tigam.valdetectie.streams.filters;

/**
 * 
 * @author koen!
 */
public class ThresholdFilter implements ImageFilter
{
	private final int threshold;
	private final boolean binary;

	public ThresholdFilter( int threshold )
	{
		this(threshold, true );
	}
	
	public ThresholdFilter( int threshold, boolean binary )
	{
		this.threshold = threshold;
		this.binary = binary;
	}
	
	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		int[] res = new int[img.length];
		
		for( int i = 0; i < img.length; i++ )
			if( ((img[i]&0xff00)>>8) < threshold )
				res[i] = this.binary ? ~0 : img[i];
		
		return res;
	}

}
