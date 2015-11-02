package com.agentecon.events;

import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.util.Average;

public class FirstDayProduction implements IFirmListener {

	private int count;
	private Average avg;
	private Good output;

	public FirstDayProduction(int firms) {
		this.count = firms * 10;
		this.avg = new Average();
	}

	@Override
	public void notifyProduced(IPublicCompany inst, String producer, IStock[] inputs, IStock output) {
		if (count > 0) {
			assert this.output == null || this.output.equals(output.getGood());
			assert output.getAmount() < 100;
			this.output = output.getGood();
			this.avg.add(output.getAmount());
			count--;
		}
	}

	@Override
	public void reportDividend(IPublicCompany inst, double amount) {
	}

	public Good getGood() {
		return output;
	}

	public double getAmount() {
		return avg.getAverage();
	}

	@Override
	public void reportResults(IPublicCompany inst, double revenue, double cogs, double profits) {
	}

}
