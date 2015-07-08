// Created by Luzius on May 10, 2014

package com.agentecon.price;

import com.agentecon.stats.Numbers;

public abstract class AdaptablePrice implements IPrice {

	public static final double MIN = 0.000001;
	public static final double MAX = 1000000;

	private double price;

	public AdaptablePrice() {
		this(10.0);
	}

	public AdaptablePrice(double initial) {
		this.price = initial;
	}

	public final void adapt(boolean increase) {
		double ftemp = getFactor(increase);
		double factor = (1.0 + ftemp);
		if (increase) {
			price = Math.min(MAX, price * factor);
		} else {
			price = Math.max(MIN, price / factor);
		}
	}

	protected abstract double getFactor(boolean increase);

	public double getPrice() {
		return price;
	}
	
	public boolean isProbablyUnobtainable(){
		return price >= MAX;
	}
	
	@Override
	public String toString() {
		return Numbers.toString(price) + "$";
	}

}
