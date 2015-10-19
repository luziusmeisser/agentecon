package com.agentecon.firm.decisions;

import com.agentecon.stats.Numbers;

public class StrategyExploration implements IFirmDecisions {

	public static final int TYPES = 11;

	private double laborshare, fr;
	private EExplorationMode mode;

	public StrategyExploration(double laborshare, double fr, EExplorationMode mode) {
		this.laborshare = laborshare;
		this.mode = mode;
		this.fr = fr;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new StrategyExploration(laborshare, fr, mode);
	}

	public double calcCogs(double cash, double cogs) {
		return cash / 5.0;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double c = mode.selectCosts(metrics);
		double r = mode.selectRevenue(metrics, laborshare);
		return fr * r + calcFc() * c;
	}

	protected double calcFc() {
		return (1-laborshare)/laborshare - fr/laborshare;
	}

	public String toString() {
		return mode + " exploration\t" + Numbers.toString(fr) + "\t" + Numbers.toString(calcFc());
	}

}
