import java.io.File;

import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
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

		//*/
		ImageStream in = new LinuxDeviceImageStream(320 / 2, 240 / 2);
		/*/
		ImageStream in = new LinuxDeviceImageStream(320, 240);
		//*/

		in = new ImageFilterStream(in, GrayScaleFilter.instance);
		// in = new RateLimitImageStream(in, 24);

		Imager[] imgs = new Imager[5];
		for( int i = 0; i < imgs.length; i++ )
		{
			imgs[i] = new Imager();
			imgs[i].setVisible(true);
			imgs[i].setTitle("Imager " + i);
		}

		Utils.PositionImagers(in.width(), in.height(), 50, imgs);

		GaussianModel model = new GaussianModel(in.width(), in.height(), 8, 1 / 3000.0);

		int[] img;
		int[] img2;

		DilateFilter dilate0 = new DilateFilter(5);
		ErodeFilter erode0 = new ErodeFilter(7);
		DilateFilter dilate1 = new DilateFilter(2);
		ImageFilter noiseFilter = new CompoundImageFilter(dilate0, erode0,dilate1);

		while( (img = in.read()) != null )
		{
			//img2 = NeighborDifferenceFilter.instance.applyFilter(img, in.width(), in.height());
			imgs[0].setImage(Utils.data2image(img, in.width(), in.height()));
			//*
			model.update(img);
			img2 = model.getMeanImage();
			imgs[1].setImage(Utils.data2image(img2, in.width(), in.height()));
			img2 = model.getKernelCountImage();
			imgs[2].setImage(Utils.data2image(img2, in.width(), in.height()));
			img2 = model.foreground(img);
			imgs[3].setImage(Utils.data2image(img2, in.width(), in.height()));

			img2 = noiseFilter.applyFilter(img2, in.width(), in.height());

			for( int i = img2.length; i --> 0; )
				img[i] = img[i] & img2[i];

			InvertFilter.instance.applyFilter(img2, in.width(), in.height());
			ColorFilter.red.applyFilter(img, in.width(), in.height());

			for( int i = img2.length; i --> 0; )
				img[i] = img[i] | img2[i];

			imgs[4].setImage(Utils.data2image(img, in.width(), in.height()));
			//*/
		}
		imgs[0].setImage(null);

		// */
	}

}
