// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.metric.IMarketListener;
import com.agentecon.stats.Numbers;

public abstract class AbstractOffer implements Comparable<AbstractOffer>, IOffer {
	
	private static final IMarketListener NULL_LISTENER = new IMarketListener() {

		@Override
		public void notifyOffered(Good good, double quantity, Price price) {
		}

		@Override
		public void notifySold(Good good, double quantity, Price price) {
		}
	};
	
	private double volume;
	private double quantity;
	protected IStock wallet;
	protected IStock stock;
	private Price price;
	protected IMarketListener listener = NULL_LISTENER;
	
	public AbstractOffer(IStock wallet, IStock stock, Price price, double quantity){
		this.wallet = wallet;
		this.stock = stock;
		this.price = price;
		this.volume = 0.0;
		this.quantity = quantity;
		assert price.getPrice() >= Numbers.EPSILON;
	}
	
	public double getTransactionVolume(){
		return volume;
	}
	
	public void setListener(IMarketListener listener){
		this.listener = listener == null ? NULL_LISTENER : listener;
		this.listener.notifyOffered(getGood(), getAmount(), getPrice());
	}
	
	public void transfer(IStock sourceWallet, double moneyFlow, IStock target, double goodsFlow){
		wallet.transfer(sourceWallet, moneyFlow);
		stock.transfer(target, goodsFlow);
		doStats(moneyFlow, goodsFlow);
	}

	protected void doStats(double moneyFlow, double goodsFlow) {
		volume += Math.abs(moneyFlow);
		double absQuant = Math.abs(goodsFlow);
		this.quantity -= absQuant;
		
		listener.notifySold(getGood(), absQuant, getPrice());
	}
	
	public double getAmount(){
		return quantity;
	}
	
	public Price getPrice(){
		return price;
	}
	
	public void setMarketListener(IMarketListener listener){
		this.listener = listener;
		this.listener.notifyOffered(getGood(), getAmount(), getPrice());
	}
	
	public Good getGood() {
		return stock.getGood();
	}
	
	public boolean isUsed(){
		return getAmount() == 0.0;
	}
	
	public abstract double accept(IStock source, IStock target, double amount);
	
	public int compareTo(AbstractOffer o) {
		return price.compareTo(o.price);
	}
	
	@Override
	public String toString(){
		return (isBid() ? "Buying " : "Selling ") + price.toString();
	}

}
