package com.tigam.valdetectie.streams;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.tools.ImageStreamServer;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;

/**
 * This is a network implementation of {@link ImageStream} that connects to
 * a running {@link ImageStreamServer}.
 * 
 * @author Nils Dijk
 */
public class NetworkImageStream implements ImageStream
{
	private Socket s;
	private InputStream in;

	private int width;
	private int height;
	
	public NetworkImageStream( String host )
		throws UnknownHostException, IOException
	{
		this( host, ImageStreamServer.DEFAULT_PORT );
	}
	
	public NetworkImageStream( String host, int port )
		throws UnknownHostException, IOException
	{
		s = new Socket( host, port );
		in = s.getInputStream();
	}

	public int[] read()
	{
		try {
			BufferedImage img = ImageIO.read( in );
			if( width == 0 )
			{
				this.width = img.getWidth();
				this.height = img.getHeight();
			}
			return Utils.image2data(img);
		} catch (IOException e)	{
			System.out.println( e ); 
			return null;
		}
	}

	@Override
	public int width()
	{
		if (this.width == 0) throw new RuntimeException("Can't get width before the first read.");
		return this.width;
	}
	
	@Override
	public int height()
	{
		if (this.height == 0) throw new RuntimeException("Can't get height before the first read.");
		return this.height;
	}

	/**
	 * This is a simple test main that connects to the given host and displays the frame in 
	 * an {@link Imager} frame.
	 *  
	 * @param args
	 */
	public static void main(String[] args)
	{
		String host = "localhost";
		if( args.length >= 1 )
			host = args[0];
		int port = ImageStreamServer.DEFAULT_PORT;
		try {
			port = Integer.parseInt(args[1]);
		} catch (IndexOutOfBoundsException ball){
		} catch (NumberFormatException ball){
		}

		ImageStream nIn = null;
		try
		{
			nIn = new NetworkImageStream(host, port);
		} catch (Exception e)
		{
			System.err.println( "unable to connect to "+host+":"+port );
			return;
		}
		ImageStream cam = new ImageFilterStream( nIn, GrayScaleFilter.instance );
		Imager imager = new Imager();
		imager.setVisible(true);
		while (true){
			int[] img = cam.read();
			if (img == null)
				break;
			imager.setImage(Utils.data2image(img,cam.width(), cam.height()));
		}
		try
		{
			Thread.sleep( 1000 );
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
