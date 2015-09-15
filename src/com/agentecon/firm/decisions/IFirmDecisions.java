package com.agentecon.firm.decisions;

public interface IFirmDecisions {
	
	public double calcDividend(IFinancials metrics);

	public double calcCogs(double cash, double idealCogs);

	public IFirmDecisions duplicate();

}
