package com.agentecon.firm.decisions;

/**
 * 
 */
import com.agentecon.price.ExpSearchPrice;

public class OptimalDividend implements IFirmDecisions {

//	private MovingAverage profits = new MovingAverage();
	private ExpSearchPrice dividends = new ExpSearchPrice(0.04);
	private boolean excessMoney = false;
	
	public double calcDividend(double cash, double profits) {
		dividends.adapt(excessMoney);
//		System.out.println(profits);
		return Math.min(dividends.getPrice(), cash / 2);
		// this.profits.add(profits);
		// this.dividendAdjustment.adapt(excessMoney > 0);
		// double max = getMoney().getAmount() / 3;
		// return Math.min(max, (this.profits.getAverage() * 15 + dividendAdjustment.getPrice()) / 16);
	}

	public double calcCogs(double cash, double cogs) {
		double actual = cash * 0.2;
		excessMoney = actual > cogs; 
		return actual;
	}

}
