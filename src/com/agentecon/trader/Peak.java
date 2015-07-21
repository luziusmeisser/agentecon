package com.agentecon.trader;

public class Peak {
	
	private int day;
	private double price;

	public Peak(int day, double price) {
		this.day = day;
		this.price = price;
	}

	public boolean isAbove(Peak other) {
		return price > other.price;
	}

	public boolean isPast(int day) {
		return this.day < day;
	}

	public double getPrice() {
		return price;
	}

	public boolean isBelow(Peak next) {
		return price < next.price;
	}

}
