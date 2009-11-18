package com.tigam.valdetectie.algorithms;


public class ShadowDetector
{
	public static final int N = 4;
	public static final double L = 0.96;
	
	private final int width;
	private final int height;
	
	public ShadowDetector( int width, int height )
	{
		this.width = width;
		this.height = height;
	}
	
	private double neighbor(int[] img, int i, int j, int n, int m )
	{
		int x = i+n;
		int y = j+m;
		
		// range check
		if (x < 0 || x >= width || y < 0 || y >= height )
			return 0;
		
		/*/
		return (img[x+y*width]&0xff);
		/*/
		return (img[x+y*width]&0xff)/255.0;
		//*/
	}
	
	private double er( int[] img, int[] bg, int i, int j )
	{
		double result = 0;
		for( int n = -N; n <= N; n++ )
		{
			for( int m = -N; m <= N; m++ )
			{
				result += neighbor(bg, i, j, n, m) * neighbor(img, i, j, n, m);
			}
		}
		return result;
	}

	private double rsn( int[] img, int i, int j )
	{
		return Math.sqrt( sn( img, i, j ) );
	}
	
	private double sn( int[] img, int i, int j )
	{
		double p, result = 0;
		for( int n = -N; n <= N; n++ )
		{
			for( int m = -N; m <= N; m++ )
			{
				p = neighbor(img, i, j, n, m);
				result += p * p;
			}
		}
		return result;
	}
	
	private double ncc( int[] img, int[] bg, int i, int j )
	{
		//System.out.println(rsn(bg,i,j)+ " " + rsn(img,i,j));
		return er(img,bg,i,j) / Math.sqrt( sn(bg,i,j) * sn(img,i,j) );
	}
	
	private boolean isShadow( int[] img, int[] bg, int i, int j )
	{
		//System.out.println(ncc(img,bg,i,j));
		return ncc(img,bg,i,j) > L && sn(img,i,j) < sn(bg,i,j);
	}
	
	public int[] shadow( int[] img, int[] bg, int[] fg  )  
	{
		if( img.length != width*height || img.length != bg.length || bg.length != fg.length )
			throw new IllegalArgumentException("Image sizes aren't the same.");
		
		int result[] = new int[img.length];
		
		for( int i = 0; i < fg.length; i++ )
		{
			// Only filter foreground pixels:
			if( fg[i] == 0 )
				continue;
			if( isShadow( img, bg, i%width, i/width ) )
				result[i] = ~0;
			/*
			result[i] = (int)(ncc(img,bg,i%width, i/width)*0xff);
			if( result[i] == 0xff )
				result[i] = 0xff0000;
			if( result[i] > L*0xff )
				result[i] = 0x0000ff;
			else
				result[i] |= result[i]<<16 | result[i]<<8;
			//else
			//	System.out.println("shadow found! o/");
			 * */
			
		}
		return result;
	}
}
