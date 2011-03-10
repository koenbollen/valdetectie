import java.io.File;
import java.util.List;
import java.util.Map;

import com.tigam.valdetectie.algorithms.BoundingBoxExtractor;
import com.tigam.valdetectie.algorithms.BoxTracker;
import com.tigam.valdetectie.algorithms.ShadowDetector;
import com.tigam.valdetectie.algorithms.dropdetector.AndDropFilter;
import com.tigam.valdetectie.algorithms.dropdetector.DropDetector;
import com.tigam.valdetectie.algorithms.dropdetector.DropFilter;
import com.tigam.valdetectie.algorithms.dropdetector.filters.RatioDropFilter;
import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.*;
import com.tigam.valdetectie.streams.filters.*;
import com.tigam.valdetectie.utils.Box;
import com.tigam.valdetectie.utils.ImageDisplay;
import com.tigam.valdetectie.utils.Utils;
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
public class Valdetectie {

	public static void main(String[] args) throws Exception {
		// Settings.instance.show();
		//*/ Comment toggle for webcam (Linux Device Stream) of file
		File f;
		if (args.length == 0) // if no file is provided use in01.mpg
			f = new File("in03.mpg");
		else
			f = new File(args[0]);
		ImageStream in = null;
		try
		{
			//in = new VideoFileImageStream(f, 160, 120);
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
		//in = new RateLimitImageStream(in);
		/*/
		ImageStream in = new LinuxDeviceImageStream(320, 240);
		in = new FrameDropImageStream(in);
		//*/

		// make the input gray to simulate a infrared camera
		in = new ImageFilterStream(in, GrayScaleFilter.instance);

		// create a window to display different image stream feeds
		ImageDisplay display = new ImageDisplay(in.width(), in.height(), 5);

		// initialize the background model based on a GaussionModel
		GaussianModel model = new GaussianModel(in.width(), in.height(), 8, 1 / 30000.0);
		// initialize a shadowDetector unit to remove shadow from moving pixels for better tracking
		ShadowDetector shadowDetector = new ShadowDetector(in.width(), in.height());

		int[] img;
		int[] tmp;
		int[] bg;
		int[] fg;
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

		// focus stream 5 on the display so it is drawed the biggest
		display.focus(5);
		display.update();
		
		int counter = 0;
		
		// do the main loop of the programm detecting falling people
		while ((img = in.read()) != null) {
			// display the raw image feed
			display.image(0, img);

			// update the gaussian model with the newly received image
			model.update(img);

			// fetch the an image representing the background and feed it to the display
			bg = model.getMeanModel();
			display.image(1, bg);
			
			// fecth an image representing the kernel count and display it because its awsome to see
			tmp = model.getKernelCountImage();
			display.image(2, tmp);
			
			// fetch a bitmap of the foreground based on the feed and the gaussian mixture model
			fg = model.foreground(img);
			display.image(3, fg);

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
			List<Box> boxes = BoundingBoxExtractor.extractBoxes(fg, in.width());

			// track the extracted bounding boxes against the previous boxes and label them with a number
			Map<Box, Integer> tracked = tracker.track(boxes);
			
			// update the dropdtector with the tracked boxes and test the biggest box his history to the dropfilter
			dropper.update(tracked);
			Box biggestDrop = Utils.sortedFirst(dropper.testDrop(filter), Box.SurfaceComperator);
			if (biggestDrop != null && biggestDrop.equals(Utils.sortedFirst(tracked.keySet(), Box.SurfaceComperator)))
				// it we detect a person falling flash the screen, in production this could be replaced with a network invocation to the owner of the system
				Utils.flash();

			// mask all moving parts in the feed with red
			fg = ColorFilter.red.applyFilter(fg, in.width(), in.height());
			for (int i = fg.length; i-- > 0;)
				img[i] = img[i] | fg[i];

			// draw the tracked boundingboxes
			for (Map.Entry<Box, Integer> e : tracked.entrySet())
				BoundingBoxExtractor.boundingBoxDrawerer(img, in.width(), e.getKey(), colour(e.getValue()));

			// display the final feed to the screen
			display.image(5, img);
			display.save(img, "file" + counter++ + ".png");

			// update the screen
			display.update();
		}
	}

	static int[] colours = new int[] { 0xFF0000, 0x0000FF, 0x000000, 0xD80000, 0xD8CB00, 0x00D8CB, 0x0056D8, 0x8E00D8, 0xD800C6, 0xC40000, 0xFF4040, 0xFF9B9B, 0xA09BFF, 0xA09BFF, 0x0A00C6 };

	public static int colour(int index) {
		return colours[index % colours.length];
	}

}
