package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.good.IStock;

public interface IStockMarket extends IPriceProvider {

	public void offer(BidFin bid);
	
	public void offer(AskFin bid);
	
	public Ticker findAnyAsk();
	
	public Position buy(Ticker ticker, Position existing, IStock wallet, double budget);

	public Ticker findHighestBid(Collection<Ticker> keySet);

	public double sell(Position pos, IStock wallet, double maxAmount);

}
