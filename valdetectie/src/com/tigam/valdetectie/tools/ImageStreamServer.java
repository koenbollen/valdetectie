package com.tigam.valdetectie.tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.tigam.valdetectie.streams.ImageStream;
import com.tigam.valdetectie.streams.LinuxDeviceImageStream;


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
		private final LinkedList<Socket> clients;
		public ImageDispenser(ImageStream stream){
			super("ImageDispenser[stream:" + stream + "]");
			this.stream = stream;
			this.clients = new LinkedList<Socket>();
			setDaemon(true);
		}
		
		public void addClient(Socket client){
			if (client == null) return;
			
			synchronized (clients)
			{
				if (!clients.contains(client)) clients.add(client);
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
			
			for (Socket s:clients){
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
						s.getOutputStream().write(data);
					} catch (IOException e)
					{
						toRemove.add(s);
					}
				}
				clients.removeAll(toRemove);
			}
		}
	}
	
	private final ImageDispenser dispenser;
	private final ServerListener server;
	
	public ImageStreamServer(ImageStream stream, int port) throws IOException{
		this.dispenser = new ImageDispenser(stream);
		this.server = new ServerListener(port);
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
		try {
			port = Integer.parseInt(args[0]);
		} catch (IndexOutOfBoundsException ball){
		} catch (NumberFormatException ball){
		}
		
		try
		{
			(new ImageStreamServer(new LinuxDeviceImageStream(),port)).start();
			System.out.println( "ImageStreamServer listening on port "+port );
		} catch (Exception ball)
		{
			ball.printStackTrace();
		}
		
	}
	
}
