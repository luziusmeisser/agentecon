package com.agentecon.verification;

import org.jacop.core.Store;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public class ScaledConsumer implements IConsumer {

	private Store store;
	private IConsumer wrapped;

	private double scale;
	private FloatVar labor;
	private FloatVar[] consumption;

	public ScaledConsumer(Store store, IConsumer wrapped, double size) {
		this.wrapped = wrapped;
		this.store = store;
		this.scale = size;

		this.labor = new FloatVar(store, wrapped.getType() + "_tot_labor", 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(wrapped.getWorkHours(), size, labor));

		Good[] inputs = wrapped.getInputs();
		this.consumption = new FloatVar[inputs.length];
		String type = wrapped.getType();
		for (int i = 0; i < inputs.length; i++) {
			this.consumption[i] = new FloatVar(store, type + "_tot_input_" + i, 0.0, Double.MAX_VALUE);
			store.impose(new PmulCeqR(wrapped.getConsumption(inputs[i]), size, consumption[i]));
		}
	}

	@Override
	public void imposeConstraints(FloatVar dividend, Good input, FloatVar wage, Good[] outputs, FloatVar... goodsPrices) {
		FloatVar scaledDividend = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(scaledDividend, scale, dividend));
		this.wrapped.imposeConstraints(scaledDividend, input, wage, outputs, goodsPrices);
	}

	@Override
	public FloatVar getWorkHours() {
		return labor;
	}

	@Override
	public String getType() {
		return wrapped.getType();
	}

	@Override
	public FloatVar getConsumption(Good good) {
		Good[] goods = wrapped.getInputs();
		for (int i = 0; i < goods.length; i++) {
			if (goods[i].equals(good)) {
				return consumption[i];
			}
		}
		return null;
	}

	@Override
	public Good getWorkType() {
		return wrapped.getWorkType();
	}

	@Override
	public Good[] getInputs() {
		return wrapped.getInputs();
	}

}
