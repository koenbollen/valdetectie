package com.tigam.valdetectie.algorithms;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.tigam.valdetectie.utils.Box;
import com.tigam.valdetectie.utils.BoxFactory;

public class BoundingBoxExtractor
{
	public static List<Box> extractBoxes(int [] img, int width){
		int [] ccl = CCL.ccl(img, width);
		int maxLabel = Integer.MIN_VALUE;
		for( int i = 0; i < ccl.length; i++ )
			maxLabel = Math.max(maxLabel, ccl[i]);
		BoxFactory [] creators = new BoxFactory[maxLabel];
		
		
		for( int i = 0; i < ccl.length; i++ ){
			if (ccl[i] == 0)
				continue;
			
			int x = i%width;
			int y = i/width;
			
			if (creators[ccl[i]-1] == null)
				creators[ccl[i]-1] = new BoxFactory(x,y);
			else
				creators[ccl[i]-1].setMinMax(x, y);
		}
		
		List<Box> boxlist = new LinkedList<Box>();
		for (int i=0; i<creators.length; i++)
			if (creators[i] != null)
				boxlist.add(creators[i].getBox());
		
		return boxlist;
	}
	
	public static void boundingBoxDrawerer(int[] img, int width, Box box, int color) {
		if (box == null)
			return;
		int topLeftX = box.topLeftX;
		int topLeftY = box.topLeftY;
		int bottomRightX = box.bottomRightX;
		int bottomRightY = box.bottomRightY;
		
		for (int x = topLeftX; x < bottomRightX; x++) {
			img[(topLeftY * width) + x] = color;
			img[(bottomRightY * width) + x] = color;
		}
		for (int y = topLeftY; y < bottomRightY; y++ ) {
			img[(y * width) + topLeftX] = color;
			img[(y * width) + bottomRightX] = color;
		}
	}
	
	public static List<Box> mergeBoxes(Iterable<Box> boxes){
		Box box = null;
		for (Box b: boxes)
			box = b.append(box);
		return Collections.singletonList(box);
	}
}
