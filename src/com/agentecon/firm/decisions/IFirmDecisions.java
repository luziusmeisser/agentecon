package com.agentecon.firm.decisions;

public interface IFirmDecisions {
	
	public double calcDividend(double cash, double profits);

	public double calcCogs(double cash, double idealCogs);

}
