package com.agentecon.finance;

import java.util.HashMap;

import com.agentecon.good.IStock;

public class Portfolio {
	
	private HashMap<Ticker, Share> inv;
	private IStock wallet;
	
	public Portfolio(IStock wallet){
		this.wallet = wallet;
		this.inv = new HashMap<>();
	}
	
	public Share getShares(Ticker ticker){
		return inv.get(ticker);
	}
	
	public void dispose(){
		this.inv.clear();
	}

	public void receiveDividend(IStock sourceWallet, double holderDividend) {
		this.wallet.transfer(sourceWallet, holderDividend);
	}

	public void add(Share share) {
		Object prev = this.inv.put(share.getTicker(), share);
		assert prev == null;
	}

}
