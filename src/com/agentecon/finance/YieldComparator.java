package com.agentecon.finance;

import java.util.Comparator;

import com.agentecon.market.AbstractOffer;

public class YieldComparator implements Comparator<IPublicCompany> {

	private boolean buying;
	private IStockMarket dsm;

	public YieldComparator(IStockMarket dsm, boolean buying) {
		this.dsm = dsm;
		this.buying = buying;
	}

	@Override
	public int compare(IPublicCompany o1, IPublicCompany o2) {
		double yield1 = getYield(o1);
		double yield2 = getYield(o2);
		return buying ? Double.compare(yield2, yield1) : Double.compare(yield1, yield2);
	}

	public double getYield(IPublicCompany o1) {
		double dividend = o1.getShareRegister().getLatestDividends();
		double price = getPrice(o1.getTicker());
		return dividend / price;
	}

	public double getPrice(Ticker ticker) {
		AbstractOffer offer = buying ? dsm.getAsk(ticker) : dsm.getBid(ticker);
		return offer.getPrice().getPrice();
	}

}
