package com.agentecon.firm.decisions;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitely setting price levels.
 */
public class FractionalDividends implements IFirmDecisions {

	public static double DIVIDEND_RATE = 0.1;
	
	private double potential;

	public double calcDividend(double cash, double profits) {
		return potential * (cash - 800); // TEMP
	}

	public double calcCogs(double cash, double idealCogs){
		double budget = cash * 0.5;
		if (idealCogs < budget){
			potential = 1.0;
			return idealCogs;
		} else {
			potential = ((double)budget) / idealCogs;
			return budget;
		}
	}

}
