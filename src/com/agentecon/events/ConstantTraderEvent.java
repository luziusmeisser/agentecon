package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.trader.VolumeTrader;
import com.agentecon.world.IWorld;

public class ConstantTraderEvent extends EvolvingEvent {

	private Good good;
	private double quantity;
	private VolumeTrader agent;

	public ConstantTraderEvent(Good money, double quantity, Good good) {
		super(0, -1);
		this.good = good;
		this.quantity = quantity;
		this.agent = new VolumeTrader(createEndowment(money), quantity, good, 500);
	}

	private static Endowment createEndowment(Good money) {
		return new Endowment(new Stock[] { new Stock(money, 100000000) }, new Stock[] {});
	}

	@Override
	public void execute(IWorld sim) {
		sim.getTraders().addTrader(agent);
	}

	public String toString() {
		return agent.toString();
	}

	@Override
	public EvolvingEvent createNextGeneration() {
		return new ConstantTraderEvent(agent.getMoney().getGood(), quantity + 0.2, good);
	}

	@Override
	public double getScore() {
		return 0;
	}

}
