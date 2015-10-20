package com.agentecon.sim.config;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.PriceConfig;
import com.agentecon.verification.StolperSamuelson;

public class IncreasingWiggle implements IConfiguration {

	private static final int[] CONFIGS = new int[]{0, 1, 10, 100};
	
	private int round = -1;
	private StolperSamuelson ss = new StolperSamuelson() { 
		protected void addSpecialEvents(SimConfig config) {
			updatePrefs(config, 500, LOW);
		}
	};

	@Override
	public SimulationConfig createNextConfig() {
		round++;
		return ss.createConfiguration(PriceConfig.DEFAULT, CONFIGS[round], 1, 1000);
	}

	@Override
	public boolean shouldTryAgain() {
		return round < CONFIGS.length - 1;
	}

	@Override
	public String getComment() {
		return "Wiggles: " + CONFIGS[round];
	}

}
