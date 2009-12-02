package com.tigam.valdetectie.streams.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tigam.valdetectie.utils.BoxFactory;

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
		HashMap<Integer, BoxFactory> boxlist = new HashMap<Integer, BoxFactory>();
		
		for (int i = 0; i < img.length; i++) {
			if (img[i] == -1) {
				img[i] = ~0; // White
			} else {
				if (img[i]-1 >= 0) {
					int label = (labels.get(img[i]-1)-1);
					int x = i % width;
					int y = (int)Math.floor(i / width);
					
					if (boxlist.get(label) == null) {
						boxlist.put(label, new BoxFactory(x, y));
					} else {
						boxlist.get(label).setMinMax(x, y);
					}
					img[i] = colours[label % colours.length];
				}
			}
        }
		
		//TODO: Change this cheap solution to something efficient
		Iterator it = boxlist.entrySet().iterator();
		BoxFactory[] boxArray = new BoxFactory[boxlist.entrySet().size()];
		int counter = 0;
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			boxArray[counter] = (BoxFactory)entry.getValue();
			counter++;
		}
		
		//Hide small boxes
		for (int i = 0;  i < boxArray.length; i++) {
			if (boxArray[i].size() <= 600) {
				boxArray[i].setDisplay(false);
				clearBox(img, width, height, boxArray[i]);
				
			}
		}
		
		//Merge Boxes
		/*
		for (int i = 0; i < boxArray.length-1; i++) {
			for (int j = i + 1; j < boxArray.length; j++) {
				if (i != j && boxArray[i].getDisplay() && boxArray[j].getDisplay()) {
					if (boxArray[i].intersect(boxArray[j])) {
						boxArray[i].merge(boxArray[j]);
						boxArray[j].setDisplay(false);
					}
				}
			}
		}
		//*/
		
		//Display Box or hide the content
		for (int i = 0;  i < boxArray.length; i++) {
			if (boxArray[i] != null && boxArray[i].getDisplay()) {
				boundingBoxDrawerer(img, width, height, boxArray[i]);
			}
		}
		
		
		/*
		
		it = boxlist.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			Box box = (Box)entry.getValue();
			
			if (box.size() >= 300) 
				boundingBoxDrawerer(img, width, height, box);
			else
				clearBox(img, width, height, box);
			//System.out.println("Size #"+ entry.getKey() +": "+ box.size());
			//System.out.println(box.getBottomRightX() +" - "+ box.getTopLeftX() +" x "+ box.getBottomRightY() +" - "+ box.getTopLeftY());
		}
		*/
	}

	private void boundingBoxDrawerer(int[] img, int width, int height, BoxFactory box ) {
		int topLeftX = box.getTopLeftX();
		int topLeftY = box.getTopLeftY();
		int bottomRightX = box.getBottomRightX();
		int bottomRightY = box.getBottomRightY();
		
		for (int x = topLeftX; x < bottomRightX; x++) {
			//img[x + (width * y)] = 0;
			img[(topLeftY * width) + x] = 0x000000;
			img[(bottomRightY * width) + x] = 0x000000;
		}
		for (int y = topLeftY; y < bottomRightY; y++ ) {
			img[(y * width) + topLeftX] = 0x000000;
			img[(y * width) + bottomRightX] = 0x000000;
		}
	}
	
	/**
	 * Empties the pixels inside the  box
	 * @param img Image that is to be pudated
	 * @param width Width of the img
	 * @param height Height of the img
	 * @param box Box that is to be cleared
	 */
	private void clearBox(int[] img, int width, int height, BoxFactory box ) {
		int topLeftX = box.getTopLeftX();
		int topLeftY = box.getTopLeftY();
		int bottomRightX = box.getBottomRightX();
		int bottomRightY = box.getBottomRightY();
		
		for (int y = topLeftY; y <= bottomRightY; y++){
			for (int x = topLeftX; x <= bottomRightX; x++) {
				img[x + (width * y)] = ~0; //White
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