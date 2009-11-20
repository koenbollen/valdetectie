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
		
		int [] res = new int[img.length];
		
		for( int i = 0; i < img.length; i++ )
		{
			int r = (img[i]>>16&0xff);
			int g = (img[i]>>8&0xff);
			int b = (img[i]&0xff);
			// ITU-R 601-2 luma transform:
		    // L = R * 299/1000 + G * 587/1000 + B * 114/1000
			int l = (int)(r * 299.0/1000 + g * 587.0/1000 + b * 114.0/1000);
			res[i] =  0xff000000 | l << 16 | l << 8 | l;
		}
		return res;
	}

}
