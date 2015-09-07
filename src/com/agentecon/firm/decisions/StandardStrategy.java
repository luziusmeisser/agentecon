package com.agentecon.firm.decisions;

public class StandardStrategy implements IFirmDecisions {

	@Override
	public double calcDividend(double cash, double profits) {
		return profits;
	}

	@Override
	public double calcCogs(double cash, double idealCogs) {
		return idealCogs;
	}

}
