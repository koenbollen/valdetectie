package com.tigam.valdetectie.utils;

import static java.lang.Math.sqrt;

/**
 * MeanDeviation.
 * 
 * This class is an online version of the standard deviation formula and has a build-in support for
 * calculating the mean/average. It's very fast, doesn't keep a list stored for all the values and
 * it does not recalulate the deviation everytime a new value is added re moved.
 *
 * @since Oct 2, 2009
 * @author Koen Bollen
 */
public class MeanDeviation
{
	private int sum;
	private int squared;
	private int count;
	
	/**
	 * Build a {@link MeanDeviation} instance with a pre given list.
	 * @param list The list to insert.
	 */
	public MeanDeviation(int ... list)
	{
		this();
		for( int i : list )
		{
			this.sum += i;
			this.squared += i*i;
		}
		this.count = list.length;
	}
	
	/**
	 * Create an empty {@link MeanDeviation} instance.
	 */
	public MeanDeviation()
	{
		this.sum = 0;
		this.squared = 0;
		this.count = 0;
	}
	
	/**
	 * This method inserts a new value to the online mean and deviation.
	 * 
	 * @param value The new value to insert.
	 */
	public void insert( int value )
	{
		this.sum += value;
		this.squared += value*value;
		this.count++;
	}
	
	/**
	 * This method removes a value from the online mean and deviation.
	 * 
	 * @param value The value to remove.
	 */
	public void remove( int value )
	{
		if( this.count <= 0 )
			return;
		this.sum -= value;
		this.squared -= value*value;
		this.count--;
	}
	
	/**
	 * This method calculates the deviation and returns it.
	 * 
	 * @return The sampled standard deviation.
	 */
	public double deviation()
	{
		return sqrt( (this.squared - ( this.sum * this.mean() )) / ( this.count -1 ) );
	}

	/**
	 * This method calculates the mean and returns it.
	 * 
	 * @return mean
	 */
	public double mean()
	{
		return this.sum / this.count;
	}
	
}