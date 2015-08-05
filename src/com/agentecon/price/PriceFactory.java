// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.price;

import java.util.HashMap;
import java.util.Random;

import com.agentecon.good.Good;

/**
 * Creates prices according to settings.
 * A separate instance is required for each firm.
 * PriceFactory must be carried over through generations for evolving firms and "createPrice" only called
 * once per generation for each good.
 */
public class PriceFactory implements IPriceFactory {

	public static Good NORMALIZED_GOOD = null;

	public static final String CONSTANT = "CONSTANT";
	public static final String CONSTANTFACTOR = "CONSTANTFACTOR";
	public static final String RANDOMIZED = "RANDOMIZED";
	public static final String EXPSEARCH = "EXPSEARCH";
	public static final String SENSOR = "SENSOR";
	public static final String HISTORICHINT = "HISTORICHINT";
	public static final String RATIONAL = "RATIONAL";

	private String type;
	private Random rand;
	private double factor;

	private HashMap<Good, IEvolvable> evolvablePrices;

	public PriceFactory(Random rand, String... params) {
		this.type = params[0];
		this.rand = rand;
		this.factor = params.length == 1 ? 1.0 : Double.parseDouble(params[1]);
		this.evolvablePrices = new HashMap<>();
	}

	public IPrice createPrice(Good good) {
		if (NORMALIZED_GOOD != null && good.equals(NORMALIZED_GOOD)) {
			return new HardcodedPrice(10.0);
		} else if (evolvablePrices.containsKey(good)){
			IEvolvable ev = evolvablePrices.get(good).createNextGeneration();
			evolvablePrices.put(good, ev);
			return (IPrice) ev;
		} else {
			IPrice price = createPrice();
			if (price instanceof IEvolvable){
				evolvablePrices.put(good, (IEvolvable) price);
			}
			return price;
		}
	}

	private IPrice createPrice() {
		switch (type) {
		default:
		case CONSTANT:
			return new HardcodedPrice(factor);
		case CONSTANTFACTOR:
			return new ConstantFactorPrice(factor);
		case RANDOMIZED:
			return new RandomizedFactorPrice(rand, factor);
		case SENSOR:
		case EXPSEARCH:
			return new ExpSearchPrice(factor);
		case RATIONAL:
			return new RationalExpectationsPrice(new ExpSearchPrice(factor));
		case HISTORICHINT:
			return new HistoricHintPrice(factor);
		}
	}

}
