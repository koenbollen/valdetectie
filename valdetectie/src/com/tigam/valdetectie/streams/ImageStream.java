package com.tigam.valdetectie.streams;

import java.awt.Image;

/**
 * An interface for a video feed where the video consists of individual frames
 * 
 * @author Nils Dijk
 */
public interface ImageStream
{
	/**
	 * Blocks until there is a frame
	 * @return the next {@link Image} from the ImageStream. <code>null</code> if the stream is terminated
	 */
	public Image read();
}
