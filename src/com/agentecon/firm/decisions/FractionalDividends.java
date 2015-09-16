package com.agentecon.firm.decisions;

/**
 * Choose spendings to maximize profits.
 * Pay out a constant fraction of cash holdings as dividend, thereby implicitely setting price levels.
 */
public class FractionalDividends implements IFirmDecisions {
	
	double div = 0.0;
	
	public FractionalDividends(){
	}

	public double calcDividend(double cash, double profits) {
		return div;
	}

	public double calcCogs(double cash, double idealCogs){
		div = cash / 5;
		return div;
//		double budget = cash * 0.5;
//		if (idealCogs < budget){
//			return idealCogs;
//		} else {
//			return budget;
//		}
//		return div;
	}

}
