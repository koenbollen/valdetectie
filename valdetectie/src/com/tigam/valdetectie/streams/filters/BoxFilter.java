package com.tigam.valdetectie.streams.filters;

import java.util.ArrayList;

/**
 * Boxes around things
 * 
 * @author rick en sam
 *
 */
public class BoxFilter implements ImageFilter {
	/**
	 * Easy to use instance of the {@link BoxFilter}
	 */
	public static final BoxFilter instance = new BoxFilter();
	private final int LEFT		= 0;
	private final int TOPLEFT	= 1;
	private final int TOP		= 2;
	private final int TOPRIGHT	= 3;

	@Override
	public int[] applyFilter(int[] img, int width, int height)
	{
		if( img == null )
			return null;
		
		ArrayList<Integer> labels = detect(img, width, height);
		colourize(img, width, height, labels);
		
		return img;
	}
	
	private ArrayList<Integer> detect(int[] img, int width, int height) {
		ArrayList<Integer> labels = new ArrayList<Integer>();
		
		int labelCounter = 1;
		int curPixel = 0;
		int topRightPixel = 0;
		int topPixel = 0;
    	int topLeftPixel = 0;
    	int leftPixel = 0;

    	for (int x = 0; x < width; x++) {
    		img[x] = -1;
    		if(x < height)
    			img[x * width] = -1;
    	}
    	
		//img[x + (width * y)] = 0xFF0000;
		//img[(x - 1) + (width * y)]		//LEFT
    	//img[(x - 1) + (width * (y - 1))]	//TOPLEFT
    	//img[x + (width * (y - 1))]		//TOP
    	//img[(x + 1) + (width * (y - 1))]	//TOPRIGHT
		for (int y = 1; y < height; y++){
            for (int x = 1; x < width; x++) {
            	curPixel = x + (width * y);
            	topRightPixel = (x + 1) + (width * (y - 1));
            	topPixel = x + (width * (y - 1));
            	topLeftPixel = (x - 1) + (width * (y - 1));
            	leftPixel = (x - 1) + (width * y);
 
            	
            	if ((img[curPixel]&0xFF) != 0){ // True = It is Foreground
            		img[curPixel] = 0;
            		
            		// If the TOPRIGHT pixel has a label, give current pixel the same label
            		if(img[topRightPixel] >= 1 ) { 
            			img[curPixel] = img[topRightPixel];
            		}
            		
            		// If the TOP pixel has a label, prioritize over TOPRIGHT pixel
            		// If the current pixel isn't the same as TOP pixel, link the labels.
            		if(img[topPixel] >= 1 && img[topPixel] != img[curPixel]) {
            			if(img[curPixel] != 0) {
            				updateLabels(labels, img[curPixel], img[topPixel]);
            			}
            			
            			img[curPixel] = img[topPixel];
            		}
            		
            		// If the TOPLEFT pixel has a label, prioritize over TOP pixel
            		// If the current pixel isn't the same as TOPLEFT pixel, link the labels.
            		if(img[topLeftPixel] >= 1 && img[topLeftPixel] != img[curPixel]) {
            			if(img[curPixel] != 0) {
            				updateLabels(labels, img[curPixel], img[topLeftPixel]);
            			}
            			
	    				img[curPixel] = img[topLeftPixel];
            		}
            		
            		// If the LEFT pixel has a label, prioritize over TOPLEFT pixel
            		if(img[leftPixel] >= 1 && img[leftPixel] != img[curPixel]) {
            			if(img[curPixel] != 0) {
            				updateLabels(labels, img[curPixel], img[leftPixel]);
            			}
            			
            			img[curPixel] = img[leftPixel];
            		}
            		
            		if(img[curPixel] == 0) {
            			labels.add(labelCounter);
            			img[curPixel] = labelCounter++;
            		}
            	} else {
            		img[curPixel] = -1;
            	}
            }
		}
		return labels;
	}
	
	private void colourize(int[] img, int width, int height, ArrayList<Integer> labels) {
		int[] colours = new int[] {0xFF0000, 0x00FF00, 0x0000FF, 0x000000, 0xD80000, 0xD8CB00, 0x00D8CB, 0x0056D8, 0x8E00D8, 0xD800C6, 0xC40000, 0xFF4040, 0xFF9B9B, 0xA09BFF, 0xA09BFF, 0x0A00C6 };

		for (int i = 0; i < img.length; i++) {
			if (img[i] == -1) {
				img[i] = ~0; // White
			} else {
				if (img[i]-1 >= 0)
					img[i] = colours[(labels.get(img[i]-1)-1) % colours.length];
			}
        }
	}
	
	private void updateLabels(ArrayList<Integer> labels, int oldData, int newData) {
		int index = -1;
		int oldValue = -1;
		int newValue = -1;
		
		if (oldData > newData) {
			index = oldData-1;
			newValue = labels.get(newData-1);
		} else {
			index = newData-1;
			newValue = labels.get(oldData-1);
		}
		
		if ( index >= 0 && newValue >= 0) {
			oldValue = labels.get(index);
			labels.set(index, newValue);
			
			if (oldValue != index) {
				int tmpValue = -1;
				for (int i = 0; i < labels.size(); i++) {
					tmpValue = labels.get(i);
					if ( tmpValue == oldValue)
						labels.set(i, newValue);
				}
			}
		}
	}
}