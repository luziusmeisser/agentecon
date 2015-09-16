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
		div = cash / 5;
		return div;
	}

	public double calcCogs(double cash, double idealCogs){
		return div;
//		return Math.min(cash / 2, idealCogs);
//		double revenue = cash / 5;
//		double laborShare = StolperSamuelson.RETURNS_TO_SCALE;
//		double profitShare = 1.0 - laborShare;
//		this.div = revenue * profitShare;
//		return revenue * laborShare;
//		double budget = cash * 0.5;
//		if (idealCogs < budget){
//			return idealCogs;
//		} else {
//			return budget;
//		}
//		return div;
	}

}
