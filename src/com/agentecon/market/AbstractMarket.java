// Created on May 24, 2015 by Luzius Meisser

package com.agentecon.market;

import java.util.Collection;

import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.stats.Numbers;

public abstract class AbstractMarket {
	
	private Good good;

	public AbstractMarket(Good good) {
		this.good = good;
	}

	public Good getGood(){
		return good;
	}

	public abstract IOffer getBid();

	public abstract IOffer getAsk();
	
	public void offer(Bid offer) {
		insert(getBids(), offer);
	}

	public void offer(Ask offer) {
		insert(getAsks(), offer);
	}
	
	private <T extends AbstractOffer> void insert(Collection<T> offers, T offer){
		assert offer.getGood().equals(good);
		if (!offer.isUsed()){
			offers.add(offer);
		}
	}

	public Price getPrice() {
		IOffer bid = getBid();
		IOffer ask = getAsk();
		if (bid == null && ask == null){
			return null;
		} else if (bid == null){
			return ask.getPrice();
		} else if (ask == null){
			return bid.getPrice();
		} else {
			return new Price(bid.getGood(), (bid.getPrice().getPrice() + ask.getPrice().getPrice()) / 2);
		}
	}
	
	private String getStats(Collection<? extends AbstractOffer> offers, boolean ask) {
		double amount = 0.0;
		double extremum = 0.0;
		for (AbstractOffer a: offers){
			amount += a.getAmount();
			extremum = ask ? Math.min(extremum, a.getAmount()) : Math.max(extremum, a.getAmount());
		}
		return Numbers.toString(amount) + " " + new Price(good, extremum) + (ask ? " offered" : " sought") + " in " + offers.size() + " offers";
	}
	
	protected abstract Collection<Ask> getAsks();
	
	protected abstract Collection<Bid> getBids();
	
	@Override
	public String toString() {
		// Not thread-safe! Can pose problems in debugging or multi-threaded logging
		boolean hasbids = getBid() != null;
		boolean hasasks = getAsk() != null;
		if (hasbids && hasasks){
			return getGood() + " for " + getStats(getAsks(), true) + " to " + getStats(getBids(), false);
		} else if (hasbids){
			return getStats(getBids(), false);
		} else if (hasasks){
			return getStats(getAsks(), true);
		} else {
			return "No " + getGood() + " left in the market";
		}
	}

}