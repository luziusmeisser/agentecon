package com.agentecon.firm.decisions;

public enum EExplorationMode {

	LATEST, LATEST_KNOWN, LATEST_PAIRED, LATEST_KNOWN_PAIRED;

	public double selectCosts(IFinancials metrics) {
		switch (this) {
		default:
		case LATEST:
			return metrics.getIdealCogs();
		case LATEST_KNOWN:
			return metrics.getLatestCogs();
		case LATEST_PAIRED:
			return metrics.getLatestCogs();
		case LATEST_KNOWN_PAIRED:
			throw new RuntimeException("TODO");
		}
	}

	public double selectRevenue(IFinancials metrics) {
		switch (this) {
		default:
		case LATEST:
		case LATEST_PAIRED:
			return metrics.getExpectedRevenue();
		case LATEST_KNOWN:
		case LATEST_KNOWN_PAIRED:
			return metrics.getLatestRevenue();
		}
	}

}
