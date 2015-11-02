package com.agentecon.verification;

import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public interface IFirm {
	
	public void imposeConstraints(Good outputs, FloatVar outputPrice, Good[] inputs, FloatVar... inputPrices);
	
	public FloatVar getDividend();
	
	public FloatVar getOutput();
	
	public Good[] getInputGoods();

	public FloatVar getInput(Good inputType);

	public String getType();

	public Good getOutputGood();

}
