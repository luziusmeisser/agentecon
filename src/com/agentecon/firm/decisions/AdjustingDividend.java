package com.agentecon.firm.decisions;

public class AdjustingDividend implements IFirmDecisions {
	
	private static final double SPENDING_FRACTION = 0.2;

	private boolean excessMoney;
	
	public AdjustingDividend(){
		this(false);
	}

	public AdjustingDividend(boolean excessMoney) {
		this.excessMoney = excessMoney;
	}

	public double calcCogs(double cash, double cogs) {
		double actual = cash * SPENDING_FRACTION;
		excessMoney = actual > cogs;
		return actual;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double profits = metrics.getLatestRevenue() - metrics.getLatestCogs();
		double adjustedProfits = profits + (excessMoney ? 1 : -1);
		return Math.min(adjustedProfits, metrics.getCash() / 2);
	}

	@Override
	public AdjustingDividend duplicate() {
		return new AdjustingDividend(excessMoney);
	}

}
