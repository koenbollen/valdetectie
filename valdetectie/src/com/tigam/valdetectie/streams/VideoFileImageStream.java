package com.tigam.valdetectie.streams;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.utils.ErrorStreamReader;

/**
 * An implementation of {@link ImageStream} that reads a video file.
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class VideoFileImageStream implements ImageStream
{
	public static final String Path = (System.getProperty("os.name").startsWith("Windows")) ? ".\\" : "/opt/local/bin/";
	public final File file;

	private InputStream in;
	private final int width;
	private final int height;
	
	public VideoFileImageStream(File file) throws ImageStreamException, IOException
	{
		this (file, 320, 240);
	}

	public VideoFileImageStream(File file, int width, int height) throws ImageStreamException, IOException
	{
		this.file = file;
		this.width = width;
		this.height = height;

		if( !file.exists() )
			throw new IOException("No such file or directory.");

		try
		{
			String resolution = width+"x"+height;
			String command = "ffmpeg -v 0 -i " + file + " -s "+resolution+" -f image2pipe -vcodec bmp -";
			System.out.println(command);

			Process cam = Runtime.getRuntime().exec(command);
			in = cam.getInputStream();
			(new ErrorStreamReader(cam.getErrorStream(), true)).start();
		} catch( IOException ball )
		{
			throw new ImageStreamException("Unable to start ffmpeg.", ball);
		} catch( SecurityException ball )
		{
			throw new ImageStreamException("Unable to start ffmpeg.", ball);
		}

	}

	@Override
	public synchronized int[] read()
	{
		try
		{
			BufferedImage img = ImageIO.read(this.in);
			if( img == null )
				return null;
			int [] imgar = img.getRGB(0, 0, this.width, this.height, null, 0, this.width);
//			Utils.showImage(Utils.data2image(imgar, this.width, this.height));
			return imgar;
		} catch( IOException e )
		{
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
	
	class PrintingInputStream extends InputStream {
		
		final InputStream in;
		public PrintingInputStream(InputStream in){
			this.in = in;
		}

		@Override
		public int read() throws IOException {
			int i = in.read();
			System.out.print(i);
			return i;
		}
	}

}
