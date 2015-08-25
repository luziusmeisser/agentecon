// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm.sensor;

import com.agentecon.api.Price;
import com.agentecon.firm.OutputFactor;
import com.agentecon.good.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.AdaptablePrice;
import com.agentecon.price.IPrice;

public class SensorOutputFactor extends OutputFactor {

	private Ask prevRealAsk;
	private double accuracy;

	public SensorOutputFactor(IStock stock, IPrice price) {
		this(stock, price, 0.1);
	}

	public SensorOutputFactor(IStock stock, IPrice price, double accuracy) {
		super(stock, price);
		this.accuracy = accuracy;
	}

	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealAsk == null ? 0.0 : prevRealAsk.getTransactionVolume());
	}

	private double getSensorOfferSize() {
		return accuracy / 5;
	}

	@Override
	public void createOffer(IPriceMakerMarket market, IStock money, double amount) {
		double sensorSize = getSensorOfferSize() * amount;
		super.createOffer(market, money, sensorSize);
		prevRealAsk = new Ask(money, getStock(), new Price(getGood(), getSafePrice()), amount - sensorSize);
		market.offer(prevRealAsk);
	}

	@Override
	public void adaptPrice() {
		super.adaptPrice();
		if (prevRealAsk != null) {
			if (prevRealAsk.isUsed()) {
				accuracy = Math.max(0.05, accuracy / 1.005);
			} else {
				accuracy = Math.min(0.5, accuracy * 2);
			}
			prevRealAsk = null;
		}
	}

	private double getSafePrice() {
		return Math.max(AdaptablePrice.MIN, super.getPrice() / (1 + accuracy));
	}

	@Override
	public double getPrice() {
		double offerSize = getSensorOfferSize();
		double sensor = super.getPrice();
		double most = getSafePrice();
		return offerSize * sensor + (1 - offerSize) * most;
	}

	public OutputFactor duplicate(IStock stock) {
		// assert prevRealAsk == null;
		return new SensorOutputFactor(stock, price, accuracy);
	}

}
