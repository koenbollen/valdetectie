package com.tigam.valdetectie.algorithms;

import com.tigam.valdetectie.utils.UnionFind;


/**
 * Class containing the static Connected Component Labeling method.
 *
 * @author Koen Bollen
 * @author Nils Dijk
 */
public class CCL
{
	enum Location
	{
		LEFT(-1, 0), TOPLEFT(-1, -1), TOP(0, -1), TOPRIGHT(1, -1);

		public final int x;

		public final int y;

		private Location(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Label all connected non-background pixels.
	 *
	 * @param img The image to test.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @param count Number of forground pixels.
	 */
	public static int[] ccl(int[] img, int width, int height, int count)
	{
		int[] labels = new int[img.length];
		UnionFind linked = new UnionFind(count);

		int nextlabel = 1;

		for( int i = 0; i < img.length; i++ )
		{
			if( img[i] == 0 )
				continue;
			int x = i % width;
			int y = i / width;

			int[] neighbors = neighbors(img, labels, width, height, x, y);

			int closest = Integer.MAX_VALUE;
			for( int j = 0; j < neighbors.length; j++ )
				if( neighbors[j] > 0 )
					closest = Math.min(closest, neighbors[j]);
			if( closest != Integer.MAX_VALUE )
			{
				labels[i] = closest;
				for( int j = 0; j < neighbors.length; j++ )
					if( neighbors[j] > 0 )
						linked.union( closest, neighbors[j] );
			}
			else
			{
				labels[i] = nextlabel;
				linked.find(nextlabel);
				nextlabel++;
			}

		}

		for( int i = 0; i < labels.length; i++ )
		{
			if( labels[i] == 0 )
				continue;
			labels[i] = linked.find(labels[i]);
		}

		return labels;
	}

	private static int[] neighbors(int[] img, int[] labels, int width,
			int height, int x, int y)
	{

		Location[] locations = Location.values();
		int[] closest = new int[locations.length];
		for( int i = 0; i < locations.length; i++ )
			if( n(img, width, height, x, y, locations[i].x, locations[i].y) > 0 )
				closest[i] = n(labels, width, height, x, y, locations[i].x,
						locations[i].y);
		return closest;
	}

	private static int n(int[] img, int width, int height, int x, int y, int n, int m)
	{
		x = x + n;
		y = y + m;
		if( x < 0 || x >= width || y < 0 || y >= height )
			return 0;
		return img[y * width + x];
	}



	/* Test Main
	 */
	@Deprecated
	public static void main(String[] args)
	{
		int[] img = {
				1, 0, 1, 0, 1,
				1, 1, 1, 0, 1,
				0, 1, 0, 0, 0,
				0, 0, 0, 0, 0,
				0, 1, 1, 1, 1,
				0, 0, 0, 1, 1
		};

		int[] res = ccl( img, 5, 6, 13 );
		for( int i = 0; i < res.length; i++ )
		{
			if (i != 0 && i%5 == 0)
				System.out.println();
			System.out.print(res[i] + ", ");
		}
	}

}