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


		Imager[] imgs = new Imager[6];
		for( int i = 0; i < imgs.length; i++ )
		{
			imgs[i] = new Imager();
			imgs[i].setVisible(true);
			imgs[i].setTitle("Imager " + i);
		}
		
		Utils.PositionImagers(in.width(), in.height(), 50, imgs);

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

		while( (img = in.read()) != null )
		{
			//img2 = NeighborDifferenceFilter.instance.applyFilter(img, in.width(), in.height());
			imgs[0].setImage(Utils.data2image(img, in.width(), in.height()));
	
			model.update(img);
			
			bg = model.getMeanModel();
			imgs[1].setImage(Utils.data2image(bg, in.width(), in.height()));
			tmp = model.getKernelCountImage();
			imgs[2].setImage(Utils.data2image(tmp, in.width(), in.height()));
			fg = model.foreground(img);
			imgs[3].setImage(Utils.data2image(fg, in.width(), in.height()));

			if(model.getRatio() < 0.75 )
			{
				sh = shadowDetector.shadow(img, bg, fg);
			}
			else if(sh == null )
				sh = new int[img.length];
			imgs[4].setImage(Utils.data2image(sh, in.width(), in.height()));

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
			imgs[5].setImage(Utils.data2image(img, in.width(), in.height()));
			
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
		}
		imgs[0].setImage(null);

		// */
	}

}
