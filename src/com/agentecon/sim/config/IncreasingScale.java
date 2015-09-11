package com.agentecon.sim.config;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.PriceConfig;
import com.agentecon.verification.StolperSamuelson;

public class IncreasingScale implements IConfiguration {

	private int scale = 0;
	private StolperSamuelson ss = new StolperSamuelson();

	@Override
	public SimulationConfig createNextConfig() {
		scale++;
		return ss.createConfiguration(PriceConfig.DEFAULT, scale, 3000);
	}

	@Override
	public boolean shouldTryAgain() {
		return scale < 10;
	}

	@Override
	public String getComment() {
		return "scale " + scale;
	}

}
