package com.agentecon.finance;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.Ask;

public class AskFin extends Ask {

	public AskFin(IStock wallet, Position stock, Price price, double amount) {
		super(wallet, stock, price, amount);
	}
	
	protected Position getStock(){
		return (Position)stock;
	}
	
	public Position accept(IStock payer, Position target, double budget) {
		if (target == null){
			target = getStock().split(0.0);
		}
		super.accept(payer, target, budget / getPrice().getPrice());
		return target;
	}

	public Ticker getTicker() {
		return (Ticker) getGood();
	}

}
