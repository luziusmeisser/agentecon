package com.agentecon.sim;

import com.agentecon.consumer.Weight;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;

public class ProductionWeights {

	private static final int INPUTS = 3;
	
	private static final double[] WEIGHTS = new double[]{6.0, 4.0, 2.0};
	
	private Good[] inputs;
	private Good[] outputs;

	public ProductionWeights(Good[] inputs, Good[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	public IProductionFunction createUtilFun(int type, double retToScale){
		Weight[] prefs = new Weight[INPUTS];
		for (int i=0; i<INPUTS; i++){
			Good good = inputs[(i + type) % inputs.length];
			prefs[i] = new Weight(good, WEIGHTS[i]);
		}
		return new CobbDouglasProduction(outputs[type], prefs).scale(retToScale);
	}

}
