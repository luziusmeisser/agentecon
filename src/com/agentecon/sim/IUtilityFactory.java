package com.agentecon.sim;

import com.agentecon.consumer.IUtility;

public interface IUtilityFactory {
	
	public IUtility create(int number);

}
