package com.agentecon.sim.config;

import com.agentecon.events.VolumeTraderEvent;
import com.agentecon.trader.VolumeTrader;

public class VolumeTraderConfiguration extends TaxShockConfiguration implements IConfiguration {
	
	private VolumeTraderEvent event;
	
	public VolumeTraderConfiguration(int seed, double amount) {
		super(seed);
		this.event = new VolumeTraderEvent(SimConfig.MONEY, amount, outputs[0]);
		this.evolvingEvents.add(event);
	}

	public VolumeTrader getTrader() {
		return event.getTrader();
	}
	
}
