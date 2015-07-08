// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.AdaptablePrice;
import com.agentecon.price.IPrice;

public class SensorOutputFactor extends OutputFactor {

	private Ask prevRealAsk;
	private double accuracy;

	public SensorOutputFactor(IStock stock, IPrice price) {
		super(stock, price);
		this.accuracy = 0.1;
	}
	
	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealAsk == null ? 0.0 : prevRealAsk.getTransactionVolume());
	}

	@Override
	public void createOffer(IPriceMakerMarket market, IStock money, double amount) {
		double sensorSize = accuracy * amount;
		super.createOffer(market, money, sensorSize);
		prevRealAsk = new Ask(money, getStock(), new Price(getGood(), getSafePrice()), amount - sensorSize);
		market.offer(prevRealAsk);
	}

	@Override
	public void adaptPrice() {
		super.adaptPrice();
		if (prevRealAsk.isUsed()) {
			accuracy /= 1.005;
		} else {
			accuracy = Math.min(0.5, accuracy * 2);
		}
	}

	private double getSafePrice() {
		return Math.max(AdaptablePrice.MIN, super.getPrice() / (1 + accuracy));
	}

	@Override
	public double getPrice() {
		double sensor = super.getPrice();
		double most = getSafePrice();
		return accuracy * sensor + (1 - accuracy) * most;
	}

}
