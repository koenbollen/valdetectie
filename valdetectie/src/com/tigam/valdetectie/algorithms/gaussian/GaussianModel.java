package com.tigam.valdetectie.algorithms.gaussian;

/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class GaussianModel
{
	public static final double THRESHOLD = 1;
	
	private final int width;
	private final int height;
	private final int number_of_kernels;
	private final double threshold;
	private final double alpha;
	private GaussianMixture[] mixtures;
	private double last_ratio;

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
		this.threshold = THRESHOLD/number_of_kernels;
		this.alpha = alpha;
		this.mixtures = new GaussianMixture[width*height];
		for( int i = 0; i < this.mixtures.length; i++ )
			this.mixtures[i] = new GaussianMixture( number_of_kernels, alpha );
		this.last_ratio = 0;
	}
	
	public void update( int[] data )
	{
		if (data.length != mixtures.length) throw new RuntimeException("given data's length differs");
		for (int i = 0; i < mixtures.length; i++) mixtures[i].update(data[i]&0xff);
	}
	
	public int[] foreground( int[] data )
	{
		if (data.length != this.mixtures.length) throw new RuntimeException( "given data's length differs" );
		int[] image = new int[this.mixtures.length];
		last_ratio = 0;
		for (int i = 0; i < data.length; i++)
		{
			if(this.mixtures[i].getWeight(data[i]&0xff) < threshold )
			{
				image[i] = ~0;
				last_ratio++;
			}
			else
			{
				image[i] = 0;
			}
		}
		last_ratio /= data.length;
		return image;
	}
	
	public double getRatio()
	{
		return last_ratio;
	}
	
	public int[] getMeanModel()
	{	
		int[] res = new int[this.mixtures.length];
		for( int i = 0; i < this.mixtures.length; i++ )
		{
			res[i] = (int)this.mixtures[i].getMeanAboveThreshold(threshold);
			res[i] = res[i]<<16|res[i]<<8|res[i];
		}
		return res;
	}
	
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
	
	public int[] getVarianceImage()
	{
		int[] data = new int[this.mixtures.length];
		for( int i = 0; i < this.mixtures.length; i++ )
		{
			data[i] = (int)Math.min(Math.max(0.0, mixtures[i].getDominantVariance()), 255.0);
			data[i] = data[i] << 16 | data[i] << 8 | data[i];
		}
		return data;
	}
}
