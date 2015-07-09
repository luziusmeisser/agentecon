package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Iterator;

import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class ShareRegister {

	private Ticker ticker;
	private int totalShares;
	private Portfolio selfOwnedShares;
	private ArrayList<Portfolio> shareholders;

	public ShareRegister(String firmName, IStock wallet) {
		this.totalShares = 1000;
		this.ticker = new Ticker(firmName);
		this.selfOwnedShares = new Portfolio(wallet);
		this.selfOwnedShares.add(new Share(ticker, wallet.getGood(), totalShares));
		this.shareholders = new ArrayList<>();
		this.shareholders.add(selfOwnedShares);
	}

	public void notifyAddedTo(Portfolio shareHolder) {
		this.shareholders.add(shareHolder);
	}

	public void payDividend(IStock sourceWallet, double totalDividends) {
		Iterator<Portfolio> iter = shareholders.iterator();
		double presentShares = 0.0;
		while (iter.hasNext()) {
			Portfolio holder = iter.next();
			Share share = holder.getShares(ticker);
			if (share == null) {
				iter.remove();
			} else {
				double amount = share.getAmount();
				presentShares += amount;
				double holderDividend = amount * totalDividends / totalShares;
				holder.receiveDividend(sourceWallet, holderDividend);
			}
		}
		// redistribute shares of missing shareholders, should rarely happen
		if (Numbers.isSmaller(presentShares, totalShares)) {
			double missing = totalDividends - presentShares;
			selfOwnedShares.getShares(ticker).add(missing);
		}
	}
	
	public void trade(StockMarket market, double tragetValue){
		
	}

}
