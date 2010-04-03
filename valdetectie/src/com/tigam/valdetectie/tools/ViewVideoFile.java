package com.tigam.valdetectie.tools;

import java.io.File;

import javax.swing.JFileChooser;

import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.VideoFileImageStream;
import com.tigam.valdetectie.utils.Imager;
import com.tigam.valdetectie.utils.Utils;

/**
 * This class is a simple tool that opens a file as a 
 * {@link VideoFileImageStream} and display the frames.
 * 
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class ViewVideoFile
{

	public static void main(String[] args)
	{
		File file = null;
		
		if( args.length >= 1 )
		{
			file = new File( args[0] );
		}
		else
		{
			JFileChooser fc = new JFileChooser();
			int res = fc.showOpenDialog(null);
			if( res != JFileChooser.APPROVE_OPTION )
				System.exit(1);
			file = fc.getSelectedFile();
		}
		
		ImageStream str = null;
		try
		{
			str = new VideoFileImageStream(file);
		} catch( Exception e )
		{
			System.err.println( "unable to openfile: " + e.getMessage() );
			System.exit(0);
		}
		Imager imager = new Imager();
		imager.setTitle( "Viewing file: " + file.getName() );
		imager.setVisible(true);
		while (true){
			int[] img = str.read();
			if (img == null)
				break;
			imager.setImage(Utils.data2image(img, str.width(), str.height()));
		}
		System.exit(0);
	}

}
