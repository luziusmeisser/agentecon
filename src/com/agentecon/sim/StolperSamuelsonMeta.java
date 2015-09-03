package com.agentecon.sim;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.PriceConfig;
import com.agentecon.verification.StolperSamuelson;

public class StolperSamuelsonMeta implements IConfiguration {

	private int number = -1;
	private StolperSamuelson ss = new StolperSamuelson();
	
	@Override
	public SimulationConfig createNextConfig() {
		number++;
		return ss.createConfiguration(getSearch());
	}

	@Override
	public boolean shouldTryAgain() {
		return number < PriceConfig.STANDARD_CONFIGS.length - 1;
	}

	@Override
	public String getComment() {
		return getSearch().getName();
	}

	private PriceConfig getSearch() {
		return PriceConfig.STANDARD_CONFIGS[number];
	}

}
