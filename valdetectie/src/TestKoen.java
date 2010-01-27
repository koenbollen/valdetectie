import java.io.File;
import java.util.List;
import java.util.Map;

import com.tigam.valdetectie.algorithms.BoundingBoxExtractor;
import com.tigam.valdetectie.algorithms.NilsHisBoxTracker;
import com.tigam.valdetectie.algorithms.Settings;
import com.tigam.valdetectie.algorithms.ShadowDetector;
import com.tigam.valdetectie.algorithms.dropdetector.AndDropFilter;
import com.tigam.valdetectie.algorithms.dropdetector.DropDetector;
import com.tigam.valdetectie.algorithms.dropdetector.DropFilter;
import com.tigam.valdetectie.algorithms.dropdetector.filters.RatioDropFilter;
import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.FrameDropImageStream;
import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.RateLimitImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.streams.filters.ColorFilter;
import com.tigam.valdetectie.streams.filters.CompoundImageFilter;
import com.tigam.valdetectie.streams.filters.DilateFilter;
import com.tigam.valdetectie.streams.filters.ErodeFilter;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.streams.filters.ImageFilter;
import com.tigam.valdetectie.utils.Box;
import com.tigam.valdetectie.utils.Grapher;
import com.tigam.valdetectie.utils.ImageDisplay;
import com.tigam.valdetectie.utils.Utils;

public class TestKoen
{

	public static void main(String[] args) throws Exception
	{
		Settings.instance.show();

		File f;
		VideoFileImageStream x;
		LinuxDeviceImageStream x2;

		 ImageStream in = new VideoFileImageStream( new
		 File("in03.mpg" ), 320, 240 );
		// ImageStream in = new VideoFileImageStream( new
		// File("/home/public/hall_monitor.mpg" ), 320/2, 240/2 );

		// */
		//ImageStream in = new LinuxDeviceImageStream(320, 240);
		//in = new FrameDropImageStream(in);
		/*
		 * / ImageStream in = new LinuxDeviceImageStream(320, 240); in = new
		 * FrameDropImageStream(in); //
		 */

		in = new ImageFilterStream(in, GrayScaleFilter.instance);
		//in = new FrameDropImageStream(in);
		in = new RateLimitImageStream(in, 24);

		ImageDisplay display = new ImageDisplay(in.width(), in.height(), 5);

		GaussianModel model = new GaussianModel(in.width(), in.height(), 8,
				1 / 3000.0);
		ShadowDetector shadowDetector = new ShadowDetector(in.width(), in
				.height());

		int[] img;
		int[] tmp;
		int[] bg;
		int[] fg;
		int[] sh = null;

		// */
		DilateFilter dilate0 = new DilateFilter(5);
		ErodeFilter erode0 = new ErodeFilter(7);
		DilateFilter dilate1 = new DilateFilter(2);
		/*
		 * / ErodeFilter dilate0 = new ErodeFilter(2); DilateFilter erode0 = new
		 * DilateFilter(7); ErodeFilter dilate1 = new ErodeFilter(5); //
		 */
		ImageFilter noiseFilter = new CompoundImageFilter(dilate0, erode0,
				dilate1);

		//BoxTracker tracker = new BoxTracker();
		NilsHisBoxTracker tracker = new NilsHisBoxTracker();
		DropDetector dropper = new DropDetector();
		
		DropFilter filter = new AndDropFilter(
				new RatioDropFilter(1),
				new DropFilter()
		{			
			@Override
			public boolean dropped(List<Box> history)
			{
				if (history.size() < 2)
					return false;
				double delta = history.get(0).ratio() - history.get(1).ratio();
				return delta > 0.4;
			}
		});
		
		Grapher grapher = new Grapher();
		long lastadd = 0;

		display.focus(5);
		display.update();
		while( (img = in.read()) != null )
		{
			display.image(0, img);

			model.update(img);

			bg = model.getMeanModel();
			display.image(1, bg);
			tmp = model.getKernelCountImage();
			display.image(2, tmp);
			fg = model.foreground(img);
			display.image(3, fg);

			if( model.getRatio() < 0.75 )
			{
				sh = shadowDetector.shadow(img, bg, fg);
			} else if( sh == null )
				sh = new int[img.length];
			display.image(4, sh);

			for( int i = fg.length; i-- > 0; )
			{
				if( sh[i] != 0 )
				{
					fg[i] = 0;
					img[i] = img[i] | 0xff;
				}
			}

			// */
			fg = noiseFilter.applyFilter(fg, in.width(), in.height());
			// extract bounding boxes from foreground
			 List<Box> boxes = BoundingBoxExtractor.extractBoxes(fg, in.width());

//			 boxes = BoundingBoxExtractor.mergeBoxes(boxes);

			 Map<Box, Integer> tracked = tracker.track(boxes);
			 dropper.update(tracked);
			 Box biggestDrop = Utils.sortedFirst(dropper.testDrop(filter), Box.SurfaceComperator);
			 if (biggestDrop != null && biggestDrop.equals(Utils.sortedFirst(tracked.keySet(), Box.SurfaceComperator)))
				 //*/
				 Utils.flash();
			 	 /*/
			 	 System.out.println("NewFilterDrop! " + biggestDrop);
			 	 //*/
			 
			 if( System.currentTimeMillis() - lastadd > 50 )
			 {
				 grapher.add(tracked);
				 lastadd = System.currentTimeMillis();
			 }

			

			fg = ColorFilter.red.applyFilter(fg, in.width(), in.height());
			for( int i = fg.length; i-- > 0; )
				img[i] = img[i] | fg[i];

//			 boxes = BoundingBoxExtractor.mergeBoxes(boxes);
			// draw bounding boxes
//			 for( Box b : boxes )
//				 BoundingBoxExtractor.boundingBoxDrawerer(img, in.width(), b, 0x00FF00);

			 // draw tracked
			 for( Map.Entry<Box, Integer> e : tracked.entrySet() )
				 BoundingBoxExtractor.boundingBoxDrawerer(img, in.width(), e.getKey(), colour(e.getValue()));

			display.image(5, img);

			/*
			 * / fg = noiseFilter.applyFilter(fg, in.width(), in.height()); img
			 * = ColorFilter.red.applyFilter(img, in.width(), in.height()); for(
			 * int i = fg.length; i --> 0; ) img[i] = img[i] & fg[i]; fg =
			 * InvertFilter.instance.applyFilter(fg, in.width(), in.height());
			 * for( int i = fg.length; i --> 0; ) img[i] = img[i] | fg[i];
			 * imgs[5].setImage(Utils.data2image(img, in.width(), in.height()));
			 * //
			 */

			display.update();
		}

		// */
	}

	static int[] colours = new int[]
	{ 0xFF0000, 0x0000FF, 0x000000, 0xD80000, 0xD8CB00, 0x00D8CB, 0x0056D8,
			0x8E00D8, 0xD800C6, 0xC40000, 0xFF4040, 0xFF9B9B, 0xA09BFF,
			0xA09BFF, 0x0A00C6 };

	public static int colour(int index)
	{
		return colours[index % colours.length];
	}

}
