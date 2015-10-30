package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.price.IPrice;

public class LinearProdFun extends AbstractProductionFunction {

	public LinearProdFun(Good output, Weight weight) {
		super(output, weight);
	}

	@Override
	public double useInputs(Inventory inventory) {
		double production = 0.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production += input.weight * Math.max(1.0, in.consume());
		}
		return production;
	}

	public boolean shouldProduce(IPrice inputPrice, IPrice outputPrice) {
		double weight = inputs[0].weight;
		return weight * outputPrice.getPrice() > inputPrice.getPrice();
	}

	@Override
	public double getCostOfMaximumProfit(IPriceProvider prices) {
		return Double.MAX_VALUE;
	}

	@Override
	public double getExpenses(Good good, double price, double totalSpendings) {
		return Double.MAX_VALUE;
	}

}
