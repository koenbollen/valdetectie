package com.tigam.valdetectie.algorithms;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tigam.valdetectie.utils.Box;
/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class BoxTracker
{
	
	static class BoxInterSection
	{
		public static final Comparator<BoxInterSection> OVERLAP_SORTER = new Comparator<BoxInterSection>()
		{
			
			@Override
			public int compare(BoxInterSection o1, BoxInterSection o2)
			{
				if (o1.overlap == o2.overlap)
					return 0;
				return o1.overlap > o2.overlap ? -1:1;
			}
		};
	
		public final Box oldBox;
		public final Box newBox;
		public final double overlap;

		public BoxInterSection(Box oldBox, Box newBox)
		{
			Box intersect = oldBox.intersect(newBox);

			this.oldBox = oldBox;
			this.newBox = newBox;
			if( intersect != null )
				this.overlap = (intersect.surface() / oldBox.surface()) * (intersect.surface() / newBox.surface());
			else
				this.overlap = 0;
		}
	}
	
	Map<Box, Integer> last;
	int nextLabel;
	public BoxTracker(){
		this.last = Collections.emptyMap();
		this.nextLabel = 0;
	}
	
	public Map<Box, Integer> track(List<Box> boxes){
		List<BoxInterSection> potential = new LinkedList<BoxInterSection>();
		for (Box nb:boxes){
			for (Box lb:last.keySet()){
				if (lb.isIntersecting(nb))
					potential.add(new BoxInterSection(lb, nb));
			}
		}
		Collections.sort(potential, BoxInterSection.OVERLAP_SORTER);
		
		Map<Box,Integer> newLabels = new HashMap<Box, Integer>();
		
		while (potential.size() > 0){
			BoxInterSection use = potential.remove(0);
			newLabels.put(use.newBox, last.get(use.oldBox));
			
			// remove all relatives to use
			List<BoxInterSection> toRemove = new LinkedList<BoxInterSection>();
			for (BoxInterSection i:potential)
				if (i.newBox.equals(use.newBox) || i.oldBox.equals(use.oldBox))
					toRemove.add(i);
			potential.removeAll(toRemove);
		}
		
		boxes.removeAll(newLabels.keySet());
		
		for (Box b:boxes)
			newLabels.put(b, this.nextLabel++);
		this.last = newLabels;
		return Collections.unmodifiableMap(newLabels);
	}
}
