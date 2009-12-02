import java.io.File;

import com.tigam.valdetectie.algorithms.gaussian.GaussianModel;
import com.tigam.valdetectie.streams.FrameDropImageStream;
import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.streams.filters.BoxFilter;
import com.tigam.valdetectie.streams.filters.DilateFilter;
import com.tigam.valdetectie.streams.filters.ErodeFilter;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;
 
public class RickTest
{
        public static void main(String[] args) throws Exception
        {              
       		final String screenshotLoc = "/home/seigi/.workspace/valdetectie/src/";
       		final String screenshotName = "Screenshot-f.png";
       		
       		/*/
       		BufferedImage in = ImageIO.read(new File(screenshotLoc + screenshotName));
       		
       		Imager[] imgs = new Imager[2];
       		for( int i = 0; i < imgs.length; i++ )
       		{
    			imgs[i] = new Imager();
    			imgs[i].setVisible(true);
    			imgs[i].setTitle("Imager " + i);
    		}
    		
    		Utils.PositionImagers(in.getWidth(), in.getHeight(), 50, imgs);
    		
    		int[] img = Utils.image2data(in);
    		
       		imgs[0].setImage(Utils.data2image(img, in.getWidth(), in.getHeight()));
       		img = BoxFilter.instance.applyFilter(img, in.getWidth(), in.getHeight());
       		imgs[1].setImage(Utils.data2image(img, in.getWidth(), in.getHeight()));
       		/*/
       		
       		
       		File f;
    		VideoFileImageStream x;
    		LinuxDeviceImageStream x2;
    		//*
    		ImageStream in = new VideoFileImageStream( new File( "/home/public/hall_monitor.mpg" ), 320, 240 );
    		//ImageStream in = new VideoFileImageStream( new File( "/home/public/hall_monitor.mpg" ), 320/2, 240/2 );
    		//ImageStream in = new LinuxDeviceImageStream(320/2, 240/2);
    		in = new FrameDropImageStream(in);
    		in = new ImageFilterStream(in, GrayScaleFilter.instance);
    		//in = new RateLimitImageStream(in, 24);

    		Imager[] imgs = new Imager[6];
    		for( int i = 0; i < imgs.length; i++ )
    		{
    			imgs[i] = new Imager();
    			imgs[i].setVisible(true);
    			imgs[i].setTitle("Imager " + i);
    		}
    		
    		Utils.PositionImagers(in.width(), in.height(), 50, imgs);

    		GaussianModel model = new GaussianModel(in.width(), in.height(), 4, 1/3000.0);

    		int[] img;
    		int[] img2;
    		//*
    		DilateFilter dilate0 = new DilateFilter(2);
    		ErodeFilter erode0 = new ErodeFilter(3);
    		DilateFilter dilate1 = new DilateFilter(1);
    		/*/
    		DilateFilter dilate0 = new DilateFilter(5);
    		ErodeFilter erode0 = new ErodeFilter(7);
    		DilateFilter dilate1 = new DilateFilter(2);
    		//*/
    		while( (img=in.read()) != null )
    		{
    			imgs[0].setImage( Utils.data2image(img, in.width(), in.height()) );
    			model.update( img );
    			img2 = model.getMeanImage();
    			//imgs[1].setImage( Utils.data2image(img2, in.width(), in.height()) );
    			img2 = model.getKernelCountImage();
    			//imgs[2].setImage( Utils.data2image(img2, in.width(), in.height()) );
    			img2 = model.foreground(img);			
    			//imgs[3].setImage( Utils.data2image(img2, in.width(), in.height()) );

    			img2 = dilate0.applyFilter(img2, in.width(), in.height());
    			img2 = erode0.applyFilter(img2, in.width(), in.height());
    			img2 = dilate1.applyFilter(img2, in.width(), in.height());
    			
    			imgs[4].setImage( Utils.data2image(img2, in.width(), in.height()) );
    			
    			img2 = BoxFilter.instance.applyFilter(img2, in.width(), in.height());
    			
    			imgs[5].setImage( Utils.data2image(img2, in.width(), in.height()) );
    		}
    		imgs[0].setImage(null);
    		
    		//*/
        }
        
        private static void printIntArr(int[]data) {
        	printIntArr(data, 0);
        }
        
        private static void printIntArr(int[] data, int width) {
        	if ( data.length > 0) {
	        	System.out.print("[");
	        	if (width > 0)
	        		System.out.println();
	        	for ( int i = 0; i < data.length; i++ ) {
	        		System.out.printf("%x", data[i]);
	        		if (i < data.length-1)
	        			System.out.print( ", ");
	        		if (width > 0 && (i+1) % width == 0)
	        			System.out.println();
	        	}
	        	System.out.println("]");
        	}
        }
}