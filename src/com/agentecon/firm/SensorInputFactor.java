// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPrice;

public class SensorInputFactor extends InputFactor {

	private Bid prevRealBid;
	private double accuracy;

	public SensorInputFactor(IStock stock, IPrice price) {
		super(stock, price);
		this.accuracy = 0.1;
	}
	
	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealBid == null ? 0.0 : prevRealBid.getTransactionVolume());
	}

	@Override
	public void createOffers(IPriceMakerMarket market, IStock money, double moneySpentOnBid) {
		double sensorSize = accuracy;
		double sensorAmount = sensorSize * moneySpentOnBid;
		super.createOffers(market, money, sensorAmount);
		double left = moneySpentOnBid - sensorAmount;
		double safePrice = getSafePrice();
		double planned = left / safePrice;
		prevRealBid = new Bid(money, getStock(), new Price(getGood(), safePrice), planned);
		market.offer(prevRealBid);
	}

	@Override
	public void adaptPrice() {
		super.adaptPrice();
		if (prevRealBid.isUsed()) {
			accuracy /= 1.005;
		} else {
			accuracy = Math.min(0.5, accuracy * 2);
		}
	}
	
	private double getSafePrice(){
		return super.getPrice() * (1 + accuracy);
	}

	@Override
	public double getPrice() {
		double sensor = super.getPrice();
		double most = getSafePrice();
		return accuracy * sensor + (1 - accuracy) * most;
	}

}
