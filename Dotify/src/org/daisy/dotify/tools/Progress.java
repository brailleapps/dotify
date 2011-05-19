package org.daisy.dotify.tools;

import java.util.Date;

public class Progress {
	private double progress = 0;
	private Date eta; 
	private long tstamp;
	double step;
	
	public Progress() {
		progress = 0;
		eta = new Date();
		tstamp = System.currentTimeMillis();
		step = -1;
	}
	
	public Date getETA() {
		return eta;
	}
	
	public void setProgress(double val) {
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
	
	public double getProgress() {
		return progress;
	}

}
