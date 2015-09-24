package com.agentecon.finance;

import com.agentecon.api.Price;
import com.agentecon.firm.Factor;
import com.agentecon.good.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.price.IPrice;

public class CeilingFactor extends Factor {

	public CeilingFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	public void adapt(double min) {
		if (prevOffer != null) {
			price.adaptWithFloor(shouldIncrease(), min);
		}
	}

	@Override
	protected AbstractOffer newOffer(IStock money, double price, double amount) {
		return new AskFin(money, (Position) stock, new Price(getGood(), price), amount);
	}

}
