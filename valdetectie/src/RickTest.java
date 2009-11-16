import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;
 
public class RickTest
{
        public static void main(String[] args) throws Exception
        {              
       		final String screenshotLoc = "/home/seigi/.workspace/valdetectie/src/";
    		final String filenameB = "me_bw.png";
    		
    		/*
    		BufferedImage img = ImageIO.read( new File(screenshotLoc + filenameB) );
    		int[] data = Utils.image2data(img);
    		for (int i = 0; i < data.length; i++) 
				data[i] = ((data[i]&0xffffff) == 0) ? 1 : 0;
			
    		//printIntArr(data, img.getWidth());
    		
    		
    		int width = img.getWidth();
    		int height = img.getHeight();
    		int intensity = 1;
    		double counter = 1000;
    		
    		int max_iter = 10;
    		for ( int iter = 1; iter <= max_iter; iter++){
    			long start = System.currentTimeMillis();
	    		for ( int i = 1; i <= counter; i++) {
	    			dilate(data, width, height, intensity);
		    		erode(data, width, height, intensity * 2);
		    		dilate(data, width, height, intensity);
	    		}
	    		long difference = System.currentTimeMillis() - start;
	    		System.out.println("Difference ("+ iter +"/"+ max_iter +"): ("+ difference +"/"+ counter +") = "+ (difference / counter) +"ms");
    		}
    		
    		//printIntArr(data, img.getWidth());
    		
    		
    		//*/
    		
    		//*
    		int width = 9;
    		int height = 9;
    		int intensity = 1;
    		
    		int[] tmp = {
    					0, 0, 0, 0, 0, 0, 0, 1, 0, 
    					0, 0, 0, 0, 0, 0, 0, 0, 0,
    					0, 0, 1, 1, 1, 0, 0, 0, 0,
    					0, 0, 1, 0, 1, 0, 0, 0, 0,
    					0, 0, 1, 1, 1, 0, 0, 0, 0,
    					0, 0, 0, 0, 0, 1, 1, 1, 1,
    					0, 0, 0, 0, 0, 1, 0, 0, 1,
    					0, 0, 0, 0, 0, 1, 0, 0, 1,
    					0, 0, 0, 0, 0, 1, 1, 1, 1
    		};
    		
    		int[] expectedResult = {
	    				0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    				0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    				0, 0, 1, 1, 1, 0, 0, 0, 0, 
	    				0, 0, 1, 1, 1, 0, 0, 0, 0, 
	    				0, 0, 1, 1, 1, 1, 0, 0, 0, 
	    				0, 0, 0, 0, 1, 1, 1, 1, 1, 
	    				0, 0, 0, 0, 0, 1, 1, 1, 1, 
	    				0, 0, 0, 0, 0, 1, 1, 1, 1, 
	    				0, 0, 0, 0, 0, 1, 1, 1, 1
    		};
    		//*
    		System.out.print("Before: ");
    		printIntArr(tmp, width);
    		//*/
    		
    		//*
    		erode(tmp, width, height, intensity);
    		dilate(tmp, width, height, intensity*2);
    		erode(tmp, width, height, intensity);
    		//*/
    		//*
    		System.out.print("After: ");
    		printIntArr(tmp, width);
    		//*/
    		
			//*/

    		//*
    		//ImageStream in = new VideoFileImageStream( new File( "/home/public/hall_monitor.mpg" ), 320, 240 );
            
            //ImageStream in = new LinuxDeviceImageStream(320, 240);
    		
    		/*
    		//Screenshot-a
    		ImageStream inA = new ImageStream() {public Image read() {BufferedImage bufImg = null;try {bufImg = ImageIO.read(new File(screenshotLoc +"Screenshot-a.png"));} catch (IOException e) {e.printStackTrace();}return bufImg;}};
    		//Screenshot-b Original
    		ImageStream inB = new ImageStream() {public Image read() {BufferedImage bufImg = null;try {bufImg = ImageIO.read(new File(screenshotLoc + filenameB));} catch (IOException e) {e.printStackTrace();}return bufImg;}};
    		//Screenshot-b
    		ImageStream inB2 = new ImageStream() {public Image read() {BufferedImage bufImg = null;try {bufImg = ImageIO.read(new File(screenshotLoc + filenameB));} catch (IOException e) {e.printStackTrace();}return bufImg;}};
    		//Screenshot-c
    		ImageStream inC = new ImageStream() {public Image read() {BufferedImage bufImg = null;try {bufImg = ImageIO.read(new File(screenshotLoc +"Screenshot-c.png"));} catch (IOException e) {e.printStackTrace();}return bufImg;}};
           	//*/
    		
            //in = new GrayScaleImageStream(in);
            
    		//in = new ErodeImageStream(in, 1);
            
            //inB2 = new ErodeImageStream(inB, 1);
            
            //in = new RateLimitImageStream(in, 24);
           
    		/*
            //BackgroundModel bm = new BackgroundModel(320, 240, 200);
            Imager i1 = new Imager();
            i1.setVisible(true);
            i1.setResizable(false);
            //*/
    		
            /*
            Imager i2 = new Imager();
            i2.setVisible(true);
            i2.setResizable(false);
            i2.setTitle("Image B Original");
            i2.setLocation(300, 350);
            
            Imager i3 = new Imager();
            i3.setVisible(true);
            i3.setResizable(false);
            i3.setTitle("Image B Changed");
            i3.setLocation(i2.getLocation().x + (i2.getWidth() * 2) + 14, i2.getLocation().y);
            //*/
        	
    		/*
        	Imager i4 = new Imager();
        	i4.setVisible(true);
        	i4.setResizable(false);
        	i4.setTitle("Data");
        	i4.setLocation(300, 350);
        	//*/
        	
            ////while( true )
            ////{  
                    //Image img1 = Utils.data2image(in.read(), in.width(), in.height());
                    //Image img2 = inB.read();
                    //Image img3 = inB2.read();
                    //i1.setImage(img1);
                    //i2.setImage(img2);
                    //i3.setImage(img3);
            		
        			
        			//Image img4 = data2image(tmp, width, height);
            		//i4.setImage(img4);
        	
            ////}
            
            //*/
            //System.out.println( data.length );
           
            //Utils.showImage(bi);
           
            //*/
        }
        
