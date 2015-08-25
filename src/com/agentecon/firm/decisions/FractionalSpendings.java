package com.agentecon.firm.decisions;

public class FractionalSpendings implements IFirmDecisions {

	private boolean excessMoney = false;
	
	public double calcDividend(double cash, double profits) {
		System.out.println(profits);
		return profits;
		// this.profits.add(profits);
		// this.dividendAdjustment.adapt(excessMoney > 0);
		// double max = getMoney().getAmount() / 3;
		// return Math.min(max, (this.profits.getAverage() * 15 + dividendAdjustment.getPrice()) / 16);
	}

	public double calcCogs(double cash, double cogs) {
		double actual = cash * 0.2;
		excessMoney = actual > cogs; 
		return actual;
	}

}
