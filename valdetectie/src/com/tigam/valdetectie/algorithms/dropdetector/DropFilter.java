package com.tigam.valdetectie.algorithms.dropdetector;

import java.util.List;

import com.tigam.valdetectie.utils.Box;

public interface DropFilter
{
	boolean dropped(List<Box> history);
}
