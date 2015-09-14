package com.agentecon.firm.decisions;

public class CogsDividend implements IFirmDecisions {
	
	private double dividend;
	private double dividendRatio;
	
	public CogsDividend(double returnsToScale){
		this.dividendRatio = returnsToScale / (1.0 - returnsToScale);
	}

	public double calcDividend(double cash, double profits) {
		return dividend;
	}

	public double calcCogs(double cash, double cogs) {
		double fraction = cash / 5.0;
		this.dividend = dividendRatio * fraction;
		return fraction;
	}

}
