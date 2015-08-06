package com.agentecon.sim;

public class WobbleConfiguration extends TaxShockConfiguration {
	
	private int wobbles;

	public WobbleConfiguration(int seed) {
		super(seed);
	}
	
	@Override
	protected SimConfig createConfig(int seed) {
		return new SimConfig(1000, seed, wobbles++);
	}
	
	@Override
	public boolean shouldTryAgain(){
		return wobbles < 20;
	}

}
