package com.tigam.valdetectie.algorithms.gaussian;

import java.util.Arrays;

public class GaussianMixture
{
	public static final int MAHALANOBIS = 2;

	private final int number_of_kernels;

	private final double alpha;

	private GaussianKernel[] kernels;

	public GaussianMixture(int number_of_kernels)
	{
		this(number_of_kernels, GaussianKernel.DEFAULT_ALPHA);
	}

	public GaussianMixture(int number_of_kernels, double alpha)
	{
		this.number_of_kernels = number_of_kernels;
		this.alpha = alpha;
		this.kernels = new GaussianKernel[this.number_of_kernels];
	}

	public void update(double value)
	{
		boolean placed = false;
		for( int i = 0; i < kernels.length; i++ )
		{
			if( kernels[i] == null )
				break;
			if( !placed && kernels[i].contains(value, MAHALANOBIS) )
			{
				kernels[i].update(value);
				placed = true;
			} else
			{
				kernels[i].reduce();
			}
			if( kernels[i].getWeight() < 0 )
				kernels[i] = null;
		}
		if( !placed )
		{
			int index;
			for( index = 0; index < kernels.length; index++ )
				if( kernels[index] == null )
					break;
			kernels[Math.min(index, kernels.length - 1)] = new GaussianKernel(
					value, this.alpha);
		}
		Arrays.sort(kernels, GaussianKernel.WEIGHTCOMPARATOR);
		normalizeWeight();
	}

	private void normalizeWeight()
	{
		double total = 0;
		for( int i = 0; i < kernels.length; i++ )
			if( kernels[i] != null )
				total += kernels[i].getWeight();
		for( int i = 0; i < kernels.length; i++ )
			if( kernels[i] != null )
				kernels[i].normalize(total);
	}

	public double getWeight(double value)
	{
		for( int i = 0; i < kernels.length; i++ )
			if( kernels[i] != null && kernels[i].contains(value, MAHALANOBIS) )
				return kernels[i].getWeight();
		return 0;
	}

	public double getMeanAboveThreshold( double threshold )
	{
		double total = 0;
		double c = 0;
		for( int i = 0; i < kernels.length; i++ )
		{
			if( kernels[i] != null && kernels[i].getWeight() > threshold)
			{
				total += kernels[i].getMean()*kernels[i].getWeight();
				c += kernels[i].getWeight();
			}
		}
		return total/c;
	}
	
	@Deprecated
	public int getKernelCount()
	{
		int c = 0;
		for( int i = 0; i < kernels.length; i++ )
			if( kernels[i] != null )
				c++;
		return c;
	}

	@Deprecated
	public double getDominantMean()
	{
		if( kernels[0] == null )
			return 0;
		return kernels[0].getMean();
	}
}
