package com.tigam.valdetectie.algorithms;
import static java.lang.Math.max;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.tigam.valdetectie.utils.CyclicArray;
import com.tigam.valdetectie.utils.DynamicMedian;
import com.tigam.valdetectie.utils.Utils;

public class BackgroundModel
{
	private final int width;
	private final int height;
	
	private CyclicArray<byte[][]> history;
	private DynamicMedian[][] median;
	
	private short[][] deviation;
	
	private byte[][][] model;
	
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
		int pixel;
		DynamicMedian m;
		//int size = this.history.size();
		
		BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		for( int x = 0; x < this.width; x++ )
		{
			for( int y = 0; y < this.height; y++ )
			{
				pixel = frame[x][y] & 0xFF;
				
				// Calculate Median:
				m = this.median[x][y];
				if( fold != null )
					m.remove(fold[x][y] & 0xFF);
				m.insert(pixel);
				
				
				
				int median = m.median();
				int rgb = (int)(0xFF000000 | (median&0xff) | ((median&0xff) << 8) | ((median&0xff) << 16));
				bi.setRGB(x, y, rgb);
				
			}
		}
		return bi;
	}
	
}
