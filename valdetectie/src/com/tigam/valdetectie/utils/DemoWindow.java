package com.tigam.valdetectie.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DemoWindow extends JFrame implements ComponentListener
{
	JLabel[] labels = new JLabel[6];
	
	JLabel  rateLabel;
	JSlider rateSlider;
	
	public DemoWindow()
	{
		super("Valdetectie");
		
		// Configure window
		Container content = getContentPane();
		content.setBackground(Color.WHITE);
		content.setLayout(null);
		
		for (int i = 0; i < 6; i++)
		{
			labels[i] = new JLabel("", JLabel.CENTER);
		}
		content.add(labels[0]);
		content.add(labels[1]);
		content.add(labels[2]);
		content.add(labels[3]);
		content.add(labels[4]);
		content.add(labels[5]);
		content.addComponentListener(this);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setSize(1000, 750);
	}
	
	public void show(int id, Image image)
	{
		int imgW = image.getWidth(null);
		int imgH = image.getHeight(null);
		if (imgW ==0 || imgH == 0) return;
		double ratio = Math.min((double)labels[id].getWidth() / imgW, (double)labels[id].getHeight() / imgH);
		if (ratio == 0) return;
		labels[id].setIcon(new ImageIcon(image.getScaledInstance((int)(ratio * imgW), (int)(ratio * imgH), Image.SCALE_FAST)));
	}
	
	public void componentMoved(ComponentEvent e)  {}
	public void componentShown(ComponentEvent e)  {}
	public void componentHidden(ComponentEvent e) {}
	
	public void componentResized(ComponentEvent e)
	{
		int width  = e.getComponent().getWidth();
		int height = e.getComponent().getHeight();
		
		int w1 =  width      / 3;
		int w2 = (width * 2) / 3;
		int h1 =  height     / 3;
		int h2 = (height * 2) /3;
		
		labels[0].setBounds(0,      0,      w2 - 2,         h2 - 2);
		labels[1].setBounds(0,      h2 + 1, w1 - 2,         height - h2 - 2);
		labels[2].setBounds(w1 + 1, h2 + 1, w2 - w1 - 2,    height - h2 - 2);
		labels[3].setBounds(w2 + 1, h2 + 1, width - w2 - 2, height - h2 - 2);
		labels[4].setBounds(w2 + 1, h1 + 1, width - w2 - 2, h2 - h1 - 2);
		labels[5].setBounds(w2 + 1, 0,      width - w2 - 2, h1 - 2);
	}
}