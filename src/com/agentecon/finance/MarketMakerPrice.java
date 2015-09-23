package com.agentecon.finance;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.price.ExpSearchPrice;
import com.agentecon.price.IPrice;

public class MarketMakerPrice {

	public static final double SPREAD = 0.05;

	private IPrice price;
	private Ticker ticker;
	private BidFin lastBid;
	private AskFin lastAsk;

	public MarketMakerPrice(Ticker ticker) {
		this.ticker = ticker;
		this.price = new ExpSearchPrice(0.1, 10.0) {
			protected double getMinAdaptionFactor() {
				return SPREAD / 5;
			}
		};
	}

	public void trade(IStockMarket dsm, IStock wallet, Position pos, boolean needMore) {
		assert pos.getTicker().equals(ticker);
		if (lastBid != null && lastAsk != null) {
			double change = lastBid.getTransactionVolume() / lastBid.getPrice().getPrice() - lastAsk.getTransactionVolume() / lastAsk.getPrice().getPrice();
			if (change >= 0.0 && needMore){
				// we are ok
			} else if (change <= 0.0 && !needMore){
				// we are ok
			} else {
				price.adapt(needMore);
			}
		}
		double spreadFactor = (1.0 + SPREAD / 2);
		double shares = pos.getAmount();
		double upstep = needMore ? shares / 9 : shares / 10;
		double downstep = needMore ? shares / 10 : shares / 9;
		buy(dsm, wallet, pos, price.getPrice() / spreadFactor, upstep);
		sell(dsm, wallet, pos, price.getPrice() * spreadFactor, downstep);
	}

	private void buy(IStockMarket dsm, IStock wallet, Position pos, double price, double shares) {
		assert pos != null;
		lastBid = new BidFin(wallet, pos, new Price(pos.getTicker(), price), shares);
		dsm.offer(lastBid);
	}

	private void sell(IStockMarket dsm, IStock wallet, Position pos, double price, double shares) {
		lastAsk = new AskFin(wallet, pos, new Price(pos.getTicker(), price), shares);
		dsm.offer(lastAsk);
	}

	public double getPrice() {
		return price.getPrice();
	}

	public Ticker getTicker() {
		return ticker;
	}

	@Override
	public String toString() {
		return new Price(ticker, price.getPrice()).toString();
	}

}
