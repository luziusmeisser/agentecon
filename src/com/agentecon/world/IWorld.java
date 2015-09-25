package com.agentecon.world;

import java.util.Random;

import com.agentecon.api.IAgent;
import com.agentecon.metric.ISimulationListener;


public interface IWorld {
	
	public void add(IAgent agent);
	
	public void addListener(ISimulationListener listener);

	public IConsumers getConsumers();

	public IFirms getFirms();

	public Random getRand();

	public int getDay();

	public Agents getAgents();


}
