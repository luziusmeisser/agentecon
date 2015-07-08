// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class Bid extends AbstractOffer {
	
	public Bid(IStock wallet, IStock stock, Price price, double quantity){
		super(wallet, stock, price, quantity);
		assert wallet.getAmount() - quantity * price.getPrice() >= -Numbers.EPSILON;
		assert quantity > 0;
	}
	
	@Override
	public double accept(IStock seller, IStock sellerStock, double targetAmount){
		assert sellerStock.getAmount() >= targetAmount;
		double amount = Math.min(targetAmount, getAmount());
		assert amount >= 0;
		double total = amount * getPrice().getPrice();
		transfer(seller, -total, sellerStock, amount);
		return amount;
	}
	
	@Override
	public int compareTo(AbstractOffer o) {
		return -super.compareTo(o);
	}

	@Override
	public boolean isBid() {
		return true;
	}
	
	@Override
	public IOffer getBetterOne(IOffer other) {
		return getPrice().isAbove(other.getPrice()) ? this : other;
	}

}
