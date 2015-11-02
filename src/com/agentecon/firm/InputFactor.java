package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPrice;

public class InputFactor extends Factor {
	
	public InputFactor(IStock stock) {
		super(stock);
	}

	public InputFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	protected AbstractOffer newOffer(IStock money, double p, double planned) {
		return new Bid(money, getStock(), new Price(getGood(), p), planned);
	}
	
	@Override
	public void createOffers(IPriceMakerMarket market, IStock money, double budget) {
		super.createOffers(market, money, budget / price.getPrice());  // NOT getPrice() as overridden in subclass
	}
	
	public InputFactor duplicate(IStock stock){
		return new InputFactor(stock, price);
	}

}
