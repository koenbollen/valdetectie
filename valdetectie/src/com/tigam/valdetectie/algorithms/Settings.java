package com.tigam.valdetectie.algorithms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@Deprecated
public class Settings
{	
	public static final double SCALE = 1000;
	interface ValueListener {
		void valueChanged ( double value);
	}
	
	public static class Value {
		public final String name;
		public final double upper;
		public final double lower;
		public final double defvalue;
		public final ValueListener updater;
		private double value;
		
		public Value(String name, double lower, double upper, double value, ValueListener updater){
			this.name = name;
			this.upper = upper;
			this.lower = lower;
			this.value = this.defvalue = value;
			this.updater = updater;
		}
		
		public double getValue(){
			return value;
		}
		
		public void setValue(double value){
			this.value = value;
			if (this.updater != null)
				this.updater.valueChanged(value);
		}
	}
	
	public static final Settings instance = new Settings();

	private final List<Value> values;

	private JFrame window;
	
	private Settings(){
		this.values = new LinkedList<Value>();
		this.values.add(new Value("Lncc",0, 1, ShadowDetector.Lncc, new ValueListener()
		{
			@Override
			public void valueChanged(double value)
			{
				ShadowDetector.Lncc = value;
			}
		}));
		this.values.add(new Value("Lstd",0, 1, ShadowDetector.Lstd, new ValueListener()
		{
			@Override
			public void valueChanged(double value)
			{
				ShadowDetector.Lstd = value;
			}
		}));
		this.values.add(new Value("Llow",0, 1, ShadowDetector.Llow, new ValueListener()
		{
			@Override
			public void valueChanged(double value)
			{
				ShadowDetector.Llow = value;
			}
		}));
	}

	public void show()
	{
		if( window == null )
			initialize();
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	private void initialize()
	{
		window = new JFrame("Settings Editor");
		
		JPanel sliders = new JPanel(new GridLayout(0,3));
		for (final Value v:this.values){
			final JLabel label = new JLabel(""+v.getValue());
			final JSlider slider = new JSlider( (int)(v.lower*SCALE), (int)(v.upper*SCALE) );
			slider.setValue((int)(v.getValue()*SCALE));
			
			sliders.add(new JLabel(v.name+":"));
			sliders.add(slider);
			sliders.add(label);
			
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent arg0)
				{
					v.setValue(slider.getValue()/SCALE);
					label.setText(""+(slider.getValue()/SCALE));
				}
			});
		}
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(sliders, BorderLayout.NORTH);
		
		window.setContentPane(container);
	}
}
