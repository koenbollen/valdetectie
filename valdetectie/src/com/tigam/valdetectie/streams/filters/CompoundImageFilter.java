package com.tigam.valdetectie.streams.filters;

/**
 * An implementation of {@link ImageFilter} to process multiple filters as one
 * 
 * @author nils
 * 
 */
public class CompoundImageFilter implements ImageFilter
{

	private final ImageFilter[] filters;

	/**
	 * Create a new {@link CompoundImageFilter} to process multiple filters as
	 * one
	 * 
	 * @param filters
	 *            the list of filters
	 */
	public CompoundImageFilter(ImageFilter... filters)
	{
		this.filters = filters;
	}

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		for( ImageFilter f : filters )
			img = f.applyFilter(img, width, height);
		return img;
	}

}
