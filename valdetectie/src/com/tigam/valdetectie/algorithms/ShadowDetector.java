package com.tigam.valdetectie.algorithms;

import com.tigam.valdetectie.utils.MeanDeviation;

/**
 * Based on the Shadow Detection from the paper (section 3.2): 
 * Background Substraction and Shadow Detection in Grayscale Video Sequences 
 * by Julio Cezar Silveira Jacques Jr, Claudio Rosito Jung and Soraia Raupp Musse
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class ShadowDetector
{
	public static final int N = 4;
	public static final int M = 1;
	public static double Lncc = 0.98;
	public static double Lstd = 0.001;
	public static double Llow = 0.503;
	
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
	
	/**
	 * 
	 * @param img
	 * @param bg
	 * @param i
	 * @param j
	 * @return the squared deviation of the neighbors of i and j with the size of {@link ShadowDetector#M}
	 */
	private double stds( int[] img, int[] bg, int i, int j)
	{
		double _i, b;
		MeanDeviation md = new MeanDeviation();
		for( int n = -M; n <= M; n++ )
		{
			for( int m = -M; m <= M; m++ )
			{
				_i = neighbor(img, i, j, n, m);
				b = neighbor(img, i, j, n, m);
				if( b == 0 )
					md.insert(0);
				else
					md.insert( _i/b );
			}
		}
		return md.deviationSquared();
	}
	
	private boolean secondPass( int[] img, int[] bg, int index )
	{
		double r = (double)(img[index]&0xff) / (double)(bg[index]&0xff);
		double std = stds(img, bg, index%width, index/width);
		return std < Lstd*Lstd && Llow <= r && r < 1;
	}
	
	private boolean isShadow( int[] img, int[] bg, int i, int j )
	{
		double er = er(img,bg,i,j);
		double eb = sn(bg,i,j);
		double et = sn(img,i,j);
		
		// first part of the statement below is the check if the ncc is higher than the L value
		// but this time without the root calculation which is a very expansive calculation
		return (1.0/((eb*et)/(er*er))) > Lncc*Lncc;// && et < eb;
	}
	
	private boolean isShadowOld( int[] img, int[] bg, int i, int j )
	{
//		System.out.println(ncc(img,bg,i,j));
		return ncc(img,bg,i,j) > Lncc && sn(img,i,j) < sn(bg,i,j);
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
			
			//*/
			if( isShadow( img, bg, i%width, i/width ) && secondPass(img,bg, i) )
			/*/
			if( isShadowOld( img, bg, i%width, i/width ) )
			//*/
				result[i] = ~0;
			
			
			/* remove/add the beginning slash to comment/uncomment the test of the isShadow optimalization
			//  in case you suspect an error in the optimalization
			if (isShadow( img, bg, i%width, i/width ) != isShadowOld( img, bg, i%width, i/width ))
				System.err.println("The optimalization of the isShadow methode returns a different value than the original one! FIX IT!");
			//*/
			
			
			//TODO: check if this block below is still needed or we can remove it
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
