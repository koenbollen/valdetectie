package com.tigam.valdetectie.algorithms;

import java.util.LinkedList;
import java.util.List;

import com.tigam.valdetectie.utils.Box;
import com.tigam.valdetectie.utils.BoxFactory;
import com.tigam.valdetectie.utils.MeanDeviation;
import com.tigam.valdetectie.utils.UnionFind;

/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class BoundingBoxExtractor
{

	static class GausianHolder
	{
		private final MeanDeviation x;
		private final MeanDeviation y;

		public GausianHolder()
		{
			this.x = new MeanDeviation();
			this.y = new MeanDeviation();
		}

		public void addPoint(int x, int y)
		{
			this.x.insert(x);
			this.y.insert(y);
		}
	}

	public static List<Box> extractBoxes(int[] img, int width)
	{
		int[] ccl = CCL.ccl(img, width);
		int maxLabel = Integer.MIN_VALUE;
		for( int i = 0; i < ccl.length; i++ )
			maxLabel = Math.max(maxLabel, ccl[i]);
		BoxFactory[] creators = new BoxFactory[maxLabel];

		GausianHolder[] deviation = new GausianHolder[maxLabel];

		for( int i = 0; i < ccl.length; i++ )
		{
			if( ccl[i] == 0 )
				continue;

			int labelIndex = ccl[i] - 1;
			int x = i % width;
			int y = i / width;

			// add point to BoundingBox
			if( creators[labelIndex] == null )
				creators[labelIndex] = new BoxFactory(x, y);
			else
				creators[labelIndex].setMinMax(x, y);

			if( deviation[labelIndex] == null )
			{
				deviation[labelIndex] = new GausianHolder();
			}

			// add the x and y to the deviation
			deviation[labelIndex].addPoint(x, y);
		}
		
		Box [] boxes = new Box[maxLabel];
		for (int i=0; i<boxes.length; i++)
			if (creators[i] != null)
				boxes[i] = creators[i].getBox();            

		
		
//		for (int i=0; i<deviation.length; i++){
//			if (deviation[i] == null)
//				continue;
//				
//			double x = deviation[i].x.mean();
//			double y = deviation[i].y.mean();
//			double xDev = deviation[i].x.deviation();
//			double yDev = deviation[i].y.deviation();
//		
//			for (int l=1; l<=4; l++)
//				boxlist.add(new Box((int)(x-l*xDev),(int)(y-l*yDev),(int)(x+l*xDev),(int)(y+l*yDev)));
//		}
			

		UnionFind union = new UnionFind();
		for( int i = 0; i < maxLabel; i++ )
		{
			Box outerBox = boxes[i];
			GausianHolder outerGaus = deviation[i];
			if (outerBox == null || outerGaus == null)
				continue;
			double outerX = outerGaus.x.mean();
			double outerY = outerGaus.y.mean();
			
				
			for( int j = i+1; j < maxLabel; j++ )
			{
				Box innerBox = boxes[j];
				GausianHolder innerGaus = deviation[j];
				
				if (innerBox == null || innerGaus == null)
					continue;
				
				double innerX = innerGaus.x.mean();
				double innerY = innerGaus.y.mean();
				
				double diffX = Math.abs(innerX-outerX);
				double diffY = Math.abs(innerY-outerY);				
				
				double mahalanobisX, mahalanobisY;
				
				// first outer to inner
				mahalanobisX = diffX/outerGaus.x.deviation();
				mahalanobisY = diffY/outerGaus.y.deviation();
				double distanceOuterToInner = Math.hypot(mahalanobisX, mahalanobisY);
				mahalanobisX = diffX/innerGaus.x.deviation();
				mahalanobisY = diffY/innerGaus.y.deviation();
				double distanceInnerToOuter = Math.hypot(mahalanobisX, mahalanobisY);
				
				double distance = Math.max(distanceInnerToOuter, distanceOuterToInner);
//				System.out.println(distance);
				if (distance < 12)
					union.union(i, j);
			}	
		}
		
		Box [] appendedBoxes = new Box [maxLabel]; 

		List<Box> boxlist = new LinkedList<Box>();
		
		for( int i = 0; i < appendedBoxes.length; i++ ){
			if(boxes[i] == null)
				continue;
//			boxlist.add(boxes[i]);
			
			int index = union.find(i);
			appendedBoxes[index] = boxes[i].append(appendedBoxes[index]);
		}
		
		for( int i = 0; i < maxLabel; i++ ){
			if (appendedBoxes[i] != null)
				boxlist.add(appendedBoxes[i]);
		}
				
		// */

		return boxlist;
	}

	public static void boundingBoxDrawerer(int[] img, int width, Box box, int color)
	{
		if( box == null )
			return;
		int topLeftX = box.topLeftX;
		int topLeftY = box.topLeftY;
		int bottomRightX = box.bottomRightX;
		int bottomRightY = box.bottomRightY;

		int height = img.length / width;

		for( int x = Math.max(0, topLeftX); x < Math.min(width, bottomRightX); x++ )
		{
			// if (0 > x || x >= width)
			// continue;
			if( topLeftY >= 0 && topLeftY < height )
				img[(topLeftY * width) + x] = color;
			if( bottomRightY >= 0 && bottomRightY < height )
				img[(bottomRightY * width) + x] = color;
		}
		for( int y = Math.max(0, topLeftY); y < Math.min(height, bottomRightY); y++ )
		{

			if( topLeftX >= 0 && topLeftX < width )
				img[(y * width) + topLeftX] = color;
			if( bottomRightX >= 0 && bottomRightX < width )
				img[(y * width) + bottomRightX] = color;
		}
	}

	public static List<Box> mergeBoxes(List<Box> boxes)
	{
		Box[] boxArray = new Box[boxes.size()];
		boxArray = boxes.toArray(boxArray);
		UnionFind union = new UnionFind();

		Box box = null;
		List<Box> newBoxes = new LinkedList<Box>();

		for( int i = 0; i < boxArray.length - 1; i++ )
		{
			for( int j = i + 1; j < boxArray.length; j++ )
			{
				if( boxArray[i].isIntersecting(boxArray[j]) )
				{
					union.union(i, j);
				}
			}
		}

		Box[] result = new Box[boxArray.length];
		for( int i = 0; i < boxArray.length; i++ )
		{
			int index = union.find(i);
			result[index] = boxArray[i].append(result[index]);
		}

		for( int i = 0; i < boxArray.length; i++ )
		{
			if( result[i] != null )
				newBoxes.add(result[i]);
		}

		return newBoxes;
	}
}
