package com.agentecon.firm;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

public class LogProdFun implements IProductionFunction {

	private final Good output;
	private final Weight[] inputs;

	public LogProdFun(Good output, Weight... weights) {
		assert output != null;
		this.output = output;
		this.inputs = weights;
	}

	@Override
	public Good[] getInput() {
		Good[] goods = new Good[inputs.length];
		for (int i=0; i<goods.length; i++){
			goods[i] = inputs[i].good;
		}
		return goods;
	}

	@Override
	public Good getOutput() {
		return output;
	}

	@Override
	public double getWeight(Good input) {
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].good.equals(input)) {
				return inputs[i].weight;
			}
		}
		return 0;
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
