// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.good.IStock;
import com.agentecon.price.IPriceFactory;

public class SensorFirm extends Firm {

	public SensorFirm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		super(type, end, prod, prices);
	}
	
	@Override
	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		return new SensorInputFactor(stock, prices.createPrice(stock.getGood()));
	}
	
	@Override
	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock stock) {
		return new SensorOutputFactor(stock, prices.createPrice(stock.getGood()));
	}

}
