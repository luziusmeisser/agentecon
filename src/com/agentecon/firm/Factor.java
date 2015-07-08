package com.agentecon.firm;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.price.IPrice;

public class Factor {
	
	private IStock stock;
	protected IPrice price;

	public Factor(IStock stock, IPrice price) {
		assert stock != null;
		this.stock = stock;
		this.price = price;
	}

	public final Good getGood() {
		return stock.getGood();
	}

	public final IStock getStock() {
		return stock;
	}

	public double getPrice() {
		return price.getPrice();
	}
	
	public String toString(){
		return stock + " at " + price;
	}

	protected void adaptPrice(boolean upwards) {
		price.adapt(upwards);
	}
	
}
