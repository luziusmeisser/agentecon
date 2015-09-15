package com.agentecon.verification;

import com.agentecon.api.SimulationConfig;
import com.agentecon.events.SimEvent;
import com.agentecon.firm.Firm;
import com.agentecon.firm.decisions.CogsDividend;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class ShockTest implements IConfiguration {

	private double magnitude;
	private int strategyIndex;
	private IFirmDecisions strategy;

	public ShockTest(double magnitude, IFirmDecisions strategy) {
		this.strategy = strategy;
		this.magnitude = magnitude;
	}
	
	public ShockTest(double magnitude) {
		this.strategyIndex = -1;
		this.magnitude = magnitude;
	}

	@Override
	public SimulationConfig createNextConfig() {
		final IFirmDecisions strategy = this.strategy == null ? new CogsDividend(0.5, strategyIndex) : this.strategy;
		StolperSamuelson scenario = new StolperSamuelson() {

			@Override
			protected void addSpecialEvents(SimConfig config) {
				super.updatePrefs(config, 500, HIGH - magnitude);
				config.addEvent(new SimEvent(500, -1) {
					
					@Override
					public void execute(IWorld sim) {
						for (Firm f: sim.getFirms().getRandomFirms(getCardinality())){
							f.setStrategy(strategy);
						}
					}
				});
			}

		};
		return scenario.createConfiguration(PriceConfig.DEFAULT, 1000);
	}

	@Override
	public boolean shouldTryAgain() {
		return strategy == null && strategyIndex < CogsDividend.TYPES - 1;
	}

	@Override
	public String getComment() {
		return strategy.toString() + " with " + magnitude + " shock";
	}

}
