package com.agentecon.sim.config;

import com.agentecon.consumer.IUtility;

public interface IUtilityFactory {
	
	public IUtility create(int number);

}
