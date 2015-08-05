package com.agentecon.agent;

import com.agentecon.api.IAgent;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.sim.SimConfig;

public abstract class Agent implements IAgent {

	private static int current = 1;
	
	private int number;
	private String type;
	private Endowment end;
	private Inventory inv;

	public Agent(String type, Endowment end) {
		this.type = type;
		this.inv = end.getInitialInventory();
		this.end = end;
		this.number = current++;
		
		assert type != null;
	}
	
	public String getName() {
		return getType() + " " + number;
	}

	public final String getType() {
		return type;
	}

	public final Inventory getInventory(){
		return inv;
	}
	
	public int getAgentId(){
		return number;
	}
	
	public Inventory dispose(){
		Inventory old = this.inv;
		this.inv = new Inventory();
		return old;
	}
	
	protected final IStock getStock(Good good) {
		return inv.getStock(good);
	}

	public final IStock getMoney() {
		return inv.getStock(SimConfig.MONEY);
	}

	public final void collectDailyEndowment() {
		inv.deprecate();
		inv.receive(end.getDaily());
	}

	public String toString() {
		return getType() + " with " + inv;
	}
	
}
