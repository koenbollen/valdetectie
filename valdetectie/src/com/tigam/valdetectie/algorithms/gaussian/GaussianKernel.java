package com.tigam.valdetectie.algorithms.gaussian;

import java.util.Comparator;

public class GaussianKernel
{
	public static final double INITIAL_DEVIATION = 128.0;
	public static final double DEFAULT_ALPHA = 1.0/1000;
	public static final double DEFAULT_BIAS = .01;
	
	public static final Comparator<GaussianKernel> WEIGHTCOMPARATOR = new Comparator<GaussianKernel>()
	{
		@Override
		public int compare(GaussianKernel o1, GaussianKernel o2)
		{
			if( o1 == null )
				return 1;
			if( o2 == null )
				return -1;
			if (o1.weight == o2.weight)
				return 0;
			return (o1.weight - o2.weight>0)?-1:1;
		}
	};

	private final double alpha;
	private final double bias;
	private double weight;
	private double mean;
	private double variance;

	public GaussianKernel( double value )
	{
		this(value, DEFAULT_ALPHA );
	}

	public GaussianKernel( double value, double alpha )
	{
		this(value, alpha, DEFAULT_BIAS );
	}

	public GaussianKernel( double value, double alpha, double bias )
	{
		this.alpha = alpha;
		this.bias = bias;
		this.weight = 0.0;
		this.mean = value;
		this.variance = INITIAL_DEVIATION;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "GaussianKernel [weight=" + weight + ", mean=" + mean
				+ ", variance=" + variance + "]";
	}

	public void update( double value )
	{
		weight += alpha * ( 1 - weight - bias );
		double delta = value - mean;
		mean += alpha / weight * delta;
		//mean = mean * (1.0-alpha) + alpha * delta;
		variance += alpha / weight * (delta*delta-variance);
	}
	
	public void reduce()
	{
		weight -= alpha * ( weight + bias );
	}
	
	public boolean contains( double value, int mahalanobis )
	{
		double delta = mean-value;
		return ( ( delta * delta ) <= (variance * mahalanobis) ); 
	}

	public double getWeight()
	{
		return weight;
	}

	public double getMean()
	{
		return mean;
	}

	public double getVariance()
	{
		return variance;
	}

	public void normalize(double total)
	{
		if( total == 0.0 )
			this.weight = 1;
		else
			this.weight = this.weight / total;
	}

}
