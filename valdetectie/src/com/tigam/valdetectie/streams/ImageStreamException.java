package com.tigam.valdetectie.streams;

public class ImageStreamException extends Exception
{
	private static final long serialVersionUID = -6103231814662978671L;

	public ImageStreamException()
	{
		this( "ImageStream failed" );
	}

	public ImageStreamException( String message )
	{
		super( message );
	}

	public ImageStreamException( String message, Throwable cause )
	{
		super( message, cause );
	}

}
