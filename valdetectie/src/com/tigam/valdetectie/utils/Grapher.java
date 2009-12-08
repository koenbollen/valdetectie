package com.tigam.valdetectie.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Grapher extends JFrame
{
	
	class XYSeriesContainer {
		
		private double value;
		private int emptyUpdateCount;
		
		public final XYSeries data;
		
		public XYSeriesContainer(int label){
			this.value = 0;
			this.emptyUpdateCount = 0;
			this.data = new XYSeries("Box " + label);
		}
		
		public void setNext(double value){
			this.value = value;
			this.emptyUpdateCount = 0;
		}
		
		public void update(){
			data.add(Grapher.this.updateStep, this.value);
			while (data.getItemCount() > Grapher.windowSize)
				data.remove(0);
			this.value = -1;
			this.emptyUpdateCount++;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7546779555843959009L;
	private final JFreeChart chart;
	
	private int updateStep;
	private static final int windowSize = 20;
	
	private Map<Integer, XYSeriesContainer> lines;
	private final XYPlot plot;

	public Grapher(){
		super("");
		
		this.lines = new HashMap<Integer, XYSeriesContainer>();
		
		this.updateStep = 0;
		
		chart = ChartFactory.createXYLineChart("Box Ratio", "time", "ratio", null, PlotOrientation.VERTICAL, false, false, false);
		plot = chart.getXYPlot();
		plot.getDomainAxis().setAxisLineVisible(false);
		plot.getDomainAxis().setTickMarksVisible(false);
		plot.getDomainAxis().setTickLabelsVisible(false);
		plot.getRangeAxis().setRange(0, 5.0);
		setContentPane(new ChartPanel(chart));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void add(Map<Box, Integer> boxes){
		for (Map.Entry<Box, Integer> entry:boxes.entrySet()){
			int box = entry.getValue();
			if (!lines.containsKey(box)){
				XYSeriesContainer cont ;
				lines.put(box, cont = new XYSeriesContainer(box));
				plot.setDataset(box, new XYSeriesCollection(cont.data));
				XYLineAndShapeRenderer render = new XYSplineRenderer();
				render.setPaint(new Color(colour(box)));
				plot.setRenderer(box,render);
			}
			lines.get(box).setNext(entry.getKey().ratio());
		}
		update();
	}
	
	private void update(){
		List<Integer> toRemove = new LinkedList<Integer>();
		for (Map.Entry<Integer, XYSeriesContainer> cont:lines.entrySet()){
			if (cont.getValue().emptyUpdateCount >= windowSize){
				toRemove.add(cont.getKey());
			}
			cont.getValue().update();
		}
		for (Integer i:toRemove){
			plot.setDataset(i, null);
			plot.setRenderer(i, null);
			lines.remove(i);
		}
		this.updateStep++;
	}
	

	static int[] colours = new int[]
	{ 0xFF0000, 0x0000FF, 0x000000, 0xD80000, 0xD8CB00, 0x00D8CB, 0x0056D8,
			0x8E00D8, 0xD800C6, 0xC40000, 0xFF4040, 0xFF9B9B, 0xA09BFF,
			0xA09BFF, 0x0A00C6 };

	public static int colour(int index)
	{
		return colours[index % colours.length];
	}
}
