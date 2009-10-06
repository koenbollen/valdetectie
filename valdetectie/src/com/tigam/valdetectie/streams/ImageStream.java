package com.tigam.valdetectie.streams;


/**
 * An interface for a video feed where the video consists of individual frames
 * 
 * @author Nils Dijk &and; Koen Bollen
 */
public interface ImageStream
{
	/**
	 * Blocks until there is a frame then returns it as an int array.
	 * 
	 * Array index:
	 *  pixel = data[y*width+x]
	 * 
	 * @return the next pixeldata of the image or <code>null</code> if the stream is terminated.
	 */
	public int[] read();

	/**
	 * Return the width of the images that are read from this stream.
	 * 
	 * @return The width.
	 */
	public int width();
	/**
	 * return the height of the images that are read from this stream.
	 * 
	 * @return The height.
	 */
	public int height();
}
