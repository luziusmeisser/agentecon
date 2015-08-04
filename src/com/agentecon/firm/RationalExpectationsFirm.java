// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.price.IPriceFactory;

public class RationalExpectationsFirm extends Firm {
	
	private IPriceFactory fac;

//	public RationalExpectationsFirm(String type, Endowment end, IProductionFunction prod, final IPriceFactory prices) {
//		this(type, end, prod, new RationalPriceFactory(prices));
//	}
	
	public RationalExpectationsFirm(String type, Endowment end, IProductionFunction prod, IPriceFactory fac) {
		super(type, end, prod, fac);
		this.fac = fac;
	}

	public RationalExpectationsFirm createNextGeneration(Endowment end){
//		fac.reset();
		return new RationalExpectationsFirm(getType(), end, getProductionFunction(), fac);
	}

	public double getOutputPrice() {
		return output.getPrice(); // fac.getAveragePrice(output.getGood());
	}

}
