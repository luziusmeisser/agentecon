package com.agentecon.finance;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.BestPriceMarket;
import com.agentecon.market.Bid;
import com.agentecon.market.MarketListeners;
import com.agentecon.util.InstantiatingHashMap;

public class DailyStockMarket implements IStockMarket, IPriceProvider {

	private MarketMaker maker;
	private MarketListeners listeners;
	private Iterator<BestPriceMarket> any;
	private HashMap<Ticker, BestPriceMarket> market;

	public DailyStockMarket(MarketListeners listeners, MarketMaker maker) {
		this.maker = maker;
		this.listeners = listeners;
		this.market = new InstantiatingHashMap<Ticker, BestPriceMarket>() {

			@Override
			protected BestPriceMarket create(Ticker key) {
				return new BestPriceMarket(key);
			}
		};
	}

	public BestPriceMarket getAny() {
		try {
			if (any == null || !any.hasNext()) {
				any = market.values().iterator();
			}
			return any.hasNext() ? any.next() : null;
		} catch (ConcurrentModificationException e) {
			any = null;
			return getAny();
		}
	}

	@Override
	public void offer(Bid bid) {
		bid.setListener(listeners);
		this.market.get(bid.getGood()).offer(bid);
	}

	@Override
	public void offer(Ask ask) {
		ask.setListener(listeners);
		this.market.get(ask.getGood()).offer(ask);
	}

	@Override
	public Ticker findAnyAsk() {
		Ask ask = null;
		int count = 0;
		while (ask == null && count < market.size()) {
			ask = getAny().getAsk();
			count++;
		}
		return ask == null ? null : (Ticker) ask.getGood();
	}

	@Override
	public Position buy(Ticker ticker, Position existing, IStock wallet, double budget) {
		BestPriceMarket best = market.get(ticker);
		Ask ask = best.getAsk();
		if (ask != null) {
			return ((AskFin) ask).accept(wallet, existing, budget);
		} else {
			return existing;
		}
	}

	@Override
	public Ticker findHighestBid(Collection<Ticker> keySet) {
		BidFin highest = null;
		for (Ticker ticker : keySet) {
			BestPriceMarket bpm = market.get(ticker);
			BidFin bid = (BidFin) bpm.getBid();
			if (bid != null) {
				if (highest == null || bid.getPrice().getPrice() > highest.getPrice().getPrice()) {
					highest = bid;
				}
			}
		}
		return highest == null ? null : highest.getTicker();
	}

	@Override
	public double sell(Position pos, IStock wallet, double shares) {
		BestPriceMarket best = market.get(pos.getTicker());
		BidFin bid = (BidFin) best.getBid();
		if (bid != null) {
			return bid.accept(wallet, pos, shares);
		} else {
			return 0.0;
		}
	}

	@Override
	public double getPrice(Good output) {
		return maker.getPrice(output);
	}

}
