package com.agentecon.price;

import java.util.HashMap;
import java.util.Map;

import com.agentecon.good.Good;
import com.agentecon.util.InstantiatingHashMap;

public class RationalPriceFactory implements IPriceFactory {
	
	private HashMap<Good, RationalExpectationsPrice> prices;
	
	public RationalPriceFactory(final IPriceFactory wrapped) {
		this.prices = new InstantiatingHashMap<Good, RationalExpectationsPrice>() {

			@Override
			protected RationalExpectationsPrice create(Good good) {
				return (RationalExpectationsPrice) wrapped.createPrice(good);
			}
		};
	}
	
	private RationalPriceFactory(HashMap<Good, RationalExpectationsPrice> prices) {
		this.prices = new HashMap<>();
		for (Map.Entry<Good, RationalExpectationsPrice> e: prices.entrySet()){
			this.prices.put(e.getKey(), e.getValue().createNextGeneration());
		}
	}

	public RationalPriceFactory createNextGeneration(){
		return new RationalPriceFactory(prices);
	}

	@Override
	public IPrice createPrice(Good good) {
		return prices.get(good);
	}

	public double getAveragePrice(Good good) {
		return ((RationalExpectationsPrice)prices.get(good)).getAverage();
	}

}
