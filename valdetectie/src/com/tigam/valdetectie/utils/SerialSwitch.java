package com.tigam.valdetectie.utils;
import java.util.Enumeration;
import java.io.*;
import gnu.io.*;

public class SerialSwitch
{
	private SerialPort port;
	private InputStream  instream;
	private OutputStream outstream;
	
	public SerialSwitch(int portNr)
	{
		Enumeration portIDs;
		try
		{
			portIDs = CommPortIdentifier.getPortIdentifiers();
		}
		catch (UnsatisfiedLinkError e)
		{
			System.out.println("RXTX is not installed properly. No serial commands will be sent.");
			e.printStackTrace();
			return;
		}
		CommPortIdentifier selectedPort = null;
		int amount = 0;
		
		while (portIDs.hasMoreElements())
		{
			CommPortIdentifier id = (CommPortIdentifier) portIDs.nextElement();
			String portType;
			
			switch(id.getPortType())
			{
				case CommPortIdentifier.PORT_SERIAL:
					portType = "Serial   ";
					if (id.getName().contains("" + portNr)) selectedPort = id;
					break;
					
				case CommPortIdentifier.PORT_PARALLEL:
					portType = "Parallel "; break;
					
				default:
					portType = "Unknown  "; break;
			}
			System.out.println(portType + id.getName());
			amount++;
		}
		System.out.println("");
		System.out.println("Found " + amount + " ports");
		System.out.println("\n" + selectedPort.getName() + " selected. Attempting to connect...");
		try
		{
			port = (SerialPort)selectedPort.open("Valdetectie", 5000);
			port.setSerialPortParams(/*speed*/9600, port.DATABITS_8, port.STOPBITS_1, port.PARITY_NONE);
		
			instream  = port.getInputStream();
			outstream = port.getOutputStream();
		}
		catch (PortInUseException e) {e.printStackTrace();}
		catch (UnsupportedCommOperationException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public void setEnabled(boolean on)
	{
		if (outstream == null) return;
		try
		{
			outstream.write(255);
			outstream.write(1);
			outstream.write(on ? 1 : 0);
			outstream.flush();
		}
		catch (IOException e)
		{
			System.out.println("Serial send error!");
			System.out.println(e.getMessage());
		}
	}
}