package com.agentecon.agent;

import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

public class Endowment {

	public static final int HOURS_PER_DAY = 24;
	
	private IStock[] initial;
	private IStock[] daily;
	
	public Endowment(IStock... daily) {
		this(new IStock[]{}, daily);
	}
		
	public Endowment(IStock[] initial, IStock[] daily) {
		this.initial = initial;
		this.daily = daily;
	}

	public Inventory getInitialInventory() {
		return new Inventory(clone(initial));
	}
	
	public IStock[] getDaily(){
		return clone(daily);
	}

	private IStock[] clone(IStock[] daily) {
		IStock[] copy = new IStock[daily.length];
		for (int i=0; i<copy.length; i++){
			copy[i] = daily[i].duplicate();
		}
		return copy;
	}

}
