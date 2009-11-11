package com.tigam.valdetectie.algorithms.gaussian;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

public class GaussianMixture
{
	public static final int MAHALANOBIS = 2;
	
	private final int number_of_kernels;
	private GaussianKernel[] kernels;
	
	public GaussianMixture( int number_of_kernels )
	{
		this.number_of_kernels = number_of_kernels;
		this.kernels = new GaussianKernel[this.number_of_kernels];
	}
	
	public void update( double value )
	{
		boolean placed = false;
		for( GaussianKernel kernel : kernels )
		{
			if( kernel == null )
				break;
			if( !placed && kernel.contains( value, MAHALANOBIS ) )
			{
				kernel.update(value);
				placed = true;
			}
			else
			{
				kernel.reduce();
				// TODO: Remove kernels with a weight <= 0. 
			}
		}
		if( !placed )
		{
			kernels[kernels.length-1] = new GaussianKernel(value);
		}
		Arrays.sort(kernels,GaussianKernel.WEIGHTCOMPARATOR);
	}
	
	public double getHeavy()
	{
		if( kernels[0] == null )
			return 0;
		return kernels[0].getMean();
	}
}
