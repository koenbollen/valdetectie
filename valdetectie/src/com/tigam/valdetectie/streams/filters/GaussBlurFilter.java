package com.tigam.valdetectie.streams.filters;

import java.util.Arrays;

public class GaussBlurFilter implements ImageFilter
{	
	public static final GaussBlurFilter instance = new GaussBlurFilter();
	
	public double[] kernel;
	
	public GaussBlurFilter()
	{
	}
	
	private void initialize_kernel()
	{
		double[] k = {
				2, 4, 5, 4, 2,
		        4, 9,12, 9, 4,
		        5,12,15,12, 5,
		        4, 9,12, 9, 4,
		        2, 4, 5, 4, 2
			};
		this.kernel = new double[25];
		for( int i = 0; i < 25; i++ )
			this.kernel[i] = k[i]/159.0;
	}
	
	private double n( int[] img, int width, int i, int j, int n, int m )
	{
		int x = i+n;
		int y = j+m;

		if (x < 0 || x >= width || y < 0 || y >= img.length/width )
			return 0;
		
		try
		{
//			System.out.println((img[y*width+x]&0xff));
//			System.out.println(kernel[m*5+n]);
			return ((img[y*width+x]&0xff) * kernel[m*5+n]);
		}
		catch( IndexOutOfBoundsException e )
		{
			return 0;
		}
	}
	
	private double m( int[] img, int width, int i, int j )
	{
		double result = 0;
		
		for( int n = -2; n <= 2; n++ )
			for( int m = -2; m <= 2; m++ )
				result += n( img, width, i, j, n, m );
		
		return result;
	}

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( kernel == null )
			initialize_kernel();
		
		int[] res = new int[img.length];
		
		for( int i = 0; i < img.length; i++ )
		{
			res[i] = (int)Math.round( m(img, width, (i%width), (i/width) ) );
			res[i] |= res[i]<<16 | res[i]<<8;
		}
		
		return res;
	}

}
