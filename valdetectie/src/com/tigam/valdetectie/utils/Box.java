package com.tigam.valdetectie.utils;

import java.util.Comparator;
/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class Box
{
	public static final Comparator<Box> SurfaceComperator = new Comparator<Box>()
	{
		@Override
		public int compare(Box o1, Box o2)
		{
			return o1.surface() > o2.surface() ? 1 : -1;
		}
	};
	
	public final int bottomRightX;
	public final int bottomRightY;
	public final int topLeftX;
	public final int topLeftY;
	public final int pixels;

	public Box(Box box)
	{
		super();
		this.topLeftX = box.topLeftX;
		this.topLeftY = box.topLeftY;
		this.bottomRightX = box.bottomRightX;
		this.bottomRightY = box.bottomRightY;
		this.pixels = box.pixels;
	}
	
	public Box(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY, int pixels)
	{
		super();
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
		this.pixels = pixels;
	}
	
	public double ratio()
	{
		double width = this.bottomRightX - this.topLeftX;
		double height = this.bottomRightY - this.topLeftY;
		if (height == 0) return 0;
		return width / height;
	}

	public Box append(Box that)
	{
		if (that == null) return this;
		return new Box(Math.min(this.topLeftX, that.topLeftX),
		               Math.min(this.topLeftY, that.topLeftY),
		               Math.max(this.bottomRightX, that.bottomRightX),
		               Math.max(this.bottomRightY, that.bottomRightY),
		               this.pixels + that.pixels);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		Box other = (Box) obj;
		if( bottomRightX != other.bottomRightX )
			return false;
		if( bottomRightY != other.bottomRightY )
			return false;
		if( topLeftX != other.topLeftX )
			return false;
		if( topLeftY != other.topLeftY )
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + bottomRightX;
		result = prime * result + bottomRightY;
		result = prime * result + topLeftX;
		result = prime * result + topLeftY;
		return result;
	}

	public Box intersect(Box that)
	{
		Box r = null;
		if( that != null )
		{
			r = new Box(Math.max(this.topLeftX, that.topLeftX),
			            Math.max(this.topLeftY, that.topLeftY),
			            Math.min(this.bottomRightX, that.bottomRightX),
			            Math.min(this.bottomRightY, that.bottomRightY),
			            Math.abs(this.pixels - that.pixels));
			
			if (r.topLeftX > r.bottomRightX || r.topLeftY > r.bottomRightY) return null;
		}
		return r;
	}
	
	public boolean isIntersecting(Box that) {
		if (that != null) {
			return (
					Math.max(this.topLeftX, that.topLeftX) <= Math.min(this.bottomRightX,that.bottomRightX) &&
					Math.max(this.topLeftY, that.topLeftY) <= Math.min(this.bottomRightY,	that.bottomRightY)
					);
		}
		
		return false;
	}
	
	
	public double surface()
	{
		return (this.topLeftX - this.bottomRightX) * (this.topLeftY - this.bottomRightY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Box [topLeftX=" + topLeftX + ", topLeftY=" + topLeftY
				+ ", bottomRightX=" + bottomRightX + ", bottomRightY="
				+ bottomRightY + "]";
	}
}
