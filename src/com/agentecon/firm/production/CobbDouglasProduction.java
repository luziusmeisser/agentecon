package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

public class CobbDouglasProduction extends AbstractProductionFunction {

	public static final double PRODUCTIVITY = 10;
	
	private double constantFactor;

	public CobbDouglasProduction(Good output, Weight... weights) {
		this(output, PRODUCTIVITY, weights);
	}

	public CobbDouglasProduction(Good output, double constantFactor, Weight... weights) {
		super(output, weights);
		this.constantFactor = constantFactor;
	}

	public CobbDouglasProduction scale(double returnsToScale) {
		double current = getReturnsToScale();
		double factor = returnsToScale / current;
		Weight[] newWeights = new Weight[inputs.length];
		for (int i = 0; i < newWeights.length; i++) {
			newWeights[i] = new Weight(inputs[i].good, inputs[i].weight * factor);
		}
		return new CobbDouglasProduction(getOutput(), constantFactor, newWeights);
	}

	public double getReturnsToScale() {
		return super.getTotalWeight();
	}

	@Override
	public double produce(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production *= Math.pow(in.consume(), input.weight);
		}
		production = constantFactor * Math.max(production, 1.0);
		inventory.getStock(getOutput()).add(production);
		return production;
	}
	
//	@Override
//	public double getMaximumProfits(IPriceProvider prices) {
//		double cost = getCostOfMaximumProfit(prices);
//		
//		return 0;
//	}
	
	@Override
	public double getCostOfMaximumProfit(IPriceProvider prices) {
		double totWeight = getTotalWeight();
		if (totWeight >= 1.0) {
			// increasing returns to scale
			return Double.MAX_VALUE;
		} else {
			double outprice = prices.getPrice(output) * constantFactor;
			double prod = getCBHelperProduct(prices);
			double factor = Math.pow(outprice * prod, 1 / (1 - totWeight));
			return totWeight * factor;
		}
	}

	private double getCBHelperProduct(IPriceProvider prices) {
		double tot = 1.0;
		for (Weight in : inputs) {
			double price = prices.getPrice(in.good);
			if (Double.isInfinite(price)) {
				// skip, not obtainable
			} else {
				tot *= Math.pow(in.weight / price, in.weight);
			}
		}
		return tot;
	}

	@Override
	public double getExpenses(Good good, double price, double totalSpendings) {
		double offerPerWeight = totalSpendings / getTotalWeight();
		return offerPerWeight * getWeight(good);
	}

}
