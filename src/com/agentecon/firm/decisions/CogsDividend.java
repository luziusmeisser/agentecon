package com.agentecon.firm.decisions;

public class CogsDividend implements IFirmDecisions {

	private int mode;
	private double divs;
	private double dividendRatio;

	public CogsDividend(double returnsToScale, int mode) {
		this.mode = mode;
		this.dividendRatio = returnsToScale / (1.0 - returnsToScale);
	}

	public double calcCogs(double cash, double cogs) {
//		if (mode == 0) {
//			return divs;
//		} else {
			divs = cash / 5.0;
			return divs;
//		}
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		switch (mode) {
		case 0:
		default:
//			divs = metrics.getCash() / 5; 	// 55.22366121385854	// 6
			return divs;					// 55.22365403882499
		case 1:
			return metrics.getLatestCogs(); // 55.22325880317713	// 4 
		case 2:
			return metrics.getIdealCogs(); // 45.55217246381391 Blows up
		case 3:
			return metrics.getLatestRevenue() / 2; // 55.2233039078274 Lowest peak	// 5
		case 4:
			return metrics.getExpectedRevenue() / 2; // 55.22332350825784	// 4
		case 5:
			return metrics.getLatestRevenue() - metrics.getLatestCogs(); // 54.83194273768103 Prices through the roof
		case 6:
			return metrics.getExpectedRevenue() - metrics.getLatestCogs(); // 50.88448137265661 Lots of micro-volatility
		case 7:
			return metrics.getExpectedRevenue() - metrics.getIdealCogs(); // 55.22338764543314 moderate peaks	// 3
		case 8:
			return metrics.getIdealCogs() * 2 - metrics.getLatestCogs(); // 45.55217613220216 Blows up
		case 9:
			return metrics.getLatestCogs() * 2 - metrics.getIdealCogs(); // 55.19501169759995 Very volatile trade
		case 10:
			return metrics.getLatestRevenue() / 2 + metrics.getLatestCogs() - metrics.getIdealCogs() * 1;
		}
	}

	public String getDescription() {
		switch (mode) {
		default:
			return "???";
		case 0:
			return "cash / 5 and cogs the same";
		case 1:
			return "latest cogs";
		case 2:
			return "ideal cogs";
		case 3:
			return "latest revenue / 2";
		case 4:
			return "expected revenue / 2";
		case 5:
			return "latest revenue - latest cogs";
		case 6:
			return "expected revenue - latest cogs";
		case 7:
			return "expected revenue - ideal cogs";
		case 8:
			return "2 ideal cogs - latest cogs";
		case 9:
			return "2 latest cogs - ideal cogs";
		}
	}

}
