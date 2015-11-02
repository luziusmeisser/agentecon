// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm.sensor;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.InputFactor;
import com.agentecon.firm.OutputFactor;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.price.IPrice;

public class SensorFirm extends Producer {

	public SensorFirm(String type, Endowment end, IProductionFunction prod, IFirmDecisions strategy) {
		super(type, end, prod, strategy);
	}

	@Override
	protected InputFactor createInputFactor(IStock stock) {
		return new SensorInputFactor(stock);
	}

	@Override
	protected OutputFactor createOutputFactor(IStock stock) {
		return new SensorOutputFactor(stock);
	}

	@Override
	public SensorFirm clone() {
		SensorFirm klon = (SensorFirm) super.clone();
		return klon;
	}

}
