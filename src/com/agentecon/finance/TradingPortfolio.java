package com.agentecon.finance;

import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class TradingPortfolio extends Portfolio {

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
				addPosition(stocks.buy(any, pos, wallet, wallet.getAmount() - cashTarget));
			}
		}
		while (Numbers.isSmaller(wallet.getAmount(), cashTarget)) {
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

	public double getCombinedValue(IPriceProvider prices, int timeHorizon) {
		return getSubstanceValue(prices) + getEarningsValue(timeHorizon);
	}

	public double getEarningsValue(int timeHorizon) {
		return getLatestDividendIncome() * timeHorizon;
	}

	public double getSubstanceValue(IPriceProvider prices) {
		double value = wallet.getAmount();
		for (Position p : inv.values()) {
			value += p.getAmount() * prices.getPrice(p.getTicker());
		}
		return value;
	}

	private double countShares() {
		double shares = 0.0;
		for (Position p : inv.values()) {
			shares += p.getAmount();
		}
		return shares;
	}

	public void sell(IStockMarket stocks, double fraction) {
		double sharesToSell = countShares() * fraction;
		while (Numbers.isBigger(sharesToSell, 0.0)) {
			Ticker ticker = stocks.findHighestBid(inv.keySet());
			if (ticker == null) {
				break;
			} else {
				Position pos = inv.get(ticker);
				sharesToSell -= stocks.sell(pos, wallet, sharesToSell);
				if (pos.isEmpty()) {
					inv.remove(ticker);
					pos.dispose();
				}
			}
		}
	}

	public void invest(IStockMarket stocks, double budget) {
		if (Numbers.isBigger(budget, 0.0)) {
			assert wallet.getAmount() >= budget;
			Ticker any = stocks.findAnyAsk();
			if (any != null) {
				double before = wallet.getAmount();
				Position pos = getShares(any);
				addPosition(stocks.buy(any, pos, wallet, budget));
				double spent = before - wallet.getAmount();
				invest(stocks, budget - spent);
			}
		}
	}

}
