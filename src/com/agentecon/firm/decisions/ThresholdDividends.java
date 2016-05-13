package com.agentecon.firm.decisions;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitely setting price levels.
 */
public class ThresholdDividends implements IFirmDecisions {
	
	private double threshold;
	
	public ThresholdDividends(double threshold){
		this.threshold = threshold;
	}

	public double calcDividend(double cash, double profits) {
		return cash - threshold;
	}

	public double calcCogs(double cash, double idealCogs){
		return idealCogs;
	}

}
