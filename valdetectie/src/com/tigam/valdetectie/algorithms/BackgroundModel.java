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
	private final int length;
	
	private CyclicArray<int[]> history;
	private DynamicMedian[] median;
	private MeanDeviation[] diviation;

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
		this.width = width;
		this.height = height;
		this.length = width*height;
		
		period = max(1, period%2==0 ? period-1 : period ); 
		history = new CyclicArray<int[]>( period );

		this.median = new DynamicMedian[this.length];
		this.diviation = new MeanDeviation[this.length];
		for( int i = 0; i < this.length; i++ )
		{
			this.median[i] = new DynamicMedian();
			this.diviation[i] = new MeanDeviation();
		}
		
		// Initialize the Model:
		this.m = new int[this.length];
		this.n = new int[this.length];
		this.d = new int[this.length];
	}
	
	public int[] pushImage( int[] frame )
	{
		int[] old = this.history.insert(frame);		
		return update(frame, old);
		
	}

	private int[] update( int[] frame, int[] fold )
	{
		int pixel;//, index, last;
		
		int[] buffer = new int[this.length];

		for( int i = 0; i < this.length; i++ )
		{
			pixel = frame[i] & 0xFF;
			
			// Calculate Median:
			if( fold != null )
				this.median[i].remove(fold[i] & 0xFF);
			this.median[i].insert(pixel);
			
			// Calculate Mean and Standard Deviation:
			if( fold != null )
				this.diviation[i].remove(fold[i] & 0xFF);
			this.diviation[i].insert( pixel );
			
//			last = 1;
//			index = x*this.height+y;
//			for( int k = 1; k < size; k++ )
//			{
//				pixel = this.history.get(k)[x][y] & 0xFF;
//				if( !( pixel - median <= 2 * sd )) 
//					continue;
//				m[index] = min( m[index], pixel );
//				n[index] = max( n[index], pixel );
//				d[index] = max( d[index], pixel-this.history.get(k-1)[x][y] );
//				last = k;
//			}
//			
//			int value =(int) (m[index]+n[index]+d[index])/2;
			
			buffer[i] = (int)this.diviation[i].deviation();
			buffer[i] = 0xFF000000 | buffer[i] << 16 | buffer[i] << 8 | buffer[i];
		}
		return buffer;
	}
	
}
