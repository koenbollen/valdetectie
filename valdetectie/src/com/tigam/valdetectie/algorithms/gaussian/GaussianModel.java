package com.tigam.valdetectie.algorithms.gaussian;

public class GaussianModel
{

	private final int width;
	private final int height;
	private GaussianMixture[] mixtures;

	public GaussianModel( int width, int height )
	{
		this( width, height, 4 );
	}
	
	public GaussianModel( int width, int height, int number_of_kernels )
	{
		this.width = width;
		this.height = height;
		this.mixtures = new GaussianMixture[width*height];
		for( int i = 0; i < this.mixtures.length; i++ )
			this.mixtures[i] = new GaussianMixture( number_of_kernels );
	}
	
	public void update( int[] data )
	{
		if( data.length != this.mixtures.length )
			throw new RuntimeException( "given data's length differs" );
		for( int i = 0; i < this.mixtures.length; i++ )
			mixtures[i].update(data[i]&0xff);
	}
	
	public int[] getDebugImage()
	{
		int[] data = new int[this.mixtures.length];
		for( int i = 0; i < this.mixtures.length; i++ )
			data[i] = (int)mixtures[i].getHeavy();
		return data;
	}
	
}
