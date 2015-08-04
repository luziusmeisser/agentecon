package com.agentecon.firm;

import java.util.HashMap;

import com.agentecon.good.Good;
import com.agentecon.price.IPrice;
import com.agentecon.price.IPriceFactory;
import com.agentecon.price.RationalExpectationsPrice;
import com.agentecon.util.InstantiatingHashMap;

public class RationalPriceFactory implements IPriceFactory {
	
	private HashMap<Good, RationalExpectationsPrice> prices;
	
	public RationalPriceFactory(final IPriceFactory wrapped) {
		this.prices = new InstantiatingHashMap<Good, RationalExpectationsPrice>() {

			@Override
			protected RationalExpectationsPrice create(Good key) {
				return new RationalExpectationsPrice(wrapped, key);
			}
		};
	}

	@Override
	public IPrice createPrice(Good good) {
		return prices.get(good);
	}

	public void reset() {
		for (RationalExpectationsPrice p: prices.values()){
			p.reset();
		}
	}

	public double getAveragePrice(Good good) {
		return prices.get(good).getAverage();
	}

}
