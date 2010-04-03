package com.tigam.valdetectie.streams.filters;

import com.tigam.valdetectie.streams.ImageFilterStream;

/**
 * An {@link ImageFilter} should be able to process frames with a specific
 * algorithm, the {@link ImageFilterStream} can use them to process streams of images
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public interface ImageFilter
{
	/**
	 * Process a given image
	 * @param img the image data
	 * @param width the width of the image
	 * @param height the height of the image
	 * @return the output of the processed image, the algorithm is allowed to change the input data also
	 */
	int [] applyFilter(int [] img, int width, int height);
}
