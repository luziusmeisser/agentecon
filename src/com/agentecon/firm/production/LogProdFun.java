package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

public class LogProdFun extends AbstractProductionFunction {

	public LogProdFun(Good output, Weight... weights) {
		super(output, weights);
	}

	@Override
	public double produce(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production += input.weight * Math.log(1 + in.consume());
		}
		inventory.getStock(getOutput()).add(production);
		return production;
	}

}
