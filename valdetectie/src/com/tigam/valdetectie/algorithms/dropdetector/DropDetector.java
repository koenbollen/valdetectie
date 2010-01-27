package com.tigam.valdetectie.algorithms.dropdetector;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tigam.valdetectie.utils.Box;

public class DropDetector
{
	private final Map<Integer, LinkedList<Box>> hisToys;
	
	public DropDetector (){
		hisToys = new HashMap<Integer, LinkedList<Box>>();
	}
	
	public void update (Map<Box, Integer> tracked){
		List<Integer> ids = new LinkedList<Integer>(hisToys.keySet());
		for (Entry<Box, Integer> e:tracked.entrySet()){
			Integer id = e.getValue();
			ids.remove((Object)id);
			if (!hisToys.containsKey(id))
				hisToys.put(id, new LinkedList<Box>());
			hisToys.get(id).push(e.getKey());
		}
		
		// remove boxes which are not in screen
		for (Integer i:ids)
			hisToys.remove(i);
	}
	
	public List<Box> testDrop(DropFilter filter){
		List<Box> drops = new LinkedList<Box>();
		
		for (Entry<Integer, LinkedList<Box>> e:hisToys.entrySet()){
			LinkedList<Box> s = e.getValue();
			if (filter.dropped(Collections.unmodifiableList(s)))
				drops.add(s.peek());
		}
		
		return drops;
	}
}
