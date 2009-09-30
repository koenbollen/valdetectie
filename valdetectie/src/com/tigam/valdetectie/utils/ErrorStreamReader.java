package com.tigam.valdetectie.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ErrorStreamReader extends Thread
{

	private final BufferedReader reader;

	public ErrorStreamReader(InputStream in)
	{
		reader = new BufferedReader(new InputStreamReader(in));
		setDaemon(true);
	}

	public void run()
	{
		while( !isInterrupted() )
		{
			try
			{
				System.err.println(reader.readLine());

			} catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
