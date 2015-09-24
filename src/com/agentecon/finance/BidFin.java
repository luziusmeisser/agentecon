package com.agentecon.finance;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.Bid;

public class BidFin extends Bid {

	public BidFin(IStock wallet, Position stock, Price price, double amount) {
		super(wallet, stock, price, amount);
	}
	
	protected Position getStock(){
		return (Position)stock;
	}
	
	public double accept(IStock seller, Position target, double shares) {
		return super.accept(seller, target, Math.min(shares, target.getAmount()));
	}

	public Ticker getTicker() {
		return (Ticker) getGood();
	}

}
