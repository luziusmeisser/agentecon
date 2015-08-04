package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPrice;

public class OutputFactor extends Factor {

	private Ask prevAsk = null;

	public OutputFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	public void createOffer(IPriceMakerMarket market, IStock money, double amount) {
		prevAsk = new Ask(money, getStock(), new Price(getGood(), price.getPrice()), amount);
		market.offer(prevAsk);
	}

	public void adaptPrice() {
		if (prevAsk != null) {
			super.adaptPrice(prevAsk.isUsed());
		}
	}

	public double getVolume() {
		return prevAsk == null ? 0.0 : prevAsk.getTransactionVolume();
	}

}
