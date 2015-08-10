package com.agentecon.firm.production;

import com.agentecon.good.Good;

public interface IPriceProvider {

	public double getPrice(Good output);

}
