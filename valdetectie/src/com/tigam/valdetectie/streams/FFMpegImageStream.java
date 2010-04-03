package com.tigam.valdetectie.streams;

import java.io.DataInputStream;
import java.io.IOException;

import com.tigam.valdetectie.utils.ErrorStreamReader;
/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public abstract class FFMpegImageStream implements ImageStream
{
	protected String input;

	protected final int width;
	protected final int height;
	protected final int rate;

	private boolean initialized;
	
	private Process proc;
	private DataInputStream in;
	
	public FFMpegImageStream( String input, int width, int height, int rate )
	{
		this.input = input;
		this.width = width;
		this.height = height;
		this.rate = rate;
		
		initialized = false;

		proc = null;
		in = null;
	}
	
	public final void initialize() 
		throws ImageStreamException
	{
		try
		{
			proc = Runtime.getRuntime().exec( this.command() );
			in = new DataInputStream(proc.getInputStream());
			(new ErrorStreamReader(proc.getErrorStream(), false)).start();
		} catch( IOException e )
		{
			throw new ImageStreamException("Unable to start ffmpeg!", e);
		}
		catch( SecurityException e )
		{
			throw new ImageStreamException("Unable to start ffmpeg!", e);
		}
		initialized = true;
	}
	
	protected String command()
	{
		String resolution = this.width+"x"+this.height;
		return "ffmpeg -v 0 -i "+this.input+" -f image2pipe -vcodec ppm -s "+resolution+" -"; 
	}

	@Override
	public final int[] read()
	{
		if( !initialized )
			throw new RuntimeException("FFMpegImageStream not initialized!");
		
		
		
		return null;
	}

	@Override
	public final int width()
	{
		return this.width;
	}

	@Override
	public final int height()
	{
		return this.height;
	}

}
