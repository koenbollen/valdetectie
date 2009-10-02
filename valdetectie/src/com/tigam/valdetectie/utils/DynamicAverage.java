package com.tigam.valdetectie.utils;

/**
 * @deprecated Use {@link MeanDeviation} instead!
 */
public class DynamicAverage
{
	private double sum;
	private int count;
	
	public DynamicAverage()
	{
		this.sum = 0;
		this.count = 0;
	}
	
	public void insert( double value )
	{
		this.sum += value;
		this.count++;
	}
	
	public void remove( double value )
	{
		if( this.count <= 0 )
			return;
		this.sum -= value;
		this.count--;
	}
	
	public double average()
	{
		return this.sum/this.count;
	}
	
}
