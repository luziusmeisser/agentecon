package com.agentecon.events;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.util.Average;

public class FirstDayProduction implements IFirmListener {

	private int count;
	private Average avg;
	private Good output;

	public FirstDayProduction(int firms) {
		this.count = firms * 50;
		this.avg = new Average();
	}

	@Override
	public void notifyProduced(String producer, IStock[] inputs, IStock output) {
		if (count > 0) {
			assert this.output == null || this.output.equals(output.getGood());
			assert output.getAmount() < 100;
			this.output = output.getGood();
			this.avg.add(output.getAmount());
			count--;
		}
	}

	@Override
	public void reportDividend(double amount) {
	}

	public Good getGood() {
		return output;
	}

	public double getAmount() {
		return avg.getAverage();
	}

}
