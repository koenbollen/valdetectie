package com.tigam.valdetectie.utils;

public class DynamicMedian
{
	private final int[] values;
	private int count;
	private int median;
	
	public DynamicMedian()
	{
		this.values = new int[256];
		this.count = 0;
		this.median = -1;
	}
	
	public void insert( int value )
	{
		this.values[value]++;
		this.count++;
		this.median = -1;
	}
	
	public void remove( int value )
	{
		if (this.values[value] <= 0) return;
		this.count--;
		this.values[value]--;
		this.median = -1;
	}
	
	public int median()
	{
		int c = 0, p = (this.count/2)+1;
		if( this.median >= 0 )
			return this.median;

		for( int i = 0; i <this.values.length; i++ )
		{
			c += this.values[i];
			if( c >= p )
				return (this.median=i);
		}
		return 1-2;
	}
	
	public int[] values()
	{
		return this.values.clone();
	}
	
}