package com.tigam.valdetectie.algorithms;
import static java.lang.Math.max;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.tigam.valdetectie.utils.CyclicArray;
import com.tigam.valdetectie.utils.DynamicMedian;
import com.tigam.valdetectie.utils.MeanDeviation;
import com.tigam.valdetectie.utils.Utils;

public class BackgroundModel
{
	private final int width;
	private final int height;
	
	private CyclicArray<byte[][]> history;
	private DynamicMedian[][] median;
	private MeanDeviation[][] diviation;

	// The model:
	private int[] m;
	private int[] n;
	private int[] d;
	
	public BackgroundModel( int width, int height )
	{
		this(width, height, 100);
	}
	
	public BackgroundModel( int width, int height, int period )
	{
		this.width = max(1,width);
		this.height = max(1,height);
		
		period = max(1, period%2==0 ? period-1 : period ); 
		history = new CyclicArray<byte[][]>( period );

		this.median = new DynamicMedian[width][height];
		for( int x = 0; x < this.width; x++ )
			for( int y = 0; y < this.height; y++ )
				this.median[x][y] = new DynamicMedian();
		
		this.diviation = new MeanDeviation[width][height];
		for( int x = 0; x < this.width; x++ )
			for( int y = 0; y < this.height; y++ )
				this.diviation[x][y] = new MeanDeviation();

		// Initialize the Model:
		this.m = new int[this.width*this.height];
		this.n = new int[this.width*this.height];
		this.d = new int[this.width*this.height];
	}
	
	public Image pushImage( Image image )
	{
		int data[] = Utils.image2data(image);
		byte frame[][] = new byte[this.width][this.height];
		for( int i = 0; i < data.length; i++ )
			frame[i%this.width][i/this.width] = (byte)(data[i] & 0xFF);
		byte[][] old = this.history.insert(frame);		
		return update(frame, old);
		
	}

	private Image update( byte[][] frame, byte[][] fold )
	{
		//int size = this.history.size();
		int pixel;//, index, last;
		DynamicMedian dm;
		MeanDeviation md;
		//double median;
		//double mean;
		double sd;
		
		BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		for( int x = 0; x < this.width; x++ )
		{
			for( int y = 0; y < this.height; y++ )
			{
				pixel = frame[x][y] & 0xFF;
				
				// Calculate Median:
				dm = this.median[x][y];
				if( fold != null )
					dm.remove(fold[x][y] & 0xFF);
				dm.insert(pixel);
				//median = dm.median(); // Enable if needed.
				
				// Calculate Mean and Standard Deviation:
		        md = this.diviation[x][y];
				if( fold != null )
					md.remove(fold[x][y] & 0xFF);
				md.insert( pixel );
				//mean = md.mean(); // Enable if needed.
				sd = md.deviation();
				
//				last = 1;
//				index = x*this.height+y;
//				for( int k = 1; k < size; k++ )
//				{
//					pixel = this.history.get(k)[x][y] & 0xFF;
//					if( !( pixel - median <= 2 * sd )) 
//						continue;
//					m[index] = min( m[index], pixel );
//					n[index] = max( n[index], pixel );
//					d[index] = max( d[index], pixel-this.history.get(k-1)[x][y] );
//					last = k;
//				}
//				
//				int value =(int) (m[index]+n[index]+d[index])/2;
				int value = (int)sd;
				int rgb = (int)(0xFF000000 | (value&0xff) | ((value&0xff) << 8) | ((value&0xff) << 16));
				bi.setRGB(x, y, rgb);
				
			}
		}
		return bi;
	}
	
}
