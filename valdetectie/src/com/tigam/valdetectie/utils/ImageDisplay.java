package com.tigam.valdetectie.utils;

import static java.lang.Math.ceil;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Display one or more images and update them if needed.
 * 
 * ImageDisplay is a frame that can contain multiple images for view for 
 * debugging purposes. Each images has and ID and can be updated/changed
 * as much as needed.
 * 
 * The new images will only be shown when the {@link ImageDisplay#update()} 
 * method is called. This method will also calculate the frame-per-second.
 * 
 * When the ImageDisplay has more then 2 image you can focus one on ID
 * using {@link ImageDisplay#focus(int)} and this image will be shown
 * first an twice as big. It's also possible to click on an image in the
 * frame to focus it.
 * 
 * 
 * Basic Usage:
 * <code>
 *  ImageDisplay display = new ImageDisplay( w, h, 5 );
 *  display.update(); 
 *  
 *  while( hasNextImage() )
 *  {
 *    int[] img = nextImage();
 *    display.image( 0, img );
 *    res = filter( i );
 *    display.image( 1, res );
 *    display.update();
 *  }
 * </code>
 *
 * @since Nov 18, 2009
 * @author Koen Bollen
 */
public class ImageDisplay extends JFrame
{
	private static final long serialVersionUID = 5880332920926051031L;

	private class ImageField extends JLabel
	{
		private static final long serialVersionUID = 1L;
		
		public int id;
		public ImageIcon icon;
		public ImageField()
		{
			super();
			this.icon = new ImageIcon();
			this.setIcon( this.icon );
		}
	}
	
	private final static Dimension screen;

	private static final double FPS_ALPHA = 0.9;

	private final int image_width;
	private final int image_height;
	private int frame_width;
	private int frame_height;
	private int spacing;
	protected int focus;
	
	private double fps;
	private double last_update;

	private List<ImageField> images;
	private List<ImageField> changed;
	private boolean initialized;

	/**
	 * Simple constructor uses a spacing of 10 and exits on close.
	 * 
     * @param image_width The width of a single image.
     * @param image_height The height of a single image.
     */
    public ImageDisplay( int image_width, int image_height )
    {
    	this( image_width, image_height, 10 );
    }

	/**
	 * Construct that defaults exit_on_close to true.
	 * 
     * @param image_width The width of a single image.
     * @param image_height The height of a single image.
     * @param spacing The spacing between each image.
     */
    public ImageDisplay( int image_width, int image_height, int spacing )
    {
    	this( image_width, image_height, spacing, true );
    }
	
	/**
	 * Construct a ImageDisplay lazy.
	 * 
     * @param image_width The width of a single image.
     * @param image_height The height of a single image.
     * @param spacing The spacing between each image.
     * @param exit_on_close Exit the application when this Frame is closed if true.
     */
    public ImageDisplay( int image_width, int image_height, int spacing, boolean exit_on_close )
    {
	    super();
	    this.image_width = image_width;
	    this.image_height = image_height;
	    this.spacing = spacing;
	    this.focus = -1;
	    
	    images = new ArrayList<ImageField>();
	    changed = new Vector<ImageField>();

	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    if( exit_on_close )
	    {
	    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	this.addKeyListener( new KeyListener() {
				public void keyReleased( KeyEvent e ) {}
				public void keyPressed( KeyEvent e ) {}
				public void keyTyped( KeyEvent e ) {
					if( e.getKeyChar() == KeyEvent.VK_ESCAPE )
						System.exit(0);
				}
			});
	    }
	    
	    this.initialized = false;
    }
    
    private void initialize()
    {
    	//System.out.println( "ImageDisplay: Initializing..." );
    	this.setSize( image_width, image_height );
		this.setResizable( false );
		this.setLayout( null );

	    this.fps = -1;
	    this.last_update = 0;
		
		this.setVisible( true );
		this.initialized = true;
    }
    
    private void geometrics()
    {
    	int size = images.size();
    	boolean has_focus = false;
    	
    	if( size < 1 )
    		return;
    	
    	if( size > 2 && focus >= 0 )
    	{
	    	try
	    	{
	    		images.get(focus);
	    		has_focus = true;
	    		size += 3;
	    	}
	    	catch( IndexOutOfBoundsException e )
	    	{
	    		System.err.println( "Warning focus doesn't exists: "+focus );
	    	}
    	}
    	
    	int h = (int)(screen.height*.75 / (image_height+spacing));
    	if( size < h )
    		h = size;
    	int w = (int)ceil(size / (double)h);
    	
    	int index = 0;
    	ImageField f;
    	for( int id = 0; id < images.size(); id++ )
    	{
    		f = images.get(id);
    		if( has_focus )
    		{
    			if( index == 0 )
    				index += 2;
    			if( id == focus )
    			{
    	    		f.setBounds( 0, 0, image_width*2, image_height*2 );
    	    		index--;
    			}
    			else
    			{
    				//f.setVisible( false );
    				int x = index/h * ((image_width)+spacing);
    				int y = index%h * ((image_height)+spacing);
    	    		f.setBounds( x, y, image_width-spacing, image_height-spacing );
    				if( index/h == 0 && index%h == h-1 )
    					index += 2;
    			}
    		}
    		else
    		{
	    		f.setBounds( 
	    				index/h * (image_width+spacing), 
	    				index%h * (image_height+spacing), 
	    				image_width, image_height
	    			);
    		}
    		index++;
    	}
    	
    	if( has_focus )
    	{
	    	frame_width = w * ((image_width)+spacing) - spacing;
	    	frame_height = h * ((image_height)+spacing) - spacing + 25; // 25=WindowTitle
    	}
    	else
    	{
	    	frame_width = w * (image_width+spacing) - spacing;
	    	frame_height = h * (image_height+spacing) - spacing + 25; // 25=WindowTitle
    	}
    }

    /**
     * Update the images and more.
     * 
     * This method should be called each frame when all the images are updated.
     */
    public void update()
    {
    	if( !this.initialized )
    		initialize();
    	
    	if( last_update == -1 )
    	{
    		last_update = System.currentTimeMillis();
    	}
    	else
    	{
        	double delta = System.currentTimeMillis()-last_update;
        	last_update += delta;
	    	if( fps == -1 )
	    		fps = 1000.0 / delta;
	    	fps = (fps * FPS_ALPHA) + ((1000.0/delta) * (1.0-FPS_ALPHA));
    	}
    	
    	if( fps == -1 )
    		this.setTitle( "ImageDisplay ("+images.size()+" images)" );
    	else
    		this.setTitle( "ImageDisplay: " + (int)fps + " fps, " +images.size()+" images" );
    	
    	
    	if( images.size() <= 0 )
    		return;
    	
    	this.setSize( frame_width, frame_height );

    	for( ImageField f : changed )
    		f.repaint();

    	//System.out.println( "ImageDisplay: updated" );
    }

    /**
     * Update or create an image.
     * 
     * This method will change the content of an image by ID. If this ID doesn't
     * exists a new will be made.
     * 
     * @param id The ID of the image.
     * @param img The image data.
     */
    public void image( int id, int[] img )
    {
    	if( img.length != image_width*image_height )
    		throw new IllegalArgumentException( "Provided image data length didn't fit the required size." );
	
    	ImageField f = null;
    	try
    	{
    		f = images.get( id );
    	}
    	catch( IndexOutOfBoundsException e )
    	{
    		//System.out.println( "ImageDisplay: ID not found, creating new entry." );
    		f = new ImageField();
    		f.id = id;
    		f.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR ) );
    		f.addMouseListener( new MouseListener() {

				@Override
                public void mouseClicked( MouseEvent e )
                {
					ImageField f = (ImageField)e.getSource();
					if( focus == f.id )
						unfocus();
					else
						focus(f.id);
                }
				@Override
                public void mouseEntered( MouseEvent e ){}
				@Override
                public void mouseExited( MouseEvent e ){}
				@Override
                public void mousePressed( MouseEvent e ){}
				@Override
                public void mouseReleased( MouseEvent e ){}
    		});
    		images.add( id, f );
    		this.add( f );
    		geometrics();
    	}
    	
    	//f.setText( "ID: "+id );
    	Image bi = data2image(img);
    	if( focus == id )
    		bi = bi.getScaledInstance( f.getWidth(), f.getHeight(), Image.SCALE_FAST );
    	f.icon.setImage( bi );
    	if( !changed.contains( f ) )
    		changed.add( f );
    }

    /**
     * Update/create the image with ID 0, the first.

     * @param img The image data.
     */
    public void image( int[] img )
    {
    	this.image(0, img);
    }

    /**
     * Update multiple images.
     * 
     * @param offset Start counting ID here.
     * @param imgs List of images.
     */
    public void image( int offset, int[] ... imgs )
    {
    	for( int i = 0; i < imgs.length; i++ )
	        this.image( offset+i, imgs[i] );
    }

    /**
     * Update multiple images starting at the beginning.
     * 
     * @param imgs List of images.
     */
    public void image( int[] ... imgs )
    {
	    this.image( 0, imgs );
    }
    
    /**
     * Focus an image.
     * 
     * @param id The ID that needs focusing.
     */
    public void focus( int id )
    {
    	this.focus = id;
    	if( images.size() <= 0 )
    		return;
    	geometrics();
    	this.setSize( frame_width, frame_height );
    }
    
    /**
     * Unfocus the focused image.
     */
    public void unfocus()
    {
    	this.focus = -1;
    	if( images.size() <= 0 )
    		return;
    	geometrics();
    	this.setSize( frame_width, frame_height );
    }
    
    private BufferedImage data2image( int[] data )
    {
		BufferedImage result = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, image_width, image_height, data, 0, image_width);
		return result;
    }
    
    static
    {
    	screen = Toolkit.getDefaultToolkit().getScreenSize();
    	
    	// Ugly multi-screen detector:
    	if( screen.width > screen.height*2 )
    		screen.width /= 2;
    	else if( screen.height > screen.width*2 )
    		screen.height /= 2;
    }

    /**
     * Test static main.
     * @deprecated Test.
     */
    @Deprecated
	public static void main( String[] args ) throws Exception
	{
		/*/ // Remove one of the first slashed to toggle the code block.
    	
		ImageDisplay display = new ImageDisplay( 32, 24, 2 );
		int[] img = new int[32*24];
		for( int c = 0; c < 200; c++ )
		{
			int v = (int)((199-c)/199.0*255.0);
			Arrays.fill( img, v<<16|v<<8|v );
			display.image(c,img);

			display.update();
			if( c > 5 )
				Thread.sleep( 100 );
			//display.unfocus();
		}
		
		/*/
		
		ImageDisplay display = new ImageDisplay( 320, 240 );
		
		int slow = 10;
		int imglist[][] = new int[4][320*240];
		// Call one 'empty' update first so 
		// the fps will be more accurate:
		display.update();
		for( int frame = 0; frame < 0x100; frame ++ )
		{
			// As if the beginning is harder to process:
			if( frame < slow )
				Thread.sleep( (slow-frame)*10 );
			
			for( int i = 0; i < 4; i++ )
			{
				if( i < 3 )
					Arrays.fill( imglist[i], frame << (8*i) );
				else
					Arrays.fill( imglist[i], frame<<16|frame<<8|frame );
				display.image( i, imglist[i] );
				
				// Atleast run on 24 fps while simulating that 
				// every seperate images takes time to process:
				Thread.sleep( 10 ); // 1000.0 / 24.0 / 4.0 = 10.25
			}
			display.update();
		}
		
		//*/
	}

}
