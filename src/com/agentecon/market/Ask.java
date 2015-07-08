// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;

public class Ask extends AbstractOffer {

	public Ask(IStock wallet, IStock stock, Price price, double amount) {
		super(wallet, stock, price, amount);
	}

	@Override
	public double getAmount() {
		return Math.min(stock.getAmount(), super.getAmount());
	}
	
	@Override
	public double accept(IStock payer, IStock target, double targetAmount) {
		double amount = Math.min(getAmount(), targetAmount);
		double price = getPrice().getPrice();
		double total = amount * price;
		if (total > payer.getAmount()) {
			amount = payer.getAmount() / price;
			total = payer.getAmount();
		}
		assert amount >= 0;
		transfer(payer, total, target, -amount);
		return amount;
	}

	@Override
	public boolean isBid() {
		return false;
	}

	@Override
	public IOffer getBetterOne(IOffer other) {
		return getPrice().isAbove(other.getPrice()) ? other : this;
	}

}
