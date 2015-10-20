package com.agentecon.firm.decisions;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitely setting price levels.
 */
public class DifferentialDividend implements IFirmDecisions {

	public static double DIVIDEND_RATE = 0.1;

	public DifferentialDividend(){
	}
	
	public double calcCogs(double cash, double idealCogs){
		double budget = cash * 0.5;
		if (idealCogs < budget){
			return idealCogs;
		} else {
			return budget;
		}
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		return metrics.getCash() - 800;
	}
	
	@Override
	public IFirmDecisions duplicate() {
		return new DifferentialDividend();
	}

}
