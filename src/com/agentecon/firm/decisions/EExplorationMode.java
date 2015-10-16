package com.agentecon.firm.decisions;

public enum EExplorationMode {

	IDEAL_COST, PLANNED, KNOWN, PAIRED, KNOWN_PAIRED;

	public double selectCosts(IFinancials metrics) {
		switch (this) {
		default:
		case IDEAL_COST:
			return metrics.getIdealCogs();
		case PLANNED:
			return metrics.getPlannedCogs();
		case KNOWN:
			return metrics.getLatestCogs();
		case PAIRED:
			return metrics.getLatestCogs();
		case KNOWN_PAIRED:
			throw new RuntimeException("TODO");
		}
	}

	public double selectRevenue(IFinancials metrics) {
		switch (this) {
		default:
		case IDEAL_COST:
		case PLANNED:
		case PAIRED:
			return metrics.getExpectedRevenue();
		case KNOWN:
		case KNOWN_PAIRED:
			return metrics.getLatestRevenue();
		}
	}

}
