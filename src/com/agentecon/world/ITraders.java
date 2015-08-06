package com.agentecon.world;

import java.util.Collection;

public interface ITraders {

	public void addTrader(Trader trader);

	public Collection<? extends Trader> getAllTraders();

}
