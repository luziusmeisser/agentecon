// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.price.IPriceFactory;

public class RationalExpectationsFirm extends Firm {
	
	private RationalPriceFactory fac;

	public RationalExpectationsFirm(String type, Endowment end, IProductionFunction prod, final IPriceFactory prices) {
		this(type, end, prod, new RationalPriceFactory(prices));
	}
	
	public RationalExpectationsFirm(String type, Endowment end, IProductionFunction prod, RationalPriceFactory fac) {
		super(type, end, prod, fac);
		this.fac = fac;
	}

	public RationalExpectationsFirm createNextGeneration(Endowment end){
		return new RationalExpectationsFirm(getType(), end, getProductionFunction(), fac);
	}

}
