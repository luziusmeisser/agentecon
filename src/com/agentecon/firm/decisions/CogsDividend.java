package com.agentecon.firm.decisions;

public class CogsDividend implements IFirmDecisions {

	private int mode;
	private double dividendRatio;

	public CogsDividend(double returnsToScale, int mode) {
		this.mode = mode;
		this.dividendRatio = returnsToScale / (1.0 - returnsToScale);
	}

	public double calcCogs(double cash, double cogs) {
		return cash / 5.0;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		switch (mode) {
		case 0:
		default:
			return calcCogs(metrics.getCash(), 0.0);	// 55.20651895154295
		case 1:
			return metrics.getLatestCogs();				// 55.22325880317713
		case 2:
			return metrics.getIdealCogs();				// 45.55217246381391 Blows up
		case 3:
			return metrics.getLatestRevenue() / 2;		// 55.2233039078274	Lowest peak
		case 4:
			return metrics.getExpectedRevenue() / 2;	// 55.22332350825784
		case 5:
			return metrics.getLatestRevenue() - metrics.getLatestCogs();	// 54.83194273768103 Prices through the roof
		case 6:
			return metrics.getExpectedRevenue() - metrics.getLatestCogs();	// 50.88448137265661 Lots of micro-volatility
		case 7:
			return metrics.getExpectedRevenue() - metrics.getIdealCogs();	// 55.22338764543314 Very moderate peaks
		case 8:
			return metrics.getIdealCogs() * 2 - metrics.getLatestCogs();	// 45.55217613220216 Blows up
		}
	}

}
