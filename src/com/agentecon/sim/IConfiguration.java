package com.agentecon.sim;

import com.agentecon.api.SimulationConfig;

public interface IConfiguration {

	public SimulationConfig createNextConfig();

	public boolean shouldTryAgain();

	public String getComment();

}
