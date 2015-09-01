package com.agentecon.firm.decisions;

public class FractionalSpendings implements IFirmDecisions {

	public double calcDividend(double cash, double profits) {
//		return profits; TEMP
		return cash * 0.2;
	}

	public double calcCogs(double cash, double cogs) {
		return cash * 0.2;
	}

}
