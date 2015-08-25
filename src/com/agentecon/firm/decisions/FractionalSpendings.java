package com.agentecon.firm.decisions;

/**
 * Elegant, but not optimal... :(
 */
public class FractionalSpendings implements IFirmDecisions {

	public double calcDividend(double cash, double profits) {
		return profits;
	}

	public double calcCogs(double cash, double cogs) {
		return cash * 0.2;
	}

}
