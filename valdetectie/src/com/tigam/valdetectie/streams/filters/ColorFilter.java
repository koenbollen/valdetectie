package com.tigam.valdetectie.streams.filters;

public class ColorFilter implements ImageFilter
{
	public static final int RED = 16;
	public static final int GREEN = 8;
	public static final int BLUE = 0;

	private final int color_shift;
	
	/**
	 * Easy to use instance of the {@link ColorFilter}
	 */
	public static final ColorFilter red = new ColorFilter(RED);
	public static final ColorFilter green = new ColorFilter(GREEN);
	public static final ColorFilter blue = new ColorFilter(BLUE);
	public static final ColorFilter instance = red;

	public ColorFilter()
	{
		this(RED);
	}
	
	public ColorFilter(int color_shift)
	{
		this.color_shift = color_shift;
	}
	
	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if (img == null)
			return null;
			
		int [] res = new int[img.length];
		int x = img.length;
		while( x --> 0 )
			res[x] = img[x] & (0xff<<color_shift);
		return res;
	}

}
