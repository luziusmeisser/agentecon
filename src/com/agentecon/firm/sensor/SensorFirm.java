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
import com.agentecon.price.IPriceFactory;

public class SensorFirm extends Producer {

	public SensorFirm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		super(type, end, prod, prices);
	}

	public SensorFirm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices, IFirmDecisions strategy) {
		super(type, end, prod, prices, strategy);
	}

	@Override
	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		IPrice price = prices.createPrice(stock.getGood());
		return new SensorInputFactor(stock, price);
	}

	@Override
	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock stock) {
		IPrice price = prices.createPrice(stock.getGood());
		return new SensorOutputFactor(stock, price);
	}

	@Override
	public SensorFirm createNextGeneration(Endowment end, IProductionFunction prod) {
		return new SensorFirm(getType(), end, prod, prices);
	}

	@Override
	public SensorFirm clone() {
		SensorFirm klon = (SensorFirm) super.clone();
		return klon;
	}

}