        private static Image data2image(int[] data, int width, int height) {
        	for (int i = 0; i < data.length; i++) 
    			data[i] = data[i] == 1 ? 0xff000000 : 0xffffffff;
        	
        	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    		img.setRGB(0, 0, width, height, data, 0, width);
    		return img;
        }
        
        private static void dilate(int[] data, int width, int height, int intensity) {
        	
        	//Calculate the manhattan distance from the nearest occupied pixel (1 = occupied)
        	manhattanDistance(data, width, height);
        	
        	//*
        	//Loops through the entire array and sets the 
            for (int i = 0; i < data.length; i++) {
                data[i] = (data[i] <= intensity) ? 1 : 0;
            }
       	}
        private static void erode(int[] data, int width, int height, int intensity) {
            reversedManhattanDistance(data, width, height);           

            //Loops through the entire array and sets the 
            for (int i = 0; i < data.length; i++) {
                data[i] = (data[i] <= intensity) ? 0 : 1;
            }
        }
        
        /*
         * Very slightly modified from the 2D variant at:
         * @url http://ostermiller.org/dilate_and_erode.html
         */
        private static int[] reversedManhattanDistance(int[] data, int width, int height) {
        	//traverse from top left to bottom right
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++) {
                    if (data[x + (width * y)] == 0){
                        // first pass and pixel was off, it gets a one
                    	data[x + (width * y)] = 0;
                    } else {
                        // pixel was on
                        // It is at most the sum of the lengths of the array
                        // away from a pixel that is off
                    	data[x + (width * y)] = width + height;
                        // or one more than the pixel to the north
                        if (y > 0) 
                        	data[x + (width * y)] = Math.min(data[x + (width * y)], data[x + (width * (y - 1))] + 1);
                        // or one more than the pixel to the west
                        if (x > 0) 
                        	data[x + (width * y)] = Math.min(data[x + (width * y)], data[(x - 1) + (width * y)] + 1);
                    }
                }
            }
            
            // traverse from bottom right to top left
            for (int y = height - 1; y >= 0; y--){
                for (int x = width - 1; x >= 0; x--){
                    // either what we had on the first pass
                    // or one more than the pixel to the south
                    if ((y + 1) < height) 
                    	data[x + (width * y)] = Math.min(data[x + (width * y)], data[x + (width * (y + 1))] + 1);
                    // or one more than the pixel to the east
                    if ((x + 1) < width) 
                    	data[x + (width * y)] = Math.min(data[x + (width * y)], data[(x + 1) + (width * y)] + 1);
                }
            }
            
            return data;
        }
        
        /*
         * Very slightly modified from the 2D variant at:
         * @url http://ostermiller.org/dilate_and_erode.html
         */
        private static int[] manhattanDistance(int[] data, int width, int height) {
        	//traverse from top left to bottom right
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++) {
                    if (data[x + (width * y)] == 1){
                        // first pass and pixel was on, it gets a zero
                    	data[x + (width * y)] = 0;
                    } else {
                        // pixel was off
                        // It is at most the sum of the lengths of the array
                        // away from a pixel that is on
                    	data[x + (width * y)] = width + height;
                        // or one more than the pixel to the north
                        if (y > 0) 
                        	data[x + (width * y)] = Math.min(data[x + (width * y)], data[x + (width * (y - 1))] + 1);
                        // or one more than the pixel to the west
                        if (x > 0) 
                        	data[x + (width * y)] = Math.min(data[x + (width * y)], data[(x - 1) + (width * y)] + 1);
                    }
                }
            }
            
            // traverse from bottom right to top left
            for (int y = height - 1; y >= 0; y--){
                for (int x = width - 1; x >= 0; x--){
                    // either what we had on the first pass
                    // or one more than the pixel to the south
                    if ((y + 1) < height) 
                    	data[x + (width * y)] = Math.min(data[x + (width * y)], data[x + (width * (y + 1))] + 1);
                    // or one more than the pixel to the east
                    if ((x + 1) < width) 
                    	data[x + (width * y)] = Math.min(data[x + (width * y)], data[(x + 1) + (width * y)] + 1);
                }
            }
            
            return data;
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