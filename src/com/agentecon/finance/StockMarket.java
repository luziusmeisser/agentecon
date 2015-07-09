package com.agentecon.finance;

import java.util.HashMap;

import com.agentecon.market.BestPriceMarket;

public class StockMarket {
	
	private HashMap<Ticker, BestPriceMarket> stocks;
	
	public StockMarket(){
		this.stocks = new HashMap<>();
	}

}
