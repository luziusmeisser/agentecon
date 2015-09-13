package com.agentecon.firm;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPrice;
import com.agentecon.util.MovingAverage;

public abstract class Factor {
	
	private IStock stock;
	protected IPrice price;
	private AbstractOffer prevOffer;
	private MovingAverage successRate;

	public Factor(IStock stock, IPrice price) {
		assert stock != null;
		assert price != null;
		this.stock = stock;
		this.price = price;
		this.successRate = new MovingAverage(0.5);
	}
	
	public void adaptPrice() {
		if (prevOffer != null) {
			boolean success = prevOffer.isUsed();
			price.adapt(prevOffer.isBid() ? !success : success);
			successRate.add(getCurrentSuccessRate());
			prevOffer = null;
		}
	}
	
	public void createOffers(IPriceMakerMarket market, IStock money, double amount) {
		prevOffer = newOffer(money, price.getPrice(), amount);
		if (prevOffer.isBid()){
			market.offer((Bid)prevOffer);
		} else {
			
			market.offer((Ask)prevOffer);
		}
	}

	protected abstract AbstractOffer newOffer(IStock money, double p, double planned);
	
	public double getVolume() {
		return prevOffer == null ? 0.0 : prevOffer.getTransactionVolume();
	}
	
	public double getQuantity() {
		return prevOffer == null ? 0.0 : prevOffer.getTransactionVolume() / prevOffer.getPrice().getPrice();
	}

	public boolean isObtainable() {
		return !price.isProbablyUnobtainable();
	}
	
	protected double getCurrentSuccessRate() {
		return prevOffer.isUsed() ? 1.0 : 0.0;
	}
	
	public double getSuccessRateAverage(){
		return successRate.getAverage();
	}
	
	public final Good getGood() {
		return stock.getGood();
	}

	public final IStock getStock() {
		return stock;
	}
	
	public double getPrice() {
		return price.getPrice();
	}
	
	public String toString(){
		return stock + " at " + price;
	}

}
