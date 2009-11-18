package com.tigam.valdetectie.streams;

import static java.lang.Math.max;


/**
 * RateLimitImageStream.
 * 
 * This is the <i>framerate</i> Limiter ImageStream. It'll wrap any given {@link ImageStream} and 
 * keep the image that {@link RateLimitImageStream#read()} return limited to a specified
 * framerate. 
 * 
 * This means that; One: if read() is called to fast after the last call the method will
 * block to keep the rate level. And two: if read() is called with a longer delay then 
 * the framerate suggested it'll skip frames to keep in sync with the wrapped ImageStream.
 * 
 * So, this limit is both ways.
 *
 * @since 23 September, 2009
 * @author Koen Bollen
 */
public class RateLimitImageStream implements ImageStream
{
	/** Default frame rate.
	 */
	public static final int DEFAULT_RATE = 24;
	
	private final ImageStream stream;
	private final int width;
	private final int height;
	private final int rate;
	private final long delay;
	
	private long last;

	/**
	 * Create a new {@link RateLimitImageStream} from an existing {@link ImageStream} with
	 * the default framerate of {@link RateLimitImageStream#DEFAULT_RATE}.
	 * 
	 * @param stream The {@link ImageStream} to wrap.
	 */
	public RateLimitImageStream( ImageStream stream )
	{
		this( stream, RateLimitImageStream.DEFAULT_RATE );
	}
	
	/**
	 * Create a new {@link RateLimitImageStream} from an existing {@link ImageStream} with a given
	 * framerate.
	 * 
	 * @param stream The {@link ImageStream} to wrap.
	 * @param rate The framerate (in number of fps)
	 */
	public RateLimitImageStream( ImageStream stream, int rate )
	{
		this.stream = stream;
		this.width = stream.width();
		this.height = stream.height();
		this.rate = max( 1, rate );
		this.delay = 1000/this.rate;
		
		this.last = -1;
	}
	
	/**
	 * Returns the next image but keeps in account the set framerate, so it might skip frames or block a number
	 * of milliseconds to keep the framerate leveled and synced.
	 */
	@Override
	public int[] read()
	{
		int[] img = this.stream.read();
		if( img == null )
			return null;
		
		long now = time();
		
		// Return the first image directly:
		if( this.last == -1 )
		{
			this.last = now;
			return img;
		}
		
		long elapsed = max( 0, now-this.last );
		if( elapsed > this.delay )
		{
			long skip = elapsed / this.delay;
			System.out.println( "Skipping " + skip + " frames." );
			for( int i = 0; i < skip; i++ )
				if( (img = this.stream.read()) == null )
					return null;
			elapsed %= this.delay;
		}
		
		System.out.println( "Sleeping " + (this.delay - elapsed) + " milliseconds." );
		sleep( this.delay - elapsed );
		this.last = now;
		
		return img;
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
	
	/**
	 * Returns the current time in milliseconds.
	 * 
	 * @return The current time.
	 */
	private static long time()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 * Sleep for a given number of milliseconds.
	 * 
	 * @param millis The number of milliseconds to sleep.
	 */
	private static void sleep( long millis )
	{
		try
        {
	        Thread.sleep( millis );
        }
        catch( InterruptedException e )
        {
        }
	}

}
