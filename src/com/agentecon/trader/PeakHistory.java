package com.agentecon.trader;

import java.util.Iterator;
import java.util.LinkedList;

public class PeakHistory {
	
	private boolean max;
	private LinkedList<Peak> peaks;
	
	public PeakHistory(boolean max){
		this.max = max;
		this.peaks = new LinkedList<>();
	}
	
	public void report(int day, double price){
		Peak current = new Peak(day, price);
		Iterator<Peak> iter = peaks.iterator();
		while (iter.hasNext()){
			if (max && !current.isBelow(iter.next())){
				iter.remove();
			} else if (!max && !current.isAbove(iter.next())){
				iter.remove();
			}
		}
		peaks.add(current);
	}
	
	public double findNextPeak(int day){
		Iterator<Peak> iter = peaks.iterator();
		while (iter.hasNext()){
			Peak next = iter.next();
			if (next.isPast(day)){
				iter.remove();
			} else {
				return next.getPrice();
			}
		}
		return 0.0;
	}

}
