package com.agentecon.price;

import java.util.ArrayList;

import com.agentecon.good.Good;

public class RationalExpectationsPrice implements IPrice {
	
	private int pos;
	private Good good;
	private IPriceFactory factory;
	private ArrayList<IPrice> priceHistory;
	
	public RationalExpectationsPrice(IPriceFactory factory, Good good){
		this.priceHistory = new ArrayList<>();
		this.factory = factory;
		this.good = good;
		this.pos = 0;
	}

	@Override
	public double getPrice() {
		return getCurrent().getPrice();
	}

	@Override
	public void adapt(boolean increasePrice) {
		getCurrent().adapt(increasePrice);
		pos++;
	}

	private IPrice getCurrent() {
		while (pos >= priceHistory.size()){
			priceHistory.add(factory.createPrice(good));
		}
		return priceHistory.get(pos);
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return getCurrent().isProbablyUnobtainable();
	}

}
