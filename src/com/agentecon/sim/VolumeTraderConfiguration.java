package com.agentecon.sim;

import com.agentecon.events.ConstantTraderEvent;

public class VolumeTraderConfiguration extends TaxShockConfiguration {
	
	public VolumeTraderConfiguration(int seed, double amount) {
		super(seed);
		evolvingEvents.add(new ConstantTraderEvent(SimConfig.MONEY, amount, outputs[0]));
	}
	
}
