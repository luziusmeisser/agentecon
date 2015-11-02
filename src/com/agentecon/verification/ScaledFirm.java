package com.agentecon.verification;

import org.jacop.core.Store;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public class ScaledFirm implements IFirm {

	private IFirm wrapped;
	private FloatVar scaledDividend;
	private FloatVar scaledOutput;
	private FloatVar[] scaledInputs;

	public ScaledFirm(Store store, IFirm wrapped, double size) {
		this.wrapped = wrapped;
		this.scaledDividend = new FloatVar(store, wrapped.getType() + "_tot_dividend", 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(wrapped.getDividend(), size, scaledDividend));
		this.scaledOutput = new FloatVar(store, wrapped.getType() + "_tot_output", 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(wrapped.getOutput(), size, scaledOutput));

		Good[] inputs = wrapped.getInputGoods();
		this.scaledInputs = new FloatVar[inputs.length];
		String type = wrapped.getType();
		for (int i = 0; i < inputs.length; i++) {
			this.scaledInputs[i] = new FloatVar(store, type + "_tot_input_" + i, 0.0, Double.MAX_VALUE);
			store.impose(new PmulCeqR(wrapped.getInput(inputs[i]), size, scaledInputs[i]));
		}
	}

	@Override
	public void imposeConstraints(Good output, FloatVar outputPrice, Good[] inputs, FloatVar... inputPrices) {
		wrapped.imposeConstraints(output, outputPrice, inputs, inputPrices);
	}

	@Override
	public FloatVar getDividend() {
		return scaledDividend;
	}

	@Override
	public FloatVar getOutput() {
		return scaledOutput;
	}

	@Override
	public FloatVar getInput(Good inputType) {
		Good[] goods = wrapped.getInputGoods();
		for (int i = 0; i < goods.length; i++) {
			if (goods[i].equals(inputType)) {
				return scaledInputs[i];
			}
		}
		return null;
	}

	@Override
	public String getType() {
		return wrapped.getType();
	}

	@Override
	public Good[] getInputGoods() {
		return wrapped.getInputGoods();
	}

	@Override
	public Good getOutputGood() {
		return wrapped.getOutputGood();
	}

}
