package com.tigam.valdetectie.utils;

public class Box
{
	public final int topLeftX;

	public final int topLeftY;

	public final int bottomRightX;

	public final int bottomRightY;

	public Box(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY)
	{
		super();
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
	}

	public Box append(Box that)
	{
		if( that == null )
			return this;
		return new Box(Math.min(this.topLeftX, that.topLeftX), Math.min(
				this.topLeftY, that.topLeftY), Math.max(this.bottomRightX,
				that.bottomRightX), Math.max(this.bottomRightY,
				that.bottomRightY));
	}

	public Box intersect(Box that)
	{
		Box r = null;
		if( that != null )
		{
			r = new Box(
					Math.max(this.topLeftX, that.topLeftX),
					Math.max(this.topLeftY, that.topLeftY),
					Math.min(this.bottomRightX,that.bottomRightX),
					Math.min(this.bottomRightY,	that.bottomRightY)
			);
			
			if (r.topLeftX > r.bottomRightX || r.topLeftY > r.bottomRightY)
				return null;
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

	public static void main(String... args)
	{
		Box b1 = new Box(5, 5, 10, 10);
		Box b2 = new Box(7, 3, 17, 17);
		
		System.out.println(b1.intersect(b2));
		System.out.println(b2.intersect(b1));
	}
}
