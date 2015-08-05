// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.good.IStock;
import com.agentecon.price.HardcodedPrice;
import com.agentecon.price.IPrice;
import com.agentecon.price.IPriceFactory;

public class SensorFirm extends Firm {

	public SensorFirm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		super(type, end, prod, prices);
	}

	@Override
	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		IPrice price = prices.createPrice(stock.getGood());
		if (price instanceof HardcodedPrice) {
			return new InputFactor(stock, price);
		} else {
			return new SensorInputFactor(stock, price);
		}
	}

	@Override
	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock stock) {
		IPrice price = prices.createPrice(stock.getGood());
		if (price instanceof HardcodedPrice) {
			return new OutputFactor(stock, price);
		} else {
			return new SensorOutputFactor(stock, price);
		}
	}

}
