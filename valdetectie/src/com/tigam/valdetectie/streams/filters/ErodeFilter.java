package com.tigam.valdetectie.streams.filters;

/**
 * Erodes an image
 * 
 * @author rick
 *
 */
public class ErodeFilter implements ImageFilter
{
	/**
	 * Easy to use instance of the {@link ErodeDilateFilter}
	 */
	public static final ErodeFilter instance = new ErodeFilter();
	private int intensity;
	
	public ErodeFilter() {
		this(2);
	}
	public ErodeFilter(int intensity) {
		this.intensity = intensity;
	}

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( img == null )
			return null;
		
		reversedManhattanDistance(img, width, height);           

        //Loops through the entire array and applies the intensity by setting
    	//all the values within the intensity threshold to white, the rest to black
        for (int i = 0; i < img.length; i++) {
        	img[i] = (img[i] <= this.intensity) ? 0xFFFFFF : 0x000000; // White : Black
        }
		
		return img;
	}
	
    public int getIntensity() {
    	return this.intensity;
    }
    
    public void setIntensity(int intensity) {
    	this.intensity = intensity;
    }
	
	/*
     * Slightly modified from the 2D variant at:
     * @url http://ostermiller.org/dilate_and_erode.html
     */
    private void reversedManhattanDistance(int[] data, int width, int height) {
    	//traverse from top left to bottom right
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++) {
                if ((data[x + (width * y)]&0xFF) != 0){ // Is it White?
                    // first pass and pixel was off, it gets a zero
                	data[x + (width * y)] = 0;
                } else {
                    // pixel was on
                    // It is at most the sum of the lengths of the array
                    // away from a pixel that is off
                	data[x + (width * y)] = width + height;
                    // or one more than the pixel to the north
                    if (y > 0) 
                    	data[x + (width * y)] = Math.min(data[x + (width * y)], data[x + (width * (y - 1))] + 1);
                    // or one more than the pixel to the west
                    if (x > 0) 
                    	data[x + (width * y)] = Math.min(data[x + (width * y)], data[(x - 1) + (width * y)] + 1);
                }
            }
        }
        
        // traverse from bottom right to top left
        for (int y = height - 1; y >= 0; y--){
            for (int x = width - 1; x >= 0; x--){
                // either what we had on the first pass
                // or one more than the pixel to the south
                if ((y + 1) < height) 
                	data[x + (width * y)] = Math.min(data[x + (width * y)], data[x + (width * (y + 1))] + 1);
                // or one more than the pixel to the east
                if ((x + 1) < width) 
                	data[x + (width * y)] = Math.min(data[x + (width * y)], data[(x + 1) + (width * y)] + 1);
            }
        }
    }
}