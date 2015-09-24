package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.good.IStock;
import com.agentecon.market.IPriceMakerMarket;

public interface IStockMarket extends IPriceProvider, IPriceMakerMarket {

	public Ticker findAnyAsk();
	
	public Position buy(Ticker ticker, Position existing, IStock wallet, double budget);

	public Ticker findHighestBid(Collection<Ticker> keySet);

	public double sell(Position pos, IStock wallet, double maxAmount);

}
