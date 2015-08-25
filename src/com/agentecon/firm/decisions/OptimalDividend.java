package com.agentecon.firm.decisions;

/**
 * 
 */
import com.agentecon.price.ExpSearchPrice;

public class OptimalDividend implements IFirmDecisions {

	private static int instance = 0;
	
	private int number = instance++;

	// private MovingAverage profits = new MovingAverage();
	private ExpSearchPrice dividends = new ExpSearchPrice(0.04);
	private boolean excessMoney = false;

	public double calcDividend(double cash, double profits) {
		// System.out.println(profits);
		if (excessMoney) {
			return profits + 1;
		} else {
			return profits - 1;
		}
		// dividends.adapt(excessMoney);
		// return Math.min(profits *dividends.getPrice(), cash / 2);
		// this.profits.add(profits);
		// this.dividendAdjustment.adapt(excessMoney > 0);
		// double max = getMoney().getAmount() / 3;
		// return Math.min(max, (this.profits.getAverage() * 15 + dividendAdjustment.getPrice()) / 16);
	}

	public double calcCogs(double cash, double cogs) {
		double actual = cash * 0.2;
		excessMoney = actual > cogs;
		if (number == 0) {
//			System.out.println(actual - cogs);
		}
		return actual;
	}

}
