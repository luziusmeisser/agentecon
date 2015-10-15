package com.agentecon.firm.decisions;

import com.agentecon.stats.Numbers;

public class StrategyExploration implements IFirmDecisions {

	public static final int TYPES = 11;

	private double laborshare, a, b;
	private EExplorationMode mode;

	public StrategyExploration(double laborshare, double a, double b, EExplorationMode mode) {
		this.laborshare = laborshare;
		this.mode = mode;
		this.a = a;
		this.b = b;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new StrategyExploration(laborshare, a, b, mode);
	}

	public double calcCogs(double cash, double cogs) {
		return cash / 5.0;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double c = mode.selectCosts(metrics);
		double r = mode.selectRevenue(metrics);
		double res1 = r - c;
		double res2 = (1 - laborshare) * r;
		double res3 = (1 - laborshare) / laborshare * c;
		return a * res1 + b * res2 + (1 - a - b) * res3;
	}

	public String toString() {
		return "Exploration\t" + Numbers.toString(a) + "\t" + Numbers.toString(b);
	}

}
