package com.agentecon.firm.decisions;

public class ExpectedRevenueBasedStrategy implements IFirmDecisions {

	public static final int TYPES = 11;

	private double laborshare;
	private double profitshare;

	public ExpectedRevenueBasedStrategy(double laborshare) {
		this.laborshare = laborshare;
		this.laborshare = 1.0 - laborshare;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new ExpectedRevenueBasedStrategy(laborshare);
	}

	public double calcCogs(double cash, double cogs) {
		return cash / 5.0;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		return metrics.getExpectedRevenue() * profitshare;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " with profitshare " + profitshare;
	}

}
