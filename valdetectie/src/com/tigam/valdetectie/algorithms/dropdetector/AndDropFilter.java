package com.tigam.valdetectie.algorithms.dropdetector;

import java.util.List;

import com.tigam.valdetectie.utils.Box;
/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class AndDropFilter implements DropFilter
{
	final DropFilter [] filters;
	public AndDropFilter(DropFilter ... filters){
		this.filters = filters;
	}

	@Override
	public boolean dropped(List<Box> history)
	{
		for (DropFilter f:filters)
			if (!f.dropped(history))
				return false;
		return true;
	}

}
