package com.tigam.valdetectie.algorithms.dropdetector;

import java.util.List;

import com.tigam.valdetectie.utils.Box;

public class OrDropFilter implements DropFilter
{
	final DropFilter [] filters;
	public OrDropFilter(DropFilter ... filters){
		this.filters = filters;
	}

	@Override
	public boolean dropped(List<Box> history)
	{
		for (DropFilter f:filters)
			if (f.dropped(history))
				return true;
		return false;
	}

}
