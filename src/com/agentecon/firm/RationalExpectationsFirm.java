// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.good.IStock;
import com.agentecon.price.IPriceFactory;
import com.agentecon.price.PriceFactory;
import com.agentecon.price.RationalPriceFactory;

public class RationalExpectationsFirm extends Firm {

	private IPriceFactory prices;
	private RationalPriceFactory fac;

	public RationalExpectationsFirm(String type, Endowment end, IProductionFunction prod, final IPriceFactory prices) {
		this(type, end, prod, new RationalPriceFactory(prices));
		this.prices = prices;
	}

	public RationalExpectationsFirm(String type, Endowment end, IProductionFunction prod, RationalPriceFactory fac) {
		super(type, end, prod, fac);
		this.fac = fac;
	}

	public RationalExpectationsFirm createNextGeneration(Endowment end, LogProdFun prodFun) {
		return new RationalExpectationsFirm(getType(), end, prodFun, fac.createNextGeneration());
	}

	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock outStock) {
		return new SensorOutputFactor(outStock, prices.createPrice(outStock.getGood()));
	}

	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		return new SensorInputFactor(stock, prices.createPrice(stock.getGood()));
	}

	public double getOutputPrice() {
		return output.getPrice(); // fac.getAveragePrice(output.getGood());
	}

}
