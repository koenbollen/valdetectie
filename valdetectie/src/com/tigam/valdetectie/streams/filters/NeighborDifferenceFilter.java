package com.tigam.valdetectie.streams.filters;

import static java.lang.Math.*;

/**
 * WIP
 * @author koen
 *
 */
public class NeighborDifferenceFilter implements ImageFilter
{
	public final static NeighborDifferenceFilter instance = new NeighborDifferenceFilter();
	
	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if (img == null)
			return null;
		
		int x = img.length;
		int[] res = new int[x];
		while( x --> 0 )
		{
			if( x - width >= 0 )
				res[x] += abs(img[x]&0xff - img[x-width]&0xff);
			if( x + width < img.length )
				res[x] += abs(img[x]&0xff - img[x+width]&0xff);
			if( x % width != 0 ) // If you are the first column don't go:
				res[x] += abs(img[x]&0xff - img[x-1]&0xff);
			if( (x+1) % width != 0 ) // If you are the last column don't go.
				res[x] += abs(img[x]&0xff - img[x+1]&0xff);
			res[x] = res[x]<<16 | res[x]<<8 | res[x]<<0; 
		}
		return res;
	}

}
