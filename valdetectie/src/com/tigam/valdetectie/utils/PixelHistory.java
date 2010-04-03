package com.tigam.valdetectie.utils;

/**
 * @author Rick van Steen <rick.van.steen@hva.nl>
 * @author Koen Bollen <koen.bollen@hva.nl>
 * @author Nils Dijk <nils.dijk@hva.nl>
 * @author Sam Zwaan <sam.zwaan@hva.nl>
 */
public class PixelHistory {
	
	private final int period;
	private final int pixels;
	
	private int size;
	
	private final int [][] frameHistory;
	private final int [][] pixelHistory;
	private int historyIndex;
	
	public PixelHistory(int period,int pixels){
		this.period = period;
		this.pixels = pixels;
		
		this.historyIndex = 0;
		this.size = 0;
		this.frameHistory = new int[period][pixels];
		this.pixelHistory = new int[pixels][period];
	}
	
	public void add(int [] pixels){
		insert(pixels);
	}
	
	public int [] insert(int [] pixels){
		if (pixels.length != this.pixels) throw new RuntimeException("pixel size doesn't match");
		int [] old = this.frameHistory[this.historyIndex];
		this.frameHistory[this.historyIndex] = pixels;
		//System.arraycopy(pixels, 0, this.frameHistory[this.historyIndex], 0, this.pixels);
		for (int i=0; i<this.pixels; i++) this.pixelHistory[i][this.historyIndex] = pixels[i];
		this.historyIndex++;
		this.historyIndex%=period;
		if (size < period){
			this.size++;
			return null;
		}
		return old;
	}
	
	public int size(){
		return size;
	}
	
	public int [] getFrameHistory(int i){
		i=+this.historyIndex-1+period;
		i%=period;
		return this.frameHistory[i];
	}
	
	public int [] getPixelHistory(int i){
		return this.pixelHistory[i];
	}
}
