package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Ask;
import com.agentecon.price.IPrice;

public class OutputFactor extends Factor {

	public OutputFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	protected AbstractOffer newOffer(IStock money, double p, double amount) {
		return new Ask(money, getStock(), new Price(getGood(), p), amount);
	}

	public OutputFactor duplicate(IStock stock) {
		return new OutputFactor(stock, price);
	}

}
