// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.price;

import java.util.Random;

import com.agentecon.firm.RandomizedFactorPrice;
import com.agentecon.good.Good;

public class PriceFactory implements IPriceFactory {

	public static final String CONSTANTFACTOR = "CONSTANTFACTOR";
	public static final String RANDOMIZED = "RANDOMIZED";
	public static final String EXPSEARCH = "EXPSEARCH";

	public static final String[] TYPES = new String[] { CONSTANTFACTOR, RANDOMIZED, EXPSEARCH };

	private String type;
	private Random rand;
	private double factor;

	public PriceFactory(Random rand, String... params) {
		this.type = params[0];
		this.rand = rand;
		this.factor = params.length == 1 ? 1.0 : Double.parseDouble(params[1]);
	}

	public IPrice createPrice(Good good) {
		switch (type) {
		default:
		case CONSTANTFACTOR:
			return new ConstantFactorPrice(factor);
		case RANDOMIZED:
			return new RandomizedFactorPrice(rand, factor);
		case EXPSEARCH:
			return new ExpSearchPrice(factor);
		}
	}

}
