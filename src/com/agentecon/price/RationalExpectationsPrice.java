package com.agentecon.price;

import java.util.ArrayList;

import com.agentecon.good.Good;
import com.agentecon.stats.Numbers;
import com.agentecon.util.Average;

public class RationalExpectationsPrice implements IPrice {
	
	private int pos;
	private Good good;
	private IPriceFactory factory;
	private ArrayList<IPrice> priceHistory;
	
	public RationalExpectationsPrice(IPriceFactory factory, Good good){
		this.priceHistory = new ArrayList<>();
		this.priceHistory.add(factory.createPrice(good));
		this.factory = factory;
		this.good = good;
		this.pos = 0;
	}

	public void reset(){
		this.pos = 0;
	}
	
	@Override
	public double getPrice() {
		return getCurrent().getPrice();
	}

	@Override
	public void adapt(boolean increasePrice) {
		getCurrent().adapt(increasePrice);
//		pos++;
	}

	private IPrice getCurrent() {
		while (pos >= priceHistory.size()){
			priceHistory.add(priceHistory.get(priceHistory.size() - 1).clone());
		}
		return priceHistory.get(pos);
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return getCurrent().isProbablyUnobtainable();
	}

	public double getAverage() {
		Average avg = new Average();
		for (IPrice p: priceHistory){
			avg.add(p.getPrice());
		}
		return avg.getAverage();
	}
	
	public IPrice clone(){
		throw new RuntimeException(new CloneNotSupportedException());
	}
	
	@Override
	public String toString() {
		return getCurrent().toString();
	}

}
