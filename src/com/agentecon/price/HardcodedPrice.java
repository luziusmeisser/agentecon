// Created on Jun 3, 2015 by Luzius Meisser

package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class HardcodedPrice implements IPrice {
	
	private double price;

	public HardcodedPrice(double price) {
		this.price = price;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public void adapt(boolean increasePrice) {
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return false;
	}
	
	@Override
	public HardcodedPrice clone(){
		return this;
	}
	
	@Override
	public String toString(){
		return Numbers.toString(price) + "$";
	}

	@Override
	public void adaptWithCeiling(boolean increasePrice, double max) {
	}

	@Override
	public void adaptWithFloor(boolean increasePrice, double min) {
	}

}
