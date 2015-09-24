package com.agentecon.finance;

import com.agentecon.api.Price;
import com.agentecon.firm.Factor;
import com.agentecon.good.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.price.IPrice;

public class FloorFactor extends Factor {

	public FloorFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	public void adapt(double max) {
		if (prevOffer != null) {
			price.adaptWithCeiling(shouldIncrease(), max);
		}
	}

	@Override
	protected AbstractOffer newOffer(IStock money, double p, double planned) {
		return new BidFin(money, (Position) stock, new Price(getGood(), p), planned);
	}

}
