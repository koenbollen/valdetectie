import java.io.File;
import java.util.List;

import com.tigam.valdetectie.algorithms.BoundingBoxExtractor;
import com.tigam.valdetectie.algorithms.Settings;
import com.tigam.valdetectie.algorithms.ShadowDetector;
import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.FrameDropImageStream;
import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.streams.filters.ColorFilter;
import com.tigam.valdetectie.streams.filters.CompoundImageFilter;
import com.tigam.valdetectie.streams.filters.DilateFilter;
import com.tigam.valdetectie.streams.filters.ErodeFilter;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.streams.filters.ImageFilter;
import com.tigam.valdetectie.utils.Box;
import com.tigam.valdetectie.utils.ImageDisplay;

public class TestKoen
{

	public static void main(String[] args) throws Exception
	{
		Settings.instance.show();
		
		File f;
		VideoFileImageStream x;
		LinuxDeviceImageStream x2;
		
		//ImageStream in = new VideoFileImageStream( new File("/home/public/hall_monitor.mpg" ), 320, 240 );
		//ImageStream in = new VideoFileImageStream( new File("/home/public/hall_monitor.mpg" ), 320/2, 240/2 );

		//*/
		ImageStream in = new LinuxDeviceImageStream(320 / 2, 240 / 2);
		in = new FrameDropImageStream(in);
		/*/
		ImageStream in = new LinuxDeviceImageStream(320, 240);
		in = new FrameDropImageStream(in);
		//*/

		in = new ImageFilterStream(in, GrayScaleFilter.instance);
		// in = new RateLimitImageStream(in, 24);


		ImageDisplay display = new ImageDisplay(in.width(), in.height(), 5 );

		GaussianModel model = new GaussianModel(in.width(), in.height(), 8, 1 / 3000.0);
		ShadowDetector shadowDetector = new ShadowDetector(in.width(), in.height());

		int[] img;
		int[] tmp;
		int[] bg;
		int[] fg;
		int[] sh = null;

		//*/
		DilateFilter dilate0 = new DilateFilter(5);
		ErodeFilter erode0 = new ErodeFilter(7);
		DilateFilter dilate1 = new DilateFilter(2);
		/*/
		ErodeFilter dilate0 = new ErodeFilter(2);
		DilateFilter erode0 = new DilateFilter(7);
		ErodeFilter dilate1 = new ErodeFilter(5);
		//*/
		ImageFilter noiseFilter = new CompoundImageFilter(dilate0, erode0,dilate1);

		display.focus(5);
		display.update();
		while( (img = in.read()) != null )
		{
			display.image( 0, img );
	
			model.update(img);
			
			bg = model.getMeanModel();
			display.image( 1, bg );
			tmp = model.getKernelCountImage();
			display.image( 2, tmp );
			fg = model.foreground(img);
			display.image( 3, fg );

			if(model.getRatio() < 0.75)
			{
				sh = shadowDetector.shadow(img, bg, fg);
			}
			else if(sh == null )
				sh = new int[img.length];
			display.image( 4, sh );


			for( int i = fg.length; i --> 0; )
			{
				if( sh[i] != 0 )
				{
					fg[i] = 0;
					img[i] = img[i] | 0xff;
				}
			}
			
			
			//*/
			fg = noiseFilter.applyFilter(fg, in.width(), in.height());
			// extract bounding boxes from foreground
			List<Box> boxes = BoundingBoxExtractor.extractBoxes(fg,in.width());
			
			fg = ColorFilter.red.applyFilter(fg, in.width(), in.height());
			for( int i = fg.length; i --> 0; )
				img[i] = img[i] | fg[i];
			
			
			//boxes = BoundingBoxExtractor.mergeBoxes(boxes);
			// draw bounding boxes
			for (Box b:boxes)
				BoundingBoxExtractor.boundingBoxDrawerer(img,in.width(),b,0x00FF00);
			
			display.image( 5, img );

			
			/*/
			fg = noiseFilter.applyFilter(fg, in.width(), in.height());
			img = ColorFilter.red.applyFilter(img, in.width(), in.height());
			for( int i = fg.length; i --> 0; )
				img[i] = img[i] & fg[i];
			fg = InvertFilter.instance.applyFilter(fg, in.width(), in.height());
			for( int i = fg.length; i --> 0; )
				img[i] = img[i] | fg[i];
			imgs[5].setImage(Utils.data2image(img, in.width(), in.height()));
			//*/
			
			display.update();
		}

		// */
	}

}
