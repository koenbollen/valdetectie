import java.io.File;
import java.util.List;
import java.util.Map;

import com.tigam.valdetectie.algorithms.*;
import com.tigam.valdetectie.algorithms.dropdetector.*;
import com.tigam.valdetectie.algorithms.dropdetector.filters.RatioDropFilter;
import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.*;
import com.tigam.valdetectie.streams.filters.*;
import com.tigam.valdetectie.utils.*;
import java.awt.image.BufferedImage;

/**
 * Valdetectie was a prototype developed by 4 students of the HvA
 * indirectly developed for Quovadis Netherlands. It is the implementation
 * of techniques for the detection of falling people by the use of
 * infrared camera's. Anyone interested in this code or technique should contact
 * Tim van Oosterhout <T.J.M.van.Oosterhout@hva.nl> as the maintainer of this project
 *
 * Valdetectie test movies can be found on:
 *     http://oege.ie.hva.nl/~bollen05/in01.mpg
 *     http://oege.ie.hva.nl/~bollen05/in02.mpg
 *     http://oege.ie.hva.nl/~bollen05/in03.mpg
 *
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class Valdetectie
{
	public static void main(String[] args) throws Exception
	{
		/*
		File f = new File(args[0]);
		ImageStream in = null;
		try
		{
			in = new VideoFileImageStream(f, 320, 240);
		}
		catch (ImageStreamException e)
		{
			Throwable cause = e;
			while(cause != null)
			{
				System.out.println(cause.getMessage());
				cause = cause.getCause();
			}
			System.exit(-1);
		}
		*/
		
		//ImageStream in = new HackRtpsStream();
		ImageStream in = new CaptureDeviceStream(320, 240);
		in = new DuplicateDropperStream(in);
		//in = new RateLimitImageStream(in);

		// make the input gray to simulate a infrared camera
		in = new ImageFilterStream(in, GrayScaleFilter.instance);
		
		// initialize the background model based on a GaussionModel
		GaussianModel model = new GaussianModel(in.width(), in.height(), 4, 0.005);
		// initialize a shadowDetector unit to remove shadow from moving pixels for better tracking
		ShadowDetector shadowDetector = new ShadowDetector(in.width(), in.height());

		int[] img;
		int[] kernels;
		int[] bg;
		int[] fg;
		int[] var;
		int[] sh = null;

		// create a noise reduction filter by dilating, eroding and dilating the movingparts image
		DilateFilter dilate0 = new DilateFilter(5);
		ErodeFilter erode0 = new ErodeFilter(7);
		DilateFilter dilate1 = new DilateFilter(2);
		ImageFilter noiseFilter = new CompoundImageFilter(dilate0, erode0, dilate1);

		// initialize the tracker for bounding boxes and the detector for falling people
		BoxTracker tracker = new BoxTracker();
		DropDetector dropper = new DropDetector();

		// define the rules for the drop detector, in this case we asume someone falls when the ratio of the box has a delta of 0.38
		// which defines the speed of change of the changing box from standing to falling
		DropFilter filter = new AndDropFilter(new RatioDropFilter(1), new DropFilter() {
			@Override
			public boolean dropped(List<Box> history) {
				if (history.size() < 2)
					return false;
				double delta = history.get(0).ratio() - history.get(1).ratio();
				return delta > 0.38;
			}
		});
		
		DemoWindow demo = new DemoWindow();
		
		int counter = 0;
		int width = in.width();
		int height = in.height();
		
		// do the main loop of the programm detecting falling people
		while ((img = in.read()) != null)
		{
			//System.out.print("l");
			
			// update the gaussian model with the newly received image
			model.update(img);
			
			bg      = model.getMeanModel(); // fetch the an image representing the background and feed it to the display
			fg      = model.foreground(img); // fetch a bitmap of the foreground based on the feed and the gaussian mixture model
			kernels = model.getKernelCountImage(); // fecth an image representing the kernel count and display it because its awsome to see
			var     = model.getVarianceImage();
			
			demo.show(1, data2image(img, width, height));
			demo.show(2, data2image(bg, width, height));
			demo.show(3, data2image(var, width, height));
			demo.show(4, data2image(fg, width, height));
			demo.show(5, data2image(kernels, width, height));
			
			
			// if the ratio of pixels in the foreground is bigger than 0.75 or 75% skip the shadow
			// detection because we asume a big change in light, because the shadow detection is
			// time consuming we better skip this step.
			if (model.getRatio() < 0.75) {
				sh = shadowDetector.shadow(img, bg, fg);
			} else if (sh == null)
				sh = new int[img.length];

			// after calculating the shadow pixels we show it to the user
			display.image(4, sh);

			// substract the shadow pixels from the foreground, also mask the image with the shadow pixels (it is the last channel so it will apear as blue)
			for (int i = fg.length; i-- > 0;) {
				if (sh[i] != 0) {
					fg[i] = 0;
					img[i] = img[i] | 0xff;
				}
			}
			
			
			// apply the noise filter on the foreground to fill up holes inside the moving object and possibly merge two objects close to eachother
			fg = noiseFilter.applyFilter(fg, in.width(), in.height());

			// extract bounding boxes from foreground
			List<Box> boxes = BoundingBoxExtractor.extractBoxes(fg, in.width(), 1024);

			// track the extracted bounding boxes against the previous boxes and label them with a number
			Map<Box, Integer> tracked = tracker.track(boxes);
			
			// update the dropdtector with the tracked boxes and test the biggest box his history to the dropfilter
			dropper.update(tracked);
			Box biggestDrop = Utils.sortedFirst(dropper.testDrop(filter), Box.SurfaceComperator);
			if (biggestDrop != null && biggestDrop.equals(Utils.sortedFirst(tracked.keySet(), Box.SurfaceComperator))) alarm();

			// mask all moving parts in the feed with red
			fg = ColorFilter.red.applyFilter(fg, in.width(), in.height());
			for (int i = fg.length; i-- > 0;) img[i] = img[i] | fg[i];

			// draw the tracked boundingboxes
			for (Map.Entry<Box, Integer> e : tracked.entrySet())
				BoundingBoxExtractor.boundingBoxDrawerer(img, in.width(), e.getKey(), colour(e.getValue()));

			// display the final feed to the screen
			demo.show(0, data2image(img, width, height));
			//display.save(img, "file" + counter++ + ".png");
			
			System.out.print(".");
		}
	}
	
	public static BufferedImage data2image(int[] data, int width, int height)
	{
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width, height, data, 0, width);
		return result;
	}

	static int[] colours = new int[] { 0xFF0000, 0x0000FF, 0x00FF00, 0xD80000, 0xD8CB00, 0x00D8CB, 0x0056D8, 0x8E00D8, 0xD800C6, 0xC40000, 0xFF4040, 0xFF9B9B, 0xA09BFF, 0xA09BFF, 0x0A00C6 };

	public static int colour(int index) {
		return colours[index % colours.length];
	}
	
	static SerialSwitch light = new SerialSwitch(19);
	
	public static void alarm()
	{
		// it we detect a person falling flash the screen, in production this could be replaced with a network invocation to the owner of the system
		//Utils.flash();
		System.out.println("FAlL!");
		Timer.go();
	}
	
	static Timer instance = new Timer();
	static class Timer implements Runnable
	{
		long stopTime;
		boolean active;
		
		static synchronized void go()
		{
			if (instance.active)
			{
				instance.extend();
			}
			else
			{
				Thread thread = new Thread(instance);
				instance.extend();
				thread.start();
			}
		}
		
		public void extend()
		{
			active = true;
			stopTime = System.currentTimeMillis() + 2000;
		}
		
		public void run()
		{
			light.setEnabled(true);
			try
			{
				long now;
				do
				{
					now = System.currentTimeMillis();
					if (now < stopTime) Thread.currentThread().sleep(stopTime - now);
				} while (now < stopTime);
			}
			catch (InterruptedException _) {}
			active = false;
			light.setEnabled(false);
		}
	}
}
