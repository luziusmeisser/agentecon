package com.agentecon.firm.decisions;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitely setting price levels.
 */
public class FractionalDividends implements IFirmDecisions {

	public static double DIVIDEND_RATE = 0.1;

	public double calcDividend(double cash, double profits) {
		return Math.max(0, cash - 800); // TEMP
	}

	public double calcCogs(double cash, double idealCogs){
		double budget = cash * 0.5;
		double actual = Math.min(budget, idealCogs);
		return actual;
	}

}
