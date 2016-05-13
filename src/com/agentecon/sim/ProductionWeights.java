package com.agentecon.sim;

import com.agentecon.consumer.Weight;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;

public class ProductionWeights {

	public static final double[] DEFAULT_WEIGHTS = new double[]{6.0, 2.0, 4.0};
	
	private Good[] inputs;
	private double[] weights;
	private Good[] outputs;
	
	public ProductionWeights(Good[] inputs, Good[] outputs) {
		this(inputs, DEFAULT_WEIGHTS, outputs);
	}

	public ProductionWeights(Good[] inputs, double[] weights, Good[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.weights = weights;
	}
	
	public IProductionFunction createProdFun(int type, double retToScale){
		Weight[] prefs = new Weight[Math.min(inputs.length, DEFAULT_WEIGHTS.length)];
		for (int i=0; i<prefs.length; i++){
			Good good = inputs[(i + type) % inputs.length];
			prefs[i] = new Weight(good, DEFAULT_WEIGHTS[i]);
		}
		return new CobbDouglasProduction(outputs[type], prefs).scale(retToScale);
	}

}
