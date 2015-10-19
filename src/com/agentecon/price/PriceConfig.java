package com.agentecon.price;

import java.util.Random;

public class PriceConfig {

	private static final double DEFAULT_ACCURACY = 0.03;

	public static final PriceConfig DEFAULT = new PriceConfig(true, EPrice.EXPSEARCH, DEFAULT_ACCURACY);

	public static final PriceConfig[] STANDARD_CONFIGS = new PriceConfig[] { new PriceConfig(false, EPrice.CONSTANTPERCENTAGE, DEFAULT_ACCURACY),
			new PriceConfig(false, EPrice.CONSTANTFACTOR, DEFAULT_ACCURACY), new PriceConfig(false, EPrice.RANDOMIZED, DEFAULT_ACCURACY), new PriceConfig(false, EPrice.EXPSEARCH, DEFAULT_ACCURACY),
			new PriceConfig(true, EPrice.CONSTANTPERCENTAGE, DEFAULT_ACCURACY), new PriceConfig(true, EPrice.CONSTANTFACTOR, DEFAULT_ACCURACY),
			new PriceConfig(true, EPrice.RANDOMIZED, DEFAULT_ACCURACY), new PriceConfig(true, EPrice.EXPSEARCH, DEFAULT_ACCURACY) };

	private boolean sensor;
	private EPrice type;
	private double accuracy;
	
	public PriceConfig() {
		this(true, EPrice.EXPSEARCH);
	}
	
	public PriceConfig(boolean sensor, EPrice type) {
		this(sensor, type, DEFAULT_ACCURACY);
	}

	public PriceConfig(boolean sensor, EPrice type, double accuracy) {
		super();
		this.sensor = sensor;
		this.type = type;
		this.accuracy = accuracy;
	}

	public IPrice createPrice(Random rand) {
		switch (type) {
		default:
		case CONSTANT:
			return new HardcodedPrice(accuracy);
		case CONSTANTPERCENTAGE:
			return new ConstantPercentagePrice(accuracy);
		case CONSTANTFACTOR:
			return new ConstantFactorPrice(accuracy);
		case RANDOMIZED:
			return new RandomizedFactorPrice(rand, accuracy);
		case EXPSEARCH:
			return new ExpSearchPrice(accuracy);
		case RATIONAL:
			return new RationalExpectationsPrice(new ExpSearchPrice(accuracy));
		case HISTORICHINT:
			return new HistoricHintPrice(accuracy);
		}
	}

	public boolean isSensor() {
		return sensor;
	}

	public String getName() {
		if (sensor) {
			return "sensor prices with " + type.getName() + " adjustments";
		} else {
			return type.getName() + " adjustments";
		}
	}
	
	public String toString(){
		return getName();
	}

}
