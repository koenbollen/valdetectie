package com.tigam.valdetectie.tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.streams.ImageFilterStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.CaptureDeviceStream;
import com.tigam.valdetectie.streams.NetworkImageStream;
import com.tigam.valdetectie.streams.filters.GrayScaleFilter;
import com.tigam.valdetectie.utils.Utils;


/**
 * This tool opens a {@link LinuxDeviceImageStream} and shares it to connected clients. It now uses a
 * {@link BlockingQueue} to enable frame dropping.
 * 
 * @see NetworkImageStream
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public final class ImageStreamServer
{
	public static final int DEFAULT_PORT = 46243;
	
	final class ServerListener extends Thread {
		private final ServerSocket socket;
		
		public ServerListener(int port) throws IOException{
			super("ServerListener[port:" + port + "]");
			setDaemon(true);
			this.socket = new ServerSocket(port);		
		}
		
		public void close(){
			try
			{
				this.socket.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			this.interrupt();
		}
		
		@Override
		public void run(){
			while (!isInterrupted()){
				try
				{
					Socket client = this.socket.accept();
					System.out.println( "Client connected: " + client.getInetAddress() );
					ImageStreamServer.this.handler.addClient(client);
				} catch (IOException e)
				{
					break;
				}
			}
			System.out.println("ServerListener is closed");
		}
		
		
	}
	
	final class ClientHandler extends Thread {
		private final BlockingQueue<Image> queue;
		private final Vector<Socket> clients;
		public ClientHandler(BlockingQueue<Image> queue){
			super("ClientHandler");
			setDaemon(true);
			this.queue = queue;
			this.clients = new Vector<Socket>();
		}
		
		public void addClient(Socket client) {
			if (client == null) return;
			
			synchronized (clients)
			{
				if (!clients.contains(client)){
					clients.add( client );
				}
			}
		}
		
		public void run(){
			while(!isInterrupted()){
				
				Image img = null;
				try
				{
					img = this.queue.take();
				} catch( InterruptedException e1 )
				{
					this.interrupt();
					break;
				}
				if (img == null) break;
				
				BufferedImage buffimg;
				if (img instanceof BufferedImage) buffimg = (BufferedImage)img;
				else {
					buffimg = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
					Graphics g = buffimg.getGraphics();
					g.drawImage(img, 0, 0, null);
					g.dispose();
				}
				try
				{
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ImageIO.write(buffimg, "bmp", out);
					send(out.toByteArray());
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("ClientHandler Stopped Working");
			
			for (Socket s : clients){
				try
				{
					if (!s.isClosed()) s.close();
				} catch (IOException e) { }
			}
		}
		
		private void send(byte [] data){
			synchronized (clients)
			{
				LinkedList<Socket> toRemove = new LinkedList<Socket>();
				for (Socket s:clients){
					if (s.isClosed()){
						toRemove.add(s);
						continue;
					}
					try	{
						OutputStream out = s.getOutputStream();
						out.write(data);
						out.flush();
					} catch (IOException e)
					{
						toRemove.add(s);
					}
				}
				for (Socket s:toRemove) clients.remove(s);
			}
		}
	}
	
	final class ImageDispenser extends Thread
	{
		private final BlockingQueue<Image> queue;
		private final ImageStream stream;
		
		public ImageDispenser(ImageStream stream, BlockingQueue<Image> queue){
			super("ImageDispenser");
			this.stream = stream;
			this.queue = queue;
		}
		
		public void run(){
			while(!isInterrupted()){
				try
				{
					Image img = Utils.data2image(this.stream.read(), this.stream.width(), this.stream.height());
					if (img == null) break;
					this.queue.offer(img, 10, TimeUnit.MILLISECONDS);
				} catch( InterruptedException e )
				{
				}
			}
		}
	}

	private final BlockingQueue<Image> queue;
	
	private final ServerListener server;
	private final ClientHandler handler;
	private final ImageDispenser dispenser;
	
	public ImageStreamServer(ImageStream stream, int port) throws IOException{
		this.queue = new LinkedBlockingQueue<Image>(1);
		this.handler = new ClientHandler(this.queue);
		this.dispenser = new ImageDispenser(stream, this.queue);
		this.server = new ServerListener(port);
	}
	
	public void start(){
		this.server.start();
		this.handler.start();
		this.dispenser.start();
	}

	public void close(){
		this.dispenser.interrupt();
		this.handler.interrupt();
		this.server.close();
	}
	
	public static void main(String ... args){
		int port = DEFAULT_PORT;
		int rate = 12;
		boolean grayscale = false;
		boolean gzipped = false;
		
		for (int i = 0; i<args.length; i++){
			if (args[i].equalsIgnoreCase("-help")){
				System.out.println("WebcamServer [-p portnumber] [-rate #] [-gray]");
				System.out.println();
				System.out.println(" -p portnumeber");
				System.out.println("    the portnumber to listen on with the server");
				System.out.println();
				System.out.println(" -rate");
				System.out.println("    set the framerate of the imagestream (default: 12)");
				System.out.println();
				System.out.println(" -gray");
				System.out.println("    if this flag is set the server will stream in grayscale");
				System.out.println();
				System.exit(0);
			} else if (args[i].equalsIgnoreCase("-p")){
				i++;
				try {
					port = Integer.parseInt(args[i]);
				} catch (IndexOutOfBoundsException ball){
				} catch (NumberFormatException ball){
				}				
			} else if (args[i].equalsIgnoreCase("-rate")){
				i++;
				try {
					rate = Integer.parseInt(args[i]);
				} catch (IndexOutOfBoundsException ball){
				} catch (NumberFormatException ball){
				}				
			} else if (args[i].equalsIgnoreCase("-gray")){
				grayscale = true;
			}
		}		
		
		try
		{
			ImageStream imgStream = new CaptureDeviceStream(320,240,rate);
			if (grayscale) imgStream = new ImageFilterStream( imgStream, GrayScaleFilter.instance );
			(new ImageStreamServer(imgStream,port)).start();
			System.out.println( "ImageStreamServer listening on port " + port );
			if (grayscale) System.out.println( "Images are streamed in grayscale");
			if (gzipped) System.out.println("The stream is compressed");
		} catch (Exception ball)
		{
			ball.printStackTrace();
		}
		
	}
	
}
