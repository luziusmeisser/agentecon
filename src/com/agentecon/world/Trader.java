package com.agentecon.world;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.market.Market;

public abstract class Trader extends Agent {

	public Trader(String type, Endowment end) {
		super(type, end);
	}
	
	public abstract void offer(Market market, int day);

	public abstract void notifyDayEnded(int day);

}
