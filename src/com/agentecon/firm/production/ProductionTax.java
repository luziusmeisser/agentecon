package com.agentecon.firm.production;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.stats.Numbers;

public class ProductionTax implements IProductionFunction {
	
	private double taxRate;
	private IProductionFunction wrapped;

	public ProductionTax(IProductionFunction wrapped, double taxRate) {
		this.wrapped = wrapped;
		this.taxRate = taxRate;
	}

	@Override
	public Good[] getInput() {
		return wrapped.getInput();
	}

	@Override
	public Good getOutput() {
		return wrapped.getOutput();
	}

	@Override
	public double produce(Inventory inventory) {
		IStock output = inventory.getStock(getOutput());
		double before = output.getAmount();
		double production = wrapped.produce(inventory);
		assert Numbers.equals(output.getAmount() - before, production);
		double tax = production * taxRate;
		output.remove(tax);
		return production - tax;
	}

	@Override
	public double getCostOfMaximumProfit(IPriceProvider prices) {
		return wrapped.getCostOfMaximumProfit(prices);
	}

	@Override
	public double getExpenses(Good good, double price, double totalSpendings) {
		return wrapped.getExpenses(good, price, totalSpendings);
	}

	@Override
	public double[] getWeights() {
		return wrapped.getWeights();
	}

}
