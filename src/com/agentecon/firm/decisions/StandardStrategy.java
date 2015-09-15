package com.agentecon.firm.decisions;

public class StandardStrategy implements IFirmDecisions {

	@Override
	public double calcCogs(double cash, double idealCogs) {
		return idealCogs;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		return metrics.getLatestRevenue() - metrics.getLatestCogs();
	}

	@Override
	public IFirmDecisions duplicate() {
		return new StandardStrategy();
	}

}
