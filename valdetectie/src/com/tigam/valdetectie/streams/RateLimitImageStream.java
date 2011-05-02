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
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
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
	
	private long previousTime;

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
		
		this.previousTime = -1;
	}
	
	/**
	 * Returns the next image but keeps in account the set framerate, so it might skip frames or block a number
	 * of milliseconds to keep the framerate leveled and synced.
	 */
	@Override
	public int[] read()
	{
		int[] img = this.stream.read();
		long now = System.currentTimeMillis();
		
		// Return the first image directly:
		if(previousTime > 0)
		{
			long elapsed = max( 0, now - previousTime);
			
			if (elapsed < this.delay) sleep(this.delay - elapsed);
			else
			{
				// It's okay to have some time left, but skip frames if enough time has passed to read another frame
				while(elapsed >= 2 * this.delay )
				{
					System.out.println("#");
					img = this.stream.read();
					elapsed -= this.delay;
				}
			}
		}
		previousTime = now;
		return img;
	}
	
	public int width()  {return this.width;}
	public int height() {return this.height;}
	
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
