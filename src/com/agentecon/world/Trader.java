package com.agentecon.world;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.ITrader;
import com.agentecon.market.Market;

public abstract class Trader extends Agent implements ITrader {

	public Trader(String type, Endowment end) {
		super(type, end);
	}
	
	public abstract void offer(Market market, int day);

	public abstract void notifyDayEnded(int day);

}
