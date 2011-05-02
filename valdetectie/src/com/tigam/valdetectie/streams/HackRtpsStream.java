package com.tigam.valdetectie.streams;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.utils.ErrorStreamReader;

public class HackRtpsStream implements ImageStream
{
	private final int width;
	private final int height;
	Process ffmpeg = null;
	
	/**
	 * the internal stream to read the images from
	 */
	private InputStream pipe;
	

	/**
	 * Create a new instance of {@link LinuxDeviceImageStream} with a specified width, height and devicce to capture from
	 * @param width the width of the image to capture
	 * @param height the height of the image to capture
	 * @param rate the framerate
	 * @param device the device to capture from
	 * @throws ImageStreamException If ffmpeg fails.
	 */
	public HackRtpsStream() throws ImageStreamException
	{
		this.width = 320;
		this.height = 240;
		
		try
		{
			String resolution = width + "x" + height;
			String command = "ffmpeg -i rtsp://192.168.1.80/medias1";
			//command += " -s " + resolution;
			command += " -f image2pipe -vcodec bmp -";
			
			ffmpeg = Runtime.getRuntime().exec(command);
			//(new ErrorStreamReader(ffmpeg.getErrorStream(), false)).start();
			pipe = ffmpeg.getInputStream();
		}
		catch (IOException ball)
		{
			throw new ImageStreamException( "Unable to start ffmpeg.", ball);
		}
		catch (SecurityException ball)
		{
			throw new ImageStreamException( "Unable to start ffmpeg.", ball);
		}
	}
	
	public synchronized int[] read()
	{
		System.out.print("r");
		try
		{
			BufferedImage img = ImageIO.read(pipe);
			if (img == null) return null;
			return img.getRGB(0, 0, width, height, null, 0, width);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public int width()  {return width;}
	public int height() {return height;}
}
