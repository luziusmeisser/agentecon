package com.agentecon.sim;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.verification.StolperSamuelson;

public class StolperSamuelsonMeta implements IConfiguration {

	private int number;
	private StolperSamuelson ss = new StolperSamuelson(2);
	
	@Override
	public SimulationConfig createNextConfig() {
		SimulationConfig sc = ss.createConfiguration(isSensor(), getSearch(), "0.05");
		number++;
		return sc;
	}

	@Override
	public boolean shouldTryAgain() {
		return number < 8;
	}

	@Override
	public String getComment() {
		return getSearch().toLowerCase() + (isSensor() ? "with sensor prices" : "");
	}

	private String getSearch() {
		return PriceFactory.STANDARD_CONFIGS[number % PriceFactory.STANDARD_CONFIGS.length];
	}

	private boolean isSensor() {
		return number / PriceFactory.STANDARD_CONFIGS.length == 0;
	}

}
