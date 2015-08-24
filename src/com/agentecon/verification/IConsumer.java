package com.agentecon.verification;

import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public interface IConsumer {

	public void imposeConstraints(FloatVar dividend, Good input, FloatVar wage, Good[] outputs, FloatVar... goodsPrices);

	public FloatVar getConsumption(Good good);

	public FloatVar getWorkHours();

	public String getType();

	public Good getWorkType();

	public Good[] getInputs();

}