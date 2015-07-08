// Created on May 24, 2015 by Luzius Meisser

package com.agentecon.market;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.agentecon.good.Good;

public class RandomChoiceMarket extends AbstractMarket {

	private Random rand;
	private ArrayList<Ask> asks;
	private ArrayList<Bid> bids;

	public RandomChoiceMarket(Random rand, Good good) {
		super(good);
		this.rand = rand;
		this.asks = new ArrayList<>();
		this.bids = new ArrayList<>();
	}

	@Override
	public IOffer getBid() {
		return getBest(bids);
	}

	@Override
	public IOffer getAsk() {
		return getBest(asks);
	}

	private IOffer getBest(ArrayList<? extends AbstractOffer> offers) {
		IOffer o1 = getRandom(offers);
		if (o1 == null) {
			return null;
		} else {
			IOffer o2 = getRandom(offers);
			IOffer o3 = getRandom(offers);
			return o1.getBetterOne(o2).getBetterOne(o3);
		}
	}

	private IOffer getRandom(ArrayList<? extends AbstractOffer> offers) {
		int size = offers.size();
		while (size > 0){
			int pos = rand.nextInt(size);
			IOffer offer = offers.get(pos);
			if (offer.isUsed()){
				offers.remove(pos);
				size--;
			} else{
				return offer;
			}
		}
		return null;
	}

	@Override
	protected Collection<Ask> getAsks() {
		return asks;
	}

	@Override
	protected Collection<Bid> getBids() {
		return bids;
	}

}
