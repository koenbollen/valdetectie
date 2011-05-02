package com.tigam.valdetectie.streams;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.utils.ErrorStreamReader;

/**
 * An implementation of {@link ImageStream} to capture frames from a webcam in linux
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class CaptureDeviceStream implements ImageStream
{
	
	public static final String DEFAULT_DEVICE = "/dev/video0";
	
	/**
	 * the width of the image to capture
	 * on default it is set to 1024
	 */
	private final int width;
	
	/**
	 * the height of the image to capture
	 * on default it is set to 768 
	 */
	private final int height;
	
	/**
	 * the device to capture
	 * on default it is set to /dev/video0
	 */
	private final String device;
	
	/**
	 * the internal stream to read the images from
	 */
	private InputStream imageInput;
	
	/**
	 * create a new instance of {@link LinuxDeviceImageStream} with the default parameters
	 * @throws ImageStreamException If ffmpeg fails. 
	 */
	public CaptureDeviceStream() throws ImageStreamException{
		this (1024, 768);
	}
	
	/**
	 * Create a new instance of {@link LinuxDeviceImageStream} with a specified width and height
	 * @param width the width of the image to capture
	 * @param height the height of the image to capture
	 * @throws ImageStreamException If ffmpeg fails.
	 */
	public CaptureDeviceStream(int width, int height) throws ImageStreamException{
		this(width,height,24);
	}
	
	/**
	 * Create a new instance of {@link LinuxDeviceImageStream} with a specified width and height
	 * @param width the width of the image to capture
	 * @param height the height of the image to capture
	 * @param rate the framerate
	 * @throws ImageStreamException If ffmpeg fails.
	 */
	public CaptureDeviceStream(int width, int height, int rate) throws ImageStreamException{
		this(width,height,rate,DEFAULT_DEVICE);
	}
	
	/**
	 * Create a new instance of {@link LinuxDeviceImageStream} with a specified width, height and devicce to capture from
	 * @param width the width of the image to capture
	 * @param height the height of the image to capture
	 * @param rate the framerate
	 * @param device the device to capture from
	 * @throws ImageStreamException If ffmpeg fails.
	 */
	public CaptureDeviceStream(int width, int height, int rate, String device) throws ImageStreamException
	{
		this.width = width;
		this.height = height;
		this.device = device;
		
		InputStream in = null;
		try
		{
			String resolution = this.width + "x" + this.height;
			String command = "ffmpeg -v 0 -s " + resolution + " -r " + rate;
			if (System.getProperty("os.name").startsWith("Windows")) command += " -f vfwcap -i 0";
			else command += " -f video4linux2 -i " + this.device;
			command += " -f image2pipe -vcodec bmp -";
			Process cam = Runtime.getRuntime().exec(command);
			(new ErrorStreamReader(cam.getErrorStream(),false)).start();
			in = cam.getInputStream();
		}
		catch (IOException ball){throw new ImageStreamException( "Unable to start ffmpeg.", ball);}
		catch (SecurityException ball){throw new ImageStreamException( "Unable to start ffmpeg.", ball);}
		this.imageInput = in;
		
		// skip first frames
		for (int i=0; i<10; i++) this.read();
	}
	
	@Override
	public synchronized int[] read()
	{
		try {
			BufferedImage img = ImageIO.read(this.imageInput);
			if( img == null )
				return null;
			return img.getRGB(0, 0, this.width, this.height, null, 0, this.width);
		} catch (IOException e)	{
			return null;
		}
	}

	@Override
	public int width()
	{
		return this.width;
	}
	
	@Override
	public int height()
	{
		return this.height;
	}
}
