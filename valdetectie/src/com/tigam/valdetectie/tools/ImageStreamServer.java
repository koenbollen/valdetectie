package com.tigam.valdetectie.tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.streams.GrayScaleImageStream;
import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;
import com.tigam.valdetectie.streams.NetworkImageStream;


/**
 * This tool opens a {@link LinuxDeviceImageStream} and shares it to connected clients.
 * 
 * @see NetworkImageStream
 * @author Nils Dijk
 */
public final class ImageStreamServer
{
	public static final int DEFAULT_PORT = 46243;
	
	final class ServerListener extends Thread {
		private final ServerSocket socket;
		
		public ServerListener(int port) throws IOException{
			super("ServerListener[port:" + port + "]");
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
					ImageStreamServer.this.dispenser.addClient(this.socket.accept());
				} catch (IOException e)
				{
					break;
				}
			}
			System.out.println("ServerListener is closed");
		}
		
		
	}
	
	final class ImageDispenser extends Thread {
		private final ImageStream stream;
		private final HashMap<Socket,OutputStream> clients;
		public ImageDispenser(ImageStream stream){
			super("ImageDispenser[stream:" + stream + "]");
			this.stream = stream;
			this.clients = new HashMap<Socket,OutputStream>();
			setDaemon(true);
		}
		
		public void addClient(Socket client) {
			if (client == null) return;
			
			synchronized (clients)
			{
				try {
					if (!clients.containsKey(client)){
						OutputStream out = client.getOutputStream();
						
						// check if there is need for compression
						if (ImageStreamServer.this.gzipped){
							// if we use compression (gzip) start with 0x01 byte so the client knows it has to encapsulate the stream with a gzip stream
							out.write(1);
							out.flush();
							out = new GZIPOutputStream(out);
						} else {
							// if we do not use compression send a 0x00 byte so the client knows to do nothing with the stream
							out.write(0);
							out.flush();
						}
						
						clients.put(client,out);
					}
				} catch (IOException ball){
					System.err.println("Unable to add a client to the clientlist");
				}
			}
		}
		
		public void run(){
			while(!isInterrupted()){
				Image img = this.stream.read();
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
			System.out.println("ImageDispenser Stopped Working");
			
			for (Socket s:clients.keySet()){
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
				for (Socket s:clients.keySet()){
					if (s.isClosed()){
						toRemove.add(s);
						continue;
					}
					OutputStream out = clients.get(s);
					try	{
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
	
	private final ImageDispenser dispenser;
	private final ServerListener server;
	private final boolean gzipped;
	
	public ImageStreamServer(ImageStream stream, int port) throws IOException{
		this (stream, port, false);
	}
	
	public ImageStreamServer(ImageStream stream, int port, boolean gzipped) throws IOException{
		this.dispenser = new ImageDispenser(stream);
		this.server = new ServerListener(port);
		this.gzipped = gzipped;
	}
	
	public void start(){
		this.server.start();
		this.dispenser.start();
	}

	public void close(){
		this.dispenser.interrupt();
		this.server.close();
	}
	
	public static void main(String ... args){
		int port = DEFAULT_PORT;
		boolean grayscale = false;
		boolean gzipped = false;
		
		for (int i = 0; i<args.length; i++){
			if (args[i].equalsIgnoreCase("-help")){
				System.out.println("WebcamServer [-p portnumber] [-gray] [-gzip]");
				System.out.println();
				System.out.println(" -p portnumeber");
				System.out.println("    the portnumber to listen on with the server");
				System.out.println();
				System.out.println(" -gray");
				System.out.println("    if this flag is set the server will stream in grayscale");
				System.out.println();
				System.out.println(" -gzip");
				System.out.println("    if this flag is set the stream is compressed using gzip");
				System.exit(0);
			} else if (args[i].equalsIgnoreCase("-p")){
				i++;
				try {
					port = Integer.parseInt(args[i]);
				} catch (IndexOutOfBoundsException ball){
				} catch (NumberFormatException ball){
				}				
			} else if (args[i].equalsIgnoreCase("-gray")){
				grayscale = true;
			} else if (args[i].equalsIgnoreCase("-gzip")){
				gzipped = true;
			}
		}		
		
		try
		{
			ImageStream imgStream = new LinuxDeviceImageStream(320,240);
			if (grayscale) imgStream = new GrayScaleImageStream(imgStream);
			(new ImageStreamServer(imgStream,port,gzipped)).start();
			System.out.println( "ImageStreamServer listening on port " + port );
			if (grayscale) System.out.println( "Images are streamed in grayscale");
			if (gzipped) System.out.println("The stream is compressed");
		} catch (Exception ball)
		{
			ball.printStackTrace();
		}
		
	}
	
}
