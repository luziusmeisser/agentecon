package com.agentecon.finance;

import java.util.HashMap;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;

public class Portfolio {
	
	private IStock wallet;
	private HashMap<Ticker, Share> inv;
	
	public Portfolio(Good money){
		this.wallet = new Stock(money);
		this.inv = new HashMap<>();
	}
	
	public void balance(IStockMarket stocks, double cash) {
	}

	public void payTo(IStock target){
		payTo(target, wallet.getAmount());
	}
	
	public void payTo(IStock target, double amount){
		target.transfer(wallet, Math.min(amount, wallet.getAmount()));
	}
	
	public void absorb(IStock source) {
		absorb(source, source.getAmount());
	}
	
	public void absorb(IStock source, double amount) {
		wallet.transfer(source, Math.min(amount, source.getAmount()));
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

	public double getValue() {
		return wallet.getAmount();
	}

	public void collectDividends() {
		// TODO Auto-generated method stub
		
	}

}
