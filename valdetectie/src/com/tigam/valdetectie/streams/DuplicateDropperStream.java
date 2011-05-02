package com.tigam.valdetectie.streams;

/**
 * An implementation of {@link ImageStream} that will make sure every read returns
 * a unique result. Use this if some stream returns the same picture more than once.
 * 
 * @author Tim van Oosterhout <T.J.M.van.Oosterhout@hva.nl>
 */
public class DuplicateDropperStream implements ImageStream
{
	ImageStream source;
	int[] lastResult;
	
	public DuplicateDropperStream(ImageStream source)
	{
		this.source = source;
	}
	
	public int[] read()
	{
		int[] newResult = null;
		boolean duplicate = true;
		
		while(duplicate)
		{
			newResult = source.read();
			if (newResult == null)
			{
				if (lastResult != null) duplicate = false;
			}
			else if (lastResult == null) duplicate = false;
			else for (int i = 0; i < newResult.length; i++)
			{
				if (newResult[i] != lastResult[i]) duplicate = false;
			}
			//if (duplicate) System.out.print((newResult == null ? "0" : "-"));
		}
		lastResult = newResult;
		return newResult;
	}

	public int width()  {return source.width();}
	public int height() {return source.height();}
}