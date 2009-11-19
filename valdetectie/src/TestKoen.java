import java.io.File;

import com.tigam.valdetectie.algorithms.ShadowDetector;
import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.DropImageStream;
import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.streams.filters.CompoundImageFilter;
import com.tigam.valdetectie.streams.filters.DilateFilter;
import com.tigam.valdetectie.streams.filters.ErodeFilter;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.streams.filters.ImageFilter;
import com.tigam.valdetectie.streams.filters.ColorFilter;
import com.tigam.valdetectie.streams.filters.InvertFilter;
import com.tigam.valdetectie.streams.filters.NeighborDifferenceFilter;
import com.tigam.valdetectie.utils.ImageDisplay;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;

public class TestKoen
{

	public static void main(String[] args) throws Exception
	{
		File f;
		VideoFileImageStream x;
		LinuxDeviceImageStream x2;
		
		//ImageStream in = new VideoFileImageStream( new File("/home/public/hall_monitor.mpg" ), 320, 240 );
		//ImageStream in = new VideoFileImageStream( new File("/home/public/hall_monitor.mpg" ), 320/2, 240/2 );

		/*/
		ImageStream in = new LinuxDeviceImageStream(320 / 2, 240 / 2);
		in = new DropImageStream(in);
		/*/
		ImageStream in = new LinuxDeviceImageStream(320, 240);
		in = new DropImageStream(in);
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

		DilateFilter dilate0 = new DilateFilter(5);
		ErodeFilter erode0 = new ErodeFilter(7);
		DilateFilter dilate1 = new DilateFilter(2);
		ImageFilter noiseFilter = new CompoundImageFilter(dilate0, erode0,dilate1);

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

			if(model.getRatio() < 0.75 )
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
			fg = ColorFilter.red.applyFilter(fg, in.width(), in.height());
			for( int i = fg.length; i --> 0; )
				img[i] = img[i] | fg[i];
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
