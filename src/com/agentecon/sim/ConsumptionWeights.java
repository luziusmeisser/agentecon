package com.agentecon.sim;

import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;

public class ConsumptionWeights {

	private static final int CONSUMPTION_GOODS = 3;
	
	public static final double TIME_WEIGHT = 12.0;
	public static final double[] WEIGHTS = new double[]{4.0, 2.0, 7.0};
	
	private Good[] inputs;
	private Good[] outputs;

	public ConsumptionWeights(Good[] inputs, Good[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	public LogUtil createUtilFun(int type, int number){
		number = 0; // TEMP
		type = 0; // TEMP
		int count = Math.min(CONSUMPTION_GOODS, outputs.length);
		Weight[] prefs = new Weight[count + 1];
		for (int i=0; i<count; i++){
			Good good = outputs[(i + number) % outputs.length];
			prefs[i] = new Weight(good, WEIGHTS[i]);
		}
		prefs[prefs.length - 1] = new Weight(inputs[type], TIME_WEIGHT);
		return new LogUtil(prefs);
	}

	public IUtilityFactory getFactory(final int type) {
		return new IUtilityFactory() {
			
			@Override
			public IUtility create(int number) {
				return createUtilFun(type, number);
			}
		};
	}

}
