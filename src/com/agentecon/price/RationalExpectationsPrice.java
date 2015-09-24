package com.agentecon.price;

import java.util.ArrayList;
import java.util.Collections;

public class RationalExpectationsPrice implements IPrice, IEvolvable {
	
	private int pos;
	private ArrayList<IPrice> priceHistory;
	
	public RationalExpectationsPrice(IPrice initial){
		this(new ArrayList<>(Collections.singleton(initial)));
	}
	
	private RationalExpectationsPrice(ArrayList<IPrice> history){
		this.priceHistory = history;
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
			IPrice latest = priceHistory.get(priceHistory.size() - 1);
			priceHistory.add(latest.clone());
		}
		return priceHistory.get(pos);
	}
	
	@Override
	public IPrice clone() {
		throw new java.lang.RuntimeException(new CloneNotSupportedException());
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return getCurrent().isProbablyUnobtainable();
	}

	@Override
	public RationalExpectationsPrice createNextGeneration() {
		return new RationalExpectationsPrice(priceHistory);
	}

	@Override
	public void adaptWithCeiling(boolean increasePrice, double max) {
		getCurrent().adaptWithCeiling(increasePrice, max);
		pos++;
	}

	@Override
	public void adaptWithFloor(boolean increasePrice, double min) {
		getCurrent().adaptWithFloor(increasePrice, min);
		pos++;
	}

}