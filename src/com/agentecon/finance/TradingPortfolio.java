package com.agentecon.finance;

import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class TradingPortfolio extends Portfolio {

	public TradingPortfolio(Good money) {
		super(money);
	}
	
	public TradingPortfolio(IStock money) {
		super(money);
	}
	
	public void balance(IStockMarket stocks, double cashTarget) {
		while (Numbers.isBigger(wallet.getAmount(), cashTarget)) {
			Ticker any = stocks.findAnyAsk();
			if (any == null) {
				break;
			} else {
				Position pos = inv.get(any);
				Position pos2 = stocks.buy(any, pos, wallet, wallet.getAmount() - cashTarget);
				if (pos == null && pos2 != null) {
					add(pos2);
				} else {
					assert pos == pos2;
				}
			}
		}
		while (wallet.getAmount() < cashTarget) {
			Ticker ticker = stocks.findHighestBid(inv.keySet());
			if (ticker != null) {
				Position pos = inv.get(ticker);
				stocks.sell(pos, wallet, cashTarget - wallet.getAmount());
				if (pos.isEmpty()) {
					pos.dispose();
					inv.remove(ticker);
				}
			} else {
				break;
			}
		}
	}
	
	public double getCombinedValue(IPriceProvider prices, int timeHorizon){
		return getSubstanceValue(prices) + getEarningsValue(timeHorizon);
	}
	
	public double getEarningsValue(int timeHorizon) {
		return getLatestDividendIncome() * timeHorizon;
	}
	
	public double getSubstanceValue(IPriceProvider prices) {
		double value = wallet.getAmount();
		for (Position p: inv.values()){
			value += p.getAmount() * prices.getPrice(p.getTicker());
		}
		return value;
	}

}
