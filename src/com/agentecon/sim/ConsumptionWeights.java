package com.agentecon.sim;

import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.good.Good;

public class ConsumptionWeights {

	public static final double TIME_WEIGHT = 12.0;
	public static final double[] WEIGHTS = new double[] { 3.0, 1.0, 6.0 };
	private static final int CONSUMPTION_GOODS = WEIGHTS.length;

	private Weight[] inputs;
	private Weight[] outputs;

	public ConsumptionWeights(Good[] inputs, Good[] outputs) {
		this.inputs = new Weight[inputs.length];
		this.outputs = new Weight[outputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputs[i] = new Weight(inputs[i], TIME_WEIGHT);
		}
		for (int i = 0; i < outputs.length; i++) {
			this.outputs[i] = new Weight(outputs[i], WEIGHTS[i % CONSUMPTION_GOODS]);
		}
	}

	public LogUtil createUtilFun(int type, int number) {
		int count = Math.min(CONSUMPTION_GOODS, outputs.length);
		Weight[] prefs = new Weight[count + 1];
		for (int i = 0; i < count; i++) {
			prefs[i] = outputs[(i + type) % outputs.length];
		}
		prefs[prefs.length - 1] = inputs[type];
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
