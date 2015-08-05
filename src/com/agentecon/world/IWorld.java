package com.agentecon.world;

import java.util.Random;

import com.agentecon.metric.ISimulationListener;


public interface IWorld {
	
	public void addListener(ISimulationListener listener);

	public IConsumers getConsumers();

	public IFirms getFirms();

	public ITraders getTraders();
	
	public Random getRand();

	public int getDay();


}
