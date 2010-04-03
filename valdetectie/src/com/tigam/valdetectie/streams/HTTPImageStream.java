package com.tigam.valdetectie.streams;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Formatter;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.utils.Utils;

/**
 * HTTPImageStream reads images from a webserver.
 * 
 * When constructing simply give a http url and each read invocation will
 * download the image again.
 * 
 * When width and height isn't given on the constructor an initial image 
 * is downloaded in the constructor to determine the image size.
 * 
 * The given url may contain one %d argument for a {@link Formatter} that will be replaced
 * by a counter to work around caching.
 * 
 * @since 2009-11-23
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class HTTPImageStream 
	implements ImageStream
{
	private URL url;
	private int width;
	private int height;
	private int counter;
	
	public HTTPImageStream( String url ) 
		throws MalformedURLException, IOException
	{
		this( new URL( url ) );
	}
	
	public HTTPImageStream( URL url )
	{
		this( url, 0, 0 );
		this.read();
	}

	public HTTPImageStream( String url, int width, int height ) 
		throws MalformedURLException
	{
		this( new URL( url ), width, height );
	}
	
	public HTTPImageStream( URL url, int width, int height )
	{
		this.url = url;
		this.width = width;
		this.height = height;
		this.counter = 0;
	}

	@Override
	public int[] read()
	{
		BufferedImage img;
		try {
			URL local;
			local = new URL( String.format( this.url.toString(), this.counter ) );
			//System.out.println(local);
			this.counter = (this.counter + 1) % Integer.MAX_VALUE; 
			img = ImageIO.read( local );
			if( img == null )
				return null;
			if( img.getWidth() != this.width )
				this.width = img.getWidth();
			if( img.getHeight() != this.height )
				this.height = img.getHeight();
			return img.getRGB(0, 0, this.width, this.height, null, 0, this.width);
		} catch (IOException e) {
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
	
	@Override
	public String toString() {
		return "HTTPImageStream [url=" + url 
			+  ", width=" + width 
			+  ", height=" + height + "]";
	}
	
    public URL getUrl()
    {
    	return this.url;
    }

    public void setUrl( URL url )
    {
    	this.url = url;
    }

	@Deprecated
	public static void main(String[] args) throws Exception
	{
		//*/ // Remove the first slash to enable second block of code.
		ImageStream in = new HTTPImageStream( "http://www.google.com/intl/en_ALL/images/logo.gif" );
		System.out.println( in );
		Utils.showImage( Utils.data2image(in.read(), in.width(), in.height()) );
		/*/
		ImageStream in = new HTTPImageStream( "http://localhost/image%04d.jpg", 32, 32 );
		for( int i = 0; i < 1000; i++ )
			in.read();
		//*/
	}

}