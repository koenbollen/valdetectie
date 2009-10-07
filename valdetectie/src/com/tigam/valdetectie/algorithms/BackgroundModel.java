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

	public BackgroundModel(int width, int height)
	{
		this(width, height, 100);
	}

	public BackgroundModel(int width, int height, int period)
	{
		this.width = width;
		this.height = height;
		this.length = width * height;

		period = max(1, period % 2 == 0 ? period - 1 : period);
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


	public void pushImage(int[] frame)
	{
		int[] old = ph.insert(frame);// this.history.insert(frame);

		for( int i = 0; i < this.length; i++ )
		{
			int pixel;
			pixel = frame[i] & 0xFF;

			// Calculate Median:
			if( old != null )
				this.median[i].remove(old[i] & 0xFF);
			this.median[i].insert(pixel);

			// Calculate Mean and Standard Deviation:
			if( old != null )
				this.diviation[i].remove(old[i] & 0xFF);
			this.diviation[i].insert(pixel);
		}
	}
	
	public int[] getDeviationImage()
	{
		int[] buffer = new int[this.length];
		for( int i = 0; i < this.length; i++ )
		{
			buffer[i] = (int) this.diviation[i].deviation();
			buffer[i] = 0xFF000000 | buffer[i] << 16 | buffer[i] << 8 | buffer[i];
		}
		return buffer;
	}
	
	public void refreshBackgroundModel(){
		Arrays.fill(this.m, Integer.MAX_VALUE);
		Arrays.fill(this.n, Integer.MIN_VALUE);
		Arrays.fill(this.d, Integer.MIN_VALUE);
		
		int size = ph.size();
		for( int i = 0; i < this.length; i++ )
		{
			int[] pixelHistory = ph.getPixelHistory(i);

			double median = this.median[i].median();
			double dvs = this.diviation[i].deviationSquared();

			for( int k = 1; k < size; k++ )
			{
				int pixelData = pixelHistory[k] & 0xFF;

				double q = pixelData - median;
				q /= 2;
				q = q * q;

				if( q <= dvs ){
					m[i] = min(m[i], pixelData);
					n[i] = max(n[i], pixelData);
					d[i] = max(d[i], Math.abs(pixelData	- (pixelHistory[k - 1] & 0xFF)));
				}
			}
		}
	}
	
	public int[] getBackgroundImage(){
		int [] image = new int[length];
		for (int i=0; i<length; i++){
			int pix = (m[i]+n[i])/2;
			image[i] = 0xFF000000 | pix << 16 | pix << 8 | pix;
		}
		return image;
	}
	
	public int[] getForeground(int [] image){
		if (image.length != length) throw new RuntimeException("size of imagge does not fit the size of the background");
		int back = 0xFF000000;
		int fore = 0xFFFFFFFF;
		
		int [] foreground = new int[length];
		
		Arrays.fill(foreground, fore);
		
		DynamicMedian median = new DynamicMedian();
		for (int t:d){
			try {
				median.insert(t);
			} catch (IndexOutOfBoundsException ball) {}
		}
		
		int k = 2;
		int u = median.median();
		
		for (int i=0; i<length; i++){
			int pix = image[i]&0xFF;
			if (pix > (m[i]-k*u) && pix < (n[i]+k*u)) foreground[i] = back;
		}
		
		return foreground;
	}

}
