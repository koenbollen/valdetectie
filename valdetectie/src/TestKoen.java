import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;

public class TestKoen
{


	
	public static void main(String[] args) throws Exception
	{		
		VideoFileImageStream x;
		LinuxDeviceImageStream x2;
		//*/
		//ImageStream in = new VideoFileImageStream( new File( "/home/public/hall_monitor.mpg" ), 320, 240 );
		ImageStream in = new LinuxDeviceImageStream(320, 240);

		in = new ImageFilterStream(in, GrayScaleFilter.instance);
		//in = new RateLimitImageStream(in, 24);

		Imager[] imgs = new Imager[2];
		for( int i = 0; i < imgs.length; i++ )
		{
			imgs[i] = new Imager();
			imgs[i].setVisible(true);
			imgs[i].setTitle("Imager " + i);
		}
		
		Utils.PositionImagers(in.width(), in.height(), 50, imgs);

		GaussianModel model = new GaussianModel(320, 240);
		
		int[] img;
		while( (img=in.read()) != null )
		{
			imgs[0].setImage( Utils.data2image(img, in.width(), in.height()) );
			model.update( img );
			img = model.getDebugImage();
			imgs[1].setImage( Utils.data2image(img, in.width(), in.height()) );
		}
		imgs[0].setImage(null);
		
		//*/
	}

}
