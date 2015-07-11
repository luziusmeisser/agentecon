// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.stats.IDataRecorder;
import com.agentecon.util.InstantiatingHashMap;

public class Market implements IPriceMakerMarket, IPriceTakerMarket, IMarket {

	private MarketListeners listeners;
	private HashMap<Good, AbstractMarket> markets;

	public Market(Random rand) {
		this.markets = new InstantiatingHashMap<Good, AbstractMarket>(){

			@Override
			protected AbstractMarket create(Good good) {
				return new BestPriceMarket(good); // new RandomChoiceMarket(rand, good)
			}
			
		};
		this.listeners = new MarketListeners();
	}

	@Override
	public void addMarketListener(IMarketListener listener) {
		this.listeners.add(listener);
	}

	private AbstractMarket get(Good good) {
		AbstractMarket market = markets.get(good);
		return market;
	}

	public void offer(Bid offer) {
		offer.setListener(listeners);
		get(offer.getGood()).offer(offer);
	}

	public void offer(Ask offer) {
		offer.setListener(listeners);
		get(offer.getGood()).offer(offer);
	}

	public IOffer getBid(Good good) {
		return get(good).getBid();
	}

	public IOffer getAsk(Good good) {
		return get(good).getAsk();
	}

	public Price getPrice(Good good) {
		return get(good).getPrice();
	}

	@Override
	public IOffer getOffer(Good good, boolean bid) {
		if (bid) {
			return getBid(good);
		} else {
			return getAsk(good);
		}
	}

	public void reportStats(IDataRecorder rec) {
		for (AbstractMarket market : markets.values()) {
			market.reportStats(rec);
		}
	}

	@Override
	public String toString() {
		String s = null;
		for (AbstractMarket sub : markets.values()) {
			if (sub.getAsk() != null || sub.getBid() != null) {
				if (s == null) {
					s = sub.toString();
				} else {
					s += ", " + sub.toString();
				}
			}
		}
		if (s == null) {
			return "Nothing on the market";
		} else {
			return s;
		}
	}

	@Override
	public Collection<IOffer> getBids() {
		return getOffers(new IPriceFilter() {

			@Override
			public boolean isAskPricePreferred(Good good) {
				return false;
			}

			@Override
			public boolean isOfInterest(Good good) {
				return true;
			}
		});
	}

	@Override
	public Collection<IOffer> getAsks() {
		return getOffers(new IPriceFilter() {

			@Override
			public boolean isAskPricePreferred(Good good) {
				return true;
			}

			@Override
			public boolean isOfInterest(Good good) {
				return true;
			}
		});
	}

	@Override
	public Collection<IOffer> getOffers(IPriceFilter bidAskFilter) {
		ArrayList<IOffer> offers = new ArrayList<IOffer>();
		for (AbstractMarket sub : markets.values()) {
			if (bidAskFilter.isOfInterest(sub.getGood())) {
				boolean ask = bidAskFilter.isAskPricePreferred(sub.getGood());
				IOffer offer = ask ? sub.getAsk() : sub.getBid();
				if (offer != null) {
					offers.add(offer);
				}
			}
		}
		return offers;
	}

}
