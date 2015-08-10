package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

public class CobbDouglasProduction extends AbstractProductionFunction {

	public CobbDouglasProduction(Good output, Weight... weights) {
		super(output, weights);
	}
	
	public CobbDouglasProduction scale(double returnsToScale){
		double current = getReturnsToScale();
		double factor = returnsToScale / current;
		Weight[] newWeights = new Weight[inputs.length];
		for (int i=0; i<newWeights.length; i++){
			newWeights[i] = new Weight(inputs[i].good, inputs[i].weight * factor);
		}
		return new CobbDouglasProduction(getOutput(), newWeights);
	}

	public double getReturnsToScale() {
		double tot = 0.0;
		for (Weight w : inputs) {
			tot += w.weight;
		}
		return tot;
	}

	@Override
	public double produce(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production *= Math.pow(in.consume(), input.weight);
		}
		production = Math.max(production, 1.0);
		inventory.getStock(getOutput()).add(production);
		return production;
	}

}
