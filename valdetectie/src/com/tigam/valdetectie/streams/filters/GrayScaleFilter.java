package com.tigam.valdetectie.streams.filters;

/**
 * Can change a colored image in to a grayscale image. 
 * 
 * @author nils
 *
 */
public class GrayScaleFilter implements ImageFilter
{
	/**
	 * Easy to use instance of the {@link GrayScaleFilter}
	 */
	public static final GrayScaleFilter instance = new GrayScaleFilter();

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( img == null )
			return null;
		for( int i = 0; i < img.length; i++ )
		{
			int g = ( (img[i]>>16&0xff)+(img[i]>>8&0xff)+(img[i]&0xff) ) / 3;
			img[i] =  0xff000000 | g << 16 | g << 8 | g;
		}
		return img;
	}

}
