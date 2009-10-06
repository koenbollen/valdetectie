package com.tigam.valdetectie.algorithms;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;

import com.tigam.valdetectie.utils.DynamicMedian;
import com.tigam.valdetectie.utils.MeanDeviation;
import com.tigam.valdetectie.utils.PixelHistory;

public class BackgroundModel
{
	private final int width;
	private final int height;
	private final int length;
	
	private PixelHistory ph;
	
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
		ph = new PixelHistory(period, this.length);

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
		int[] old = ph.insert(frame);//this.history.insert(frame);		
		return update(frame, old);
		
	}

	private int[] update( int[] data, int[] fold )
	{
		
		int[] buffer = new int[this.length];

		Arrays.fill(this.m, Integer.MAX_VALUE);
		Arrays.fill(this.n, Integer.MIN_VALUE);
		Arrays.fill(this.d, Integer.MIN_VALUE);

		for( int i = 0; i < this.length; i++ )
		{
			int pixel;
			pixel = data[i] & 0xFF;
			
			// Calculate Median:
			if( fold != null )
				this.median[i].remove(fold[i] & 0xFF);
			this.median[i].insert(pixel);
			
			// Calculate Mean and Standard Deviation:
			if( fold != null )
				this.diviation[i].remove(fold[i] & 0xFF);
			this.diviation[i].insert( pixel );

			buffer[i] = (int)this.diviation[i].deviation();
			buffer[i] = 0xFF000000 | buffer[i] << 16 | buffer[i] << 8 | buffer[i];
		}
		
		for( int i = 0; i < this.length; i++ )
		{
			int [] pixelHistory = ph.getPixelHistory(i);
			for( int k = 1; k < pixelHistory.length; k++ )
			{
				int pixelData = pixelHistory[k] & 0xFF;

				double q = pixelData - this.median[i].median();
				q /= 2.0;
				q = q*q;
				
				if( q <= this.diviation[i].deviationSquared() )
				{
					m[i] = min( m[i], pixelData );
					n[i] = max( n[i], pixelData );
					d[i] = max( d[i], Math.abs(pixelData-(pixelHistory[k-1]&0xFF)) );
				}
			}
		}		
		return buffer;
	}
	
}
