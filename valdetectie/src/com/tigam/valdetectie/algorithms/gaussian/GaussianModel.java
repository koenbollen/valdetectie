package com.tigam.valdetectie.algorithms.gaussian;

import java.util.Arrays;

public class GaussianModel
{
	public static final double THRESHOLD = .025;
	
	private final int width;
	private final int height;
	private final int number_of_kernels;
	private final double alpha;
	private GaussianMixture[] mixtures;

	public GaussianModel( int width, int height )
	{
		this( width, height, 4 );
	}
	
	public GaussianModel( int width, int height, int number_of_kernels )
	{
		this( width, height, number_of_kernels, GaussianKernel.DEFAULT_ALPHA );
	}
	
	public GaussianModel( int width, int height, int number_of_kernels, double alpha )
	{
		this.width = width;
		this.height = height;
		this.number_of_kernels = number_of_kernels;
		this.alpha = alpha;
		this.mixtures = new GaussianMixture[width*height];
		for( int i = 0; i < this.mixtures.length; i++ )
			this.mixtures[i] = new GaussianMixture( number_of_kernels, alpha );
	}
	
	public void update( int[] data )
	{
		if( data.length != this.mixtures.length )
			throw new RuntimeException( "given data's length differs" );
		for( int i = 0; i < this.mixtures.length; i++ )
			mixtures[i].update(data[i]&0xff);
	}
	
	public int[] foreground( int[] data )
	{
		if( data.length != this.mixtures.length )
			throw new RuntimeException( "given data's length differs" );
		int[] image = new int[this.mixtures.length];
		for( int i = 0; i < data.length; i++ )
		{
			if(this.mixtures[i].getWeight(data[i]&0xff) < THRESHOLD )
				image[i] = 0xffffffff;
			else
				image[i] = 0;
		}
		return image;
	}

	@Deprecated
	public int[] getKernelCountImage()
	{
		int[] colors = new int[number_of_kernels+1];
		for( int i = 1; i < colors.length; i++ )
		{
			colors[i] = (int)((255.0/number_of_kernels-1) * (i-1));
			colors[i] = colors[i]<<16|colors[i]<<8|colors[i];
		}
		int[] data = new int[this.mixtures.length];
		for( int i = 0; i < this.mixtures.length; i++ )
			data[i] = colors[this.mixtures[i].getKernelCount()];
		return data;
	}

	@Deprecated
	public int[] getMeanImage()
	{
		int[] data = new int[this.mixtures.length];
		for( int i = 0; i < this.mixtures.length; i++ )
		{
			data[i] = (int)mixtures[i].getDominantMean();
			data[i] = data[i]<<16|data[i]<<8|data[i];
		}
		return data;
	}
	
}
