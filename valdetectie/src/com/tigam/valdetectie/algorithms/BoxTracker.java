package com.tigam.valdetectie.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import com.tigam.valdetectie.utils.Box;

public class BoxTracker
{

	private Map<Box, Integer> last;
	private int nextLabel;

	public BoxTracker()
	{
		this.nextLabel = 0;
		this.last = new HashMap<Box, Integer>();
	}

	public Map<Integer, Box> track(List<Box> boxes)
	{
		if( !(boxes instanceof RandomAccess) )
			boxes = new ArrayList<Box>(boxes);

		Map<Integer, Box> result = new HashMap<Integer, Box>();

		for( int i = 0; i < boxes.size(); i++ )
		{
			Box b = boxes.get(i);
			double max = Double.MIN_VALUE;
			Box best = null;
			for( Box sub : last.keySet() )
			{
				Box inter = b.intersect(sub);
				if( inter == null )
					continue;
				double surface = inter.surface();
				if( surface > max )
				{
					max = surface;
					best = sub;
				}
			}
			if( best != null )
			{
				Integer label = last.get(best);
				result.put(label, b);// best
			} else
			{
				result.put(this.nextLabel++, b);
			}

		}

		last.clear();
		for( Integer label : result.keySet() )
			last.put(result.get(label), label);

		return result;
	}
}