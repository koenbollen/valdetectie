package com.tigam.valdetectie.utils;

/**
 * Class to be used to indicate boxes around objects in the stream.
 * @author Sam and definitely not Rick
 *
 */
public class Box {
	int topLeftX = Integer.MAX_VALUE;
	int topLeftY = Integer.MAX_VALUE;
	int bottomRightX = Integer.MIN_VALUE;
	int bottomRightY = Integer.MIN_VALUE;
	boolean display = true;
	
	/**
	 * Creates a box of 0 by 0
	 * @param newX First coordinate, X-axis. Doesn't matter if it is bottom or top
	 * @param newY First coordinate, Y-axis. Doesn't matter if it is bottom or top
	 */
	public Box(int newX, int newY) {
		setMinMax(newX, newY);
	}
	
	/**
	 * Calculates the size of the box
	 * @return The size of the box (width * height)
	 */
	public int size() {
		return (this.bottomRightX - this.topLeftX) * (this.bottomRightY - this.topLeftY);
	}
	
	public void setTopLeft(int x, int y) {
		setTopLeftX(x);
		setTopLeftY(y);
	}
	
	public int getTopLeftX() {
		return this.topLeftX;
	}
	
	/**
	 * Updates the size of the box if needed
	 * @param newX New X value introduced to the box
	 * @param newY New Y value introduced to the box
	 */
	public void setMinMax(int newX, int newY) {
		setTopLeftX(Math.min(getTopLeftX(), newX));
		setTopLeftY(Math.min(getTopLeftY(), newY));
		
		setBottomRightX(Math.max(getBottomRightX(), newX));
		setBottomRightY(Math.max(getBottomRightY(), newY));
	}
	
	public void setTopLeftX(int x) {
		this.topLeftX = x;
	}
	
	public int getTopLeftY() {
		return this.topLeftY;
	}
	
	public void setTopLeftY(int y) {
		this.topLeftY = y;
	}
	
	public void setBottomRight(int x, int y) {
		setBottomRightX(x);
		setBottomRightY(y);
	}
	
	public int getBottomRightX() {
		return this.bottomRightX;
	}
	
	public void setBottomRightX(int x) {
		this.bottomRightX = x;
	}
	
	public int getBottomRightY() {
		return this.bottomRightY;
	}
	
	public void setBottomRightY(int y) {
		this.bottomRightY = y;
	}
	
	public int getCenterPointX() {
		return (this.bottomRightX + this.topLeftX) / 2;
	}
	public int getCenterPointY() {
		return (this.bottomRightY + this.topLeftY) / 2;
	}
	
	public int width() {
		return this.bottomRightX - this.topLeftX;
	}
	
	public int height() {
		return this.bottomRightY - this.topLeftY;
	}
	public double getRatio() {
		return (width() / height());
	}
	
	public boolean intersect(Box box) {
		int width = Math.abs(this.getCenterPointX() - box.getCenterPointX());
		int height = Math.abs(this.getCenterPointY() - box.getCenterPointY());
		
		int combWidth = ( this.width() + box.width() ) / 2;
		int combHeight = ( this.height() + box.height() ) / 2;
		
		if (width <= combWidth && height <= combHeight)
			return true;

		return false;
	}
	
	public void merge(Box box) {
		setMinMax(box.getTopLeftX(), box.getTopLeftY());
		setMinMax(box.getBottomRightX(), box.getBottomRightY());
	}
	
	public boolean getDisplay() {
		return this.display;
	}
	
	public void setDisplay(boolean display) {
		this.display = display;
	}
}