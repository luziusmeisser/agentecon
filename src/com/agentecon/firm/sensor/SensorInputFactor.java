// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm.sensor;

import com.agentecon.api.Price;
import com.agentecon.firm.InputFactor;
import com.agentecon.good.IStock;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPrice;

public class SensorInputFactor extends InputFactor {
	
	private Bid prevRealBid;
	private SensorAccuracy acc;

	public SensorInputFactor(IStock stock, IPrice price) {
		this(stock, price, new SensorAccuracy());
	}
	
	public SensorInputFactor(IStock stock, IPrice price, double accuracy) {
		this(stock, price, new SensorAccuracy(accuracy));
	}

	public SensorInputFactor(IStock stock, IPrice price, SensorAccuracy accuracy) {
		super(stock, price);
		this.acc = accuracy;
	}

	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealBid == null ? 0.0 : prevRealBid.getTransactionVolume());
	}

	@Override
	public double getQuantity() {
		return super.getQuantity() + (prevRealBid == null ? 0.0 : prevRealBid.getTransactionVolume() / prevRealBid.getPrice().getPrice());
	}
	
	@Override
	public void createOffers(IPriceMakerMarket market, IStock money, double moneySpentOnBid) {
		double sensorSize = acc.getOfferSize();
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
		if (prevRealBid != null) {
			if (prevRealBid.isUsed()) {
				acc.moreAccurate();
			} else {
				acc.lessAccurate();
			}
		}
	}

	private double getSafePrice() {
		return super.getPrice() * (1 + acc.getAccuracy());
	}

	public InputFactor duplicate(IStock stock) {
		return new SensorInputFactor(stock, price, acc);
	}

	@Override
	public double getPrice() {
		double sensor = super.getPrice();
		double most = getSafePrice();
		double accuracy = acc.getOfferSize();
		return accuracy * sensor + (1 - accuracy) * most;
	}

}
