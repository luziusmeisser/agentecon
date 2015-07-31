package com.agentecon.sim;

import com.agentecon.events.ConstantTraderEvent;

public class VolumeTraderConfiguration extends TaxShockConfiguration {
	
	public VolumeTraderConfiguration(int seed) {
		super(seed);
		evolvingEvents.add(new ConstantTraderEvent(SimConfig.MONEY, 0.0, outputs[0]));
	}
	
}
