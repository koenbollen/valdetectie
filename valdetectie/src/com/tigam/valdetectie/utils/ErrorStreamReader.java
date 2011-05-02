package com.tigam.valdetectie.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class ErrorStreamReader extends Thread
{
	private final BufferedReader reader;
	private final boolean verbose;

	public ErrorStreamReader(InputStream in)
	{
		this( in, true );
	}
	
	public ErrorStreamReader(InputStream in, boolean verbose)
	{
		reader = new BufferedReader(new InputStreamReader(in));
		this.verbose = verbose;
		setDaemon(true);
	}

	public void run()
	{
		String line;
		while(!isInterrupted())
		{
			try
			{
				line = reader.readLine();
				if (verbose && line != null) System.err.println(line);
			}
			catch( IOException e )
			{
				if (verbose) e.printStackTrace();
				break;
			}
		}
	}
}
