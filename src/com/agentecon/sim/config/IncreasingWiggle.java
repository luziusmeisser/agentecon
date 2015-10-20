package com.agentecon.sim.config;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.PriceConfig;
import com.agentecon.verification.StolperSamuelson;

public class IncreasingWiggle implements IConfiguration {

	private int wiggles = -1;
	private StolperSamuelson ss = new StolperSamuelson();

	@Override
	public SimulationConfig createNextConfig() {
		wiggles++;
		return ss.createConfiguration(PriceConfig.DEFAULT, wiggles, 1, 3000);
	}

	@Override
	public boolean shouldTryAgain() {
		return wiggles < 10;
	}

	@Override
	public String getComment() {
		return "Wiggles: " + wiggles;
	}

}
