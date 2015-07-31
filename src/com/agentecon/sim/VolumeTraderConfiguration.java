package com.agentecon.sim;

import com.agentecon.events.VolumeTraderEvent;

public class VolumeTraderConfiguration extends TaxShockConfiguration {
	
	public VolumeTraderConfiguration(int seed, double amount) {
		super(seed);
		evolvingEvents.add(new VolumeTraderEvent(SimConfig.MONEY, amount, outputs[0]));
	}
	
}
