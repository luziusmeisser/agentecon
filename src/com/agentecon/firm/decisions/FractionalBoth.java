package com.agentecon.firm.decisions;

/**
 * Obviously not optimal.
 */
public class FractionalBoth implements IFirmDecisions {

	public double calcDividend(double cash, double profits) {
		return cash * 0.1;
	}

	public double calcCogs(double cash, double cogs) {
		return cash * 0.2;
	}

}
