package com.agentecon.world;

import java.util.Collection;

import com.agentecon.api.IFirm;
import com.agentecon.firm.Producer;

public interface IFirms {
	
	public void add(Producer firm);
	
	public Collection<Producer> getRandomFirms();
	
	/**
	 * A random selection of 'cardinality' firms in random order
	 */
	public Collection<Producer> getRandomFirms(int cardinality);

	public Collection<? extends IFirm> getAllFirms();

}
