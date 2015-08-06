package com.agentecon.world;

import java.util.Collection;

import com.agentecon.api.IFirm;
import com.agentecon.firm.Firm;

public interface IFirms {
	
	public void add(Firm firm);
	
	public Collection<Firm> getRandomFirms();
	
	/**
	 * A random selection of 'cardinality' firms in random order
	 */
	public Collection<Firm> getRandomFirms(int cardinality);

	public Collection<? extends IFirm> getAllFirms();

}
