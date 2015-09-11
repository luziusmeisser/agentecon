package com.agentecon.firm.decisions;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitely setting price levels.
 */
public class FractionalDividends implements IFirmDecisions {

	public static double DIVIDEND_RATE = 0.1;

	public double calcDividend(double cash, double profits) {
		return mix(cash, profits, 5);
//		return cash - 800; // TEMP
	}

	private double mix(double small, double large, int totweight) {
		return (small + large*(totweight - 1)) / totweight;
	}

	public double calcCogs(double cash, double idealCogs){
		double budget = cash * 0.5;
		if (idealCogs < budget){
			return idealCogs;
		} else {
			return budget;
		}
	}

}
