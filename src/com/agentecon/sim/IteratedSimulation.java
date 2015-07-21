package com.agentecon.sim;

import com.agentecon.api.IIteratedSimulation;
import com.agentecon.api.ISimulation;

public class IteratedSimulation implements IIteratedSimulation {
	
	private TaxShockConfiguration config;
	
	public IteratedSimulation(){
		this.config = new TaxShockConfiguration(10, 100, 1, 1, 12);
	}

	@Override
	public ISimulation getNext() {
		return new Simulation(config.createNextConfig());
	}

}
