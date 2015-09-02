package com.agentecon.firm.decisions;

public class OptimalDividend implements IFirmDecisions {
	
	private static final double SPENDING_FRACTION = 0.2;

	private boolean excessMoney = false;

	public double calcDividend(double cash, double profits) {
		double adjustedProfits = profits + (excessMoney ? 1 : -1);
		return Math.min(adjustedProfits, cash / 2);
	}

	public double calcCogs(double cash, double cogs) {
		double actual = cash * SPENDING_FRACTION;
		excessMoney = actual > cogs;
		return actual;
	}

}
