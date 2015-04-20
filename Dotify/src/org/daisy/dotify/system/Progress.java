package org.daisy.dotify.system;

import java.util.Date;

/**
 * Provides simple progress measuring and reporting. The internal stop watch is started
 * as soon as an instance is created.
 * @author Joel Håkansson
 *
 */
public class Progress {
	private double progress = 0;
	private Date eta; 
	private long tstamp;
	private long start;
	double step;
	
	/**
	 * Creates a new instance. The internal stop watch is started immediately.
	 */
	public Progress() {
		progress = 0;
		eta = new Date();
		tstamp = System.currentTimeMillis();
		start = tstamp;
		step = -1;
	}
	
	/**
	 * Gets the estimated time of completion.
	 * @return returns the estimated time of completion 
	 */
	public Date getETA() {
		return eta;
	}
	
	/**
	 * Gets the start date, in other words the time of instantiation.
	 * @return returns the start date
	 */
	public Date getStart() {
		return new Date(start);
	}
	
	/**
	 * Sets the current progress of the operation, in percent.
	 * @param val the current progress, in percent.
	 * @throws IllegalArgumentException if the value is out of range
	 * @throws IllegalStateException if the progress is less than or equal to previous reported value.
	 */
	public void updateProgress(double val) {
		if (val<0 || val>1) {
			throw new IllegalArgumentException("Value out of range [0, 1]: " + val);
		}
		if (val<=progress) {
			throw new IllegalStateException("Negative or zero progress.");
		}
		long now = System.currentTimeMillis();
		double pD = val - progress;
		long tD = now - tstamp;
		if (step > 0) {
			step = step * 0.3 + (tD / pD) * 0.7; 
		} else {
			step = (tD / pD);
		}
		double etaMs = step * (1 - val);
		//System.out.println(pD + " " + tD  + " " + etaMs);
		eta = new Date(now + (long)Math.round(etaMs));// + etaMs);
		tstamp = now;
		progress = val;
	}
	
	/**
	 * Gets the current progress in percent.
	 * @return returns the current progress
	 */
	public double getProgress() {
		return progress;
	}
	
	/**
	 * Gets the time since instantiation, in milliseconds.
	 * @return returns the number of milliseconds since instantiation.
	 */
	public long timeSinceStart() {
		return System.currentTimeMillis()-start;
	}

}
