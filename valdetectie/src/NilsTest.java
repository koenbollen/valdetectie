import com.tigam.valdetectie.algorithms.BackgroundModel;
import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;

public class NilsTest
{
	public static void main(String... args) throws Exception
	{
		//ImageStream in = new VideoFileImageStream(new File("/home/public/hall_monitor.mpg"), 352, 240);
		ImageStream in = new LinuxDeviceImageStream(320, 240);
		in = new ImageFilterStream(in, GrayScaleFilter.instance);
		
		
		Imager[] imgs = new Imager[5];
		for( int i = 0; i < imgs.length; i++ )
		{
			imgs[i] = new Imager();
			imgs[i].setVisible(true);
			imgs[i].setTitle("Imager " + i);
		}
		
		Utils.PositionImagers(in.width(), in.height(), 50, imgs);

		BackgroundModel bm = new BackgroundModel(in.width(), in.height(), 500);

		int count = 0;

		while( true )
		{
			int[] data1 = in.read();
			if( data1 == null )
				break;
			bm.pushImage(data1);
			
			imgs[0].setImage(Utils.data2image(data1, in.width(), in.height()));
		//	imgs[1].setImage(Utils.data2image(RickTest.doThings(bm.getForeground(data1),in.width(),in.height(),3), in.width(), in.height()));

			count++;
			count %= 100;
			if( count == 0 ){
				bm.refreshBackgroundModel();
				imgs[2].setImage(Utils.data2image(bm.m, in.width(), in.height()));
				imgs[3].setImage(Utils.data2image(bm.n, in.width(), in.height()));
				imgs[4].setImage(Utils.data2image(multiplyArray(bm.d,8), in.width(), in.height()));
				System.out.println("UPDATE!");
			}
		}
	}
	
	public static int [] multiplyArray(int [] arr,int num){
		int [] ar = new int[arr.length];
		for (int i=0; i<ar.length; i++)
			ar[i] = arr[i]*num;
		return ar;
	}
}