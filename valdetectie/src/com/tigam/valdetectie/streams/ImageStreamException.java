package com.tigam.valdetectie.streams;

/**
 * This exception is thrown when an {@link ImageStream} fails in an operation.
 *  
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
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
