// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import java.util.Collection;
import java.util.PriorityQueue;

import com.agentecon.good.Good;

public class BestPriceMarket extends AbstractMarket {

	private static final boolean REQUEUE_TO_END = true;

	private PriorityQueue<Bid> bids;
	private PriorityQueue<Ask> asks;

	public BestPriceMarket(Good good) {
		super(good);
		this.bids = new PriorityQueue<Bid>();
		this.asks = new PriorityQueue<Ask>();
	}

	public boolean hasOffers(PriorityQueue<? extends AbstractOffer> bids) {
		return getBest(bids) != null;
	}

	@Override
	protected Collection<Ask> getAsks() {
		return asks;
	}

	@Override
	protected Collection<Bid> getBids() {
		return bids;
	}

	@Override
	public Bid getBid() {
		return getBest(bids);
	}

	@Override
	public Ask getAsk() {
		return getBest(asks);
	}

	private <T extends AbstractOffer> T getBest(PriorityQueue<T> bids) {
		if (REQUEUE_TO_END) {
			T offer = bids.poll();
			while (offer != null && offer.isUsed()) {
				offer = bids.poll();
			}
			if (offer != null) {
				bids.add(offer);
			}
			return offer;
		} else {
			T offer = bids.peek();
			while (offer != null && offer.isUsed()) {
				bids.poll();
				offer = bids.peek();
			}
			return offer;
		}
	}
}
