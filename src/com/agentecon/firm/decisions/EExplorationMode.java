package com.agentecon.firm.decisions;

public enum EExplorationMode {

	IDEAL_COST, IDEAL_COST2, EXPECTED, KNOWN, PAIRED;

	public double selectCosts(IFinancials metrics) {
		switch (this) {
		default:
		case IDEAL_COST2:
		case IDEAL_COST:
			return metrics.getIdealCogs();
		case EXPECTED:
			return metrics.getPlannedCogs();
		case KNOWN:
			return metrics.getLatestCogs();
		case PAIRED:
			return metrics.getLatestCogs();
		}
	}

	public double selectRevenue(IFinancials metrics, double laborshare) {
		switch (this) {
		default:
		case IDEAL_COST:
		case EXPECTED:
		case PAIRED:
			return metrics.getExpectedRevenue();
		case IDEAL_COST2:
		case KNOWN:
			return metrics.getLatestRevenue();
		}
	}

}
