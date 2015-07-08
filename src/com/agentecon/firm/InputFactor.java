package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPrice;

public class InputFactor extends Factor {

	private Bid prevBid;

	public InputFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	public void adaptPrice() {
		super.adaptPrice(!prevBid.isUsed());
	}

	public void createOffers(IPriceMakerMarket market, IStock money, double amount) {
		double p = price.getPrice(); // NOT getPrice() as overridden in subclass
		double planned = amount / p;
		prevBid = new Bid(money, getStock(), new Price(getGood(), p), planned);
		market.offer(prevBid);
	}
	
	public double getVolume() {
		return prevBid == null ? 0.0 : prevBid.getTransactionVolume();
	}

	public boolean isObtainable() {
		return !price.isProbablyUnobtainable();
	}

}
