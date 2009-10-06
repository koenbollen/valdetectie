import java.io.File;

import com.tigam.valdetectie.algorithms.BackgroundModel;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;


public class NilsTest {
	public static void main(String...args) throws Exception {
		ImageStream in = new VideoFileImageStream(new File("/home/public/hall_monitor.mpg"));
		
		Imager img1 = new Imager();
		img1.setVisible(true);
		
		Imager img2 = new Imager();
		img2.setVisible(true);
		
		BackgroundModel bm = new BackgroundModel(in.width(),in.height());
		while (true){
			int [] data1 = in.read();
			if (data1 == null) break;
			int [] data2 = bm.pushImage(data1);
			
			img1.setImage(Utils.data2image(data1, in.width(), in.height()));
			img2.setImage(Utils.data2image(data2, in.width(), in.height()));
		}
	}
}
