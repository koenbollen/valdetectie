package com.tigam.valdetectie.streams;

public final class FrameDropImageStream implements ImageStream {

	final class Poller implements Runnable {

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				int[] data = source.read();
				synchronized (mutex) {
					image = data;
					mutex.notifyAll();
				}
			}
		}

	}

	protected final ImageStream source;

	protected int[] image;
	protected final Object mutex;

	public FrameDropImageStream(ImageStream source) {
		this.source = source;
		this.mutex = new Object();

		Thread t = new Thread(new Poller());
		t.setDaemon(true);
		t.setName("Frame Dropper");
		t.start();
	}

	@Override
	public int height() {
		return this.source.height();
	}

	@Override
	public int[] read() {
		int[] data;
		synchronized (mutex) {
			try {
				while (image == null)
					mutex.wait();
			} catch (InterruptedException ball) {
				return null;
			}
			data = image;
			image = null;
		}
		return data;
	}

	@Override
	public int width() {
		return this.source.width();
	}

}
