package com.agentecon.firm.decisions;

public enum EExplorationMode {

	IDEAL_COST, IDEAL_COST2, IDEAL_BOTH, EXPECTED, PLANNED, KNOWN, PAIRED;

	public double selectCosts(IFinancials metrics) {
		switch (this) {
		default:
		case IDEAL_BOTH:
		case IDEAL_COST2:
		case IDEAL_COST:
			return metrics.getIdealCogs();
		case PLANNED:
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
		case IDEAL_BOTH:
			return metrics.getIdealCogs()/laborshare*(1-laborshare);
		case PLANNED:
			return metrics.getPlannedCogs()/laborshare*(1-laborshare);
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
