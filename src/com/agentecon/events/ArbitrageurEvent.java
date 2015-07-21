package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.sim.SimConfig;
import com.agentecon.trader.Arbitrageur;
import com.agentecon.world.IWorld;

public class ArbitrageurEvent extends SimEvent {

	private Arbitrageur agent;

	public ArbitrageurEvent(Good good) {
		this(new Arbitrageur(createEndowment(), good));
	}
	
	protected ArbitrageurEvent(Arbitrageur agent) {
		super(0, -1);
		this.agent = agent;
	}

	private static Endowment createEndowment() {
		return new Endowment(new Stock[]{new Stock(SimConfig.MONEY, 10000)}, new Stock[]{});
	}
	
	@Override
	public void execute(IWorld sim) {
		sim.getTraders().addTrader(agent);
	}
	
	public ArbitrageurEvent getNextGeneration(){
		return new ArbitrageurEvent(agent.createNextGeneration(createEndowment()));
	}

}
