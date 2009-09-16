package com.tigam.valdetectie.streams;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * An implementation of {@link ImageStream} to capture frames from a webcam in linux
 * 
 * @author Nils Dijk
 */
public class LinuxDeviceImageStream implements ImageStream
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
	public LinuxDeviceImageStream() throws ImageStreamException{
		this (1024, 768);
	}
	
	/**
	 * Create a new instance of {@link LinuxDeviceImageStream} with a specified width and height
	 * @param width the width of the image to capture
	 * @param height the height of the image to capture
	 * @throws ImageStreamException If ffmpeg fails.
	 */
	public LinuxDeviceImageStream(int width, int height) throws ImageStreamException{
		this(width,height,24,DEFAULT_DEVICE);
	}
	
	/**
	 * Create a new instance of {@link LinuxDeviceImageStream} with a specified width, height and devicce to capture from
	 * @param width the width of the image to capture
	 * @param height the height of the image to capture
	 * @param device the device to capture from
	 * @throws ImageStreamException If ffmpeg fails.
	 */
	public LinuxDeviceImageStream(int width, int height, int rate, String device) throws ImageStreamException{
		this.width = width;
		this.height = height;
		this.device = device;
		
		InputStream in = null;
		try {
			String resolution = this.width+"x"+this.height;
			String command = "ffmpeg -f video4linux2 -s "+resolution+" -i " + this.device + " -r "+rate+" -s "+resolution+" -f image2pipe -vcodec bmp -";
			
			Process cam = Runtime.getRuntime().exec(command);
			in = cam.getInputStream();
		} catch (IOException ball){
			throw new ImageStreamException( "Unable to start ffmpeg.", ball );
		} catch (SecurityException ball){
			throw new ImageStreamException( "Unable to start ffmpeg.", ball );
		}
		this.imageInput = in;
		
	}
	
	@Override
	public synchronized Image read()
	{
		try {
			return ImageIO.read(this.imageInput);
		} catch (IOException e)	{
			return null;
		}
	}

}
