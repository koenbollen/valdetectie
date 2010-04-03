package com.tigam.valdetectie.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class Grapher extends JFrame
{
	
	class XYSeriesContainer {

		private double value;
		private double prev;
		private int emptyUpdateCount;

		public final XYSeries ratio;
		public final XYSeries delta;
		
		public XYSeriesContainer(int label){
			this.value = 0;
			this.emptyUpdateCount = 0;
			this.ratio = new XYSeries("Ratio " + label);
			this.delta = new XYSeries("Delta " + label);
		}
		
		public void setNext(double value){
			this.value = value;
			this.emptyUpdateCount = 0;
		}
		
		public void update(){
			ratio.add(Grapher.this.updateStep, this.value);
			delta.add(Grapher.this.updateStep, (this.value>-1)?(this.value-this.prev):-1);
			
			while (ratio.getItemCount() > Grapher.windowSize)
				ratio.remove(0);
			while (delta.getItemCount() > Grapher.windowSize)
				delta.remove(0);
			this.prev = this.value;
			this.value = -1;
			this.emptyUpdateCount++;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7546779555843959009L;
	private final JFreeChart rchart;
	private final JFreeChart dchart;
	private final XYPlot rplot;
	private final XYPlot dplot;
	
	private int updateStep;
	private static final int windowSize = 20;
	
	private Map<Integer, XYSeriesContainer> lines;

	public Grapher(){
		super("Graphs");
		
		this.lines = new HashMap<Integer, XYSeriesContainer>();
		
		this.updateStep = 0;
		
		rchart = ChartFactory.createXYLineChart("Ratio", "time", "ratio", null, PlotOrientation.VERTICAL, false, false, false);
		dchart = ChartFactory.createXYLineChart("Delta", "time", "delta", null, PlotOrientation.VERTICAL, false, false, false);
		rplot = rchart.getXYPlot();
		rplot.getDomainAxis().setAxisLineVisible(false);
		rplot.getDomainAxis().setTickMarksVisible(false);
		rplot.getDomainAxis().setTickLabelsVisible(false);
		rplot.getRangeAxis().setRange(0, 5.0);
		dplot = dchart.getXYPlot();
		dplot.getDomainAxis().setAxisLineVisible(false);
		dplot.getDomainAxis().setTickMarksVisible(false);
		dplot.getDomainAxis().setTickLabelsVisible(false);
		dplot.getRangeAxis().setRange(-2.0, 2.0);
		setContentPane(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ChartPanel(rchart), new ChartPanel(dchart)));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void add(Map<Box, Integer> boxes){
		
		double max = Double.MIN_VALUE;
		for (Map.Entry<Box, Integer> entry:boxes.entrySet())
			if( entry.getKey().surface() > max )
				max = entry.getKey().surface(); 

		for (Map.Entry<Box, Integer> entry:boxes.entrySet()){
			int box = entry.getValue();
			if (!lines.containsKey(box)){
				XYSeriesContainer cont ;
				lines.put(box, cont = new XYSeriesContainer(box));
				rplot.setDataset(box, new XYSeriesCollection(cont.ratio));
				dplot.setDataset(box, new XYSeriesCollection(cont.delta));
//				rplot.setRenderer(box,render);
//				dplot.setRenderer(box,render);
			}
			if( entry.getKey().surface() < max )
			{
				rplot.setRenderer(box,null);
				dplot.setRenderer(box,null);
			} else {
				XYLineAndShapeRenderer render = new XYSplineRenderer();
				render.setPaint(new Color(colour(box)));
				rplot.setRenderer(box,render);
				dplot.setRenderer(box,render);
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
			rplot.setDataset(i, null);
			rplot.setRenderer(i, null);
			dplot.setDataset(i, null);
			dplot.setRenderer(i, null);
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
