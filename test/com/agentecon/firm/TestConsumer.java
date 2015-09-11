// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.market.IOffer;
import com.agentecon.market.Market;
import com.agentecon.sim.config.SimConfig;

public class TestConsumer {

	private static final double INFINITE = 1000000; // :)

	private Price[] prices;

	public TestConsumer(Price... prices) {
		this.prices = prices;
	}

	public void buyAndSell(Market market) {
		for (Price value : prices) {
			IOffer ask = market.getAsk(value.getGood());
			if (ask != null && value.isAbove(ask.getPrice())) {
				ask.accept(getWallet(), getStock(ask.getGood()), INFINITE);
			}
			IOffer bid = market.getBid(value.getGood());
			if (bid != null && bid.getPrice().isAbove(value)) {
				bid.accept(getWallet(), getStock(bid.getGood()), INFINITE);
			}
		}
	}

	private Stock getStock(Good good) {
		return new Stock(good, INFINITE);
	}

	private Stock getWallet() {
		return new Stock(SimConfig.MONEY, INFINITE);
	}

	public boolean checkPrices(Market market, double accuracy) {
		boolean ok = true;
		for (Price p : prices) {
			Price mp = market.getPrice(p.getGood());
			ok &= mp.equals(p, accuracy);
		}
		return ok;
	}

	public double getPriceSquareError(Market market) {
		double tot = 0.0;
		for (Price p : prices) {
			Price mp = market.getPrice(p.getGood());
			if (mp == null) {
				return Double.POSITIVE_INFINITY;
			} else {
				double diff = mp.getPrice() - p.getPrice();
				tot += diff * diff;
			}
		}
		return tot;
	}

}
