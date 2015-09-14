package com.agentecon.firm;

import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.good.IStock;

public abstract class Financials implements IFinancials {
	
	private IStock money;
	private OutputFactor output;
	private InputFactor[] inputs;

	public Financials(IStock money, InputFactor[] inputs, OutputFactor output) {
		this.money = money;
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public double getCash() {
		return money.getAmount();
	}

	@Override
	public double getLatestCogs() {
		double cogs = 0.0;
		for (InputFactor input: inputs){
			cogs += input.getVolume();
		}
		return cogs;
	}

	@Override
	public double getLatestRevenue() {
		return output.getVolume();
	}

	@Override
	public double getExpectedRevenue() {
		return output.getPrice() * output.getStock().getAmount();
	}

}
