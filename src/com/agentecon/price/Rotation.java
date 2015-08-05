package com.agentecon.price;

import java.util.ArrayList;

public class Rotation {

	private int day;
	private ArrayList<IPrice> actualPrices;

	public Rotation() {
		this.day = 0;
		this.actualPrices = new ArrayList<>();
	}

	public RotatingPrice addPrice(IPrice price) {
		actualPrices.add(price);
		return new RotatingPrice(this, actualPrices.size() - 1);
	}

	public IPrice getPrice(int pos) {
		int index = day + pos;
		return actualPrices.get(index % actualPrices.size());
	}
	
	public void rotate(){
		this.day++;
	}

}
