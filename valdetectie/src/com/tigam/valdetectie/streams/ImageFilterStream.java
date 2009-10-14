package com.tigam.valdetectie.streams;

import com.tigam.valdetectie.streams.filters.ImageFilter;

public class ImageFilterStream implements ImageStream
{

	private final ImageFilter [] filters;
	private final ImageStream stream;
	
	public ImageFilterStream(ImageStream stream, ImageFilter...filters){
		this.stream = stream;
		this.filters = filters;
	}
	
	@Override
	public int height()
	{
		return this.stream.height();
	}

	@Override
	public int[] read()
	{
		int width = this.stream.width();
		int height = this.stream.height();
		
		int [] img = this.stream.read();
		for (ImageFilter f:filters)
			img = f.applyFilter(img, width, height);
		return img;
	}

	@Override
	public int width()
	{
		return this.stream.width();
	}

}
