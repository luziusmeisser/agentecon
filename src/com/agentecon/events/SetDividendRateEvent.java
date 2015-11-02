// Created on May 30, 2015 by Luzius Meisser

package com.agentecon.events;

import com.agentecon.firm.decisions.DifferentialDividend;
import com.agentecon.world.IWorld;

public class SetDividendRateEvent extends SimEvent {

	private double rate;

	public SetDividendRateEvent(int step, double rate) {
		super(step, -1);
		this.rate = rate;
	}

	@Override
	public void execute(IWorld sim) {
		DifferentialDividend.DIVIDEND_RATE = rate;
	}

}
