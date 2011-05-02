package com.tigam.valdetectie.algorithms.dropdetector.filters;

import java.util.List;

import com.tigam.valdetectie.algorithms.dropdetector.DropFilter;
import com.tigam.valdetectie.utils.Box;
/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class RatioDropFilter implements DropFilter
{
	final boolean bigger;
	final double ratio;
	
	public RatioDropFilter(double ratio)
	{
		this(ratio, true);
	}
	
	public RatioDropFilter(double ratio, boolean bigger)
	{
		this.ratio = ratio;
		this.bigger = bigger;
	}

	@Override
	public boolean dropped(List<Box> history)
	{
		if (history.size() == 0) return false;
		if (bigger) return history.get(0).ratio() > ratio;
		else        return history.get(0).ratio() < ratio;
	}
}
