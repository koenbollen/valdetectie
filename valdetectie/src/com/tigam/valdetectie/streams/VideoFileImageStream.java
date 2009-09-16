package com.tigam.valdetectie.streams;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * An implementation of {@link ImageStream} that reads a video file.
 * 
 * @author Koen Bollen
 */
public class VideoFileImageStream implements ImageStream
{

	public final File file;

	private InputStream in;
	
	public VideoFileImageStream(File file) throws ImageStreamException, IOException
	{
		this (file, 1024, 768);
	}

	public VideoFileImageStream(File file, int width, int height) throws ImageStreamException, IOException
	{

		this.file = file;

		if( !file.exists() )
			throw new IOException("No such file or directory.");

		try
		{
			String resolution = width+"x"+height;
			String command = "ffmpeg -i " + file + " -s "+resolution+" -f image2pipe -vcodec bmp -";

			Process cam = Runtime.getRuntime().exec(command);
			in = cam.getInputStream();
		} catch( IOException ball )
		{
			throw new ImageStreamException("Unable to start ffmpeg.", ball);
		} catch( SecurityException ball )
		{
			throw new ImageStreamException("Unable to start ffmpeg.", ball);
		}

	}

	@Override
	public synchronized Image read()
	{
		try
		{
			return ImageIO.read(this.in);
		} catch( IOException e )
		{
			return null;
		}
	}

}
