package com.tigam.valdetectie.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		if( verbose )
		{
			while( !isInterrupted() )
			{
				try
				{
					System.err.println(reader.readLine());
	
				} catch( IOException e )
				{
					e.printStackTrace();
					break;
				}
			}
		}
		else
		{
			while( !isInterrupted() )
			{
				try
                {
	                reader.readLine();
                }
                catch( IOException e )
                {
	                break;
                }
			}
		}
	}
}
