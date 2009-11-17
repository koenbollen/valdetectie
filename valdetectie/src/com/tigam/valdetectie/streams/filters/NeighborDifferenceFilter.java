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
		int x = img.length;
		int[] data = new int[x];
		while( x --> 0 )
		{
			if( x - width >= 0 )
				data[x] += abs(img[x]&0xff - img[x-width]&0xff);
			if( x + width < img.length )
				data[x] += abs(img[x]&0xff - img[x+width]&0xff);
			if( x % width != 0 ) // If you are the first column don't go:
				data[x] += abs(img[x]&0xff - img[x-1]&0xff);
			if( (x+1) % width != 0 ) // If you are the last column don't go.
				data[x] += abs(img[x]&0xff - img[x+1]&0xff);
			data[x] = data[x]<<16 | data[x]<<8 | data[x]<<0; 
		}
		return data;
	}

}
