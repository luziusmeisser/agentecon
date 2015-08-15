package com.agentecon.sim;

import com.agentecon.api.IAgent;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.stats.Numbers;
import com.agentecon.world.Agents;

public class MoneySupplyVerification {
	
	private double sum;
	private Agents agents;

	public MoneySupplyVerification(Agents agents) {
		this.agents = agents;
		this.sum = calcSum();
	}

	private double calcSum() {
		double money = 0.0;
		for (IAgent ag : agents.getAllConsumers()) {
			money += ag.getMoney().getAmount();
		}
		for (IAgent ag : agents.getAllFirms()) {
			money += ag.getMoney().getAmount();
		}
		for (IAgent ag : agents.getAllTraders()) {
			money += ag.getMoney().getAmount();
		}
		return money;
	}

	public void verify() {
		double current = calcSum();
		assert Numbers.equals(sum, current);
	}

}
