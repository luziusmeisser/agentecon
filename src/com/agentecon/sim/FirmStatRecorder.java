// Created on Jun 2, 2015 by Luzius Meisser

package com.agentecon.sim;

import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.stats.DataRecorder;

public class FirmStatRecorder  extends SimulationListenerAdapter implements IFirmListener {

	private DataRecorder data;
	
	public FirmStatRecorder(DataRecorder data) {
		this.data = data;
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(new DividendRecorder(firm, data));
	}

	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
	}

	@Override
	public void notifyProduced(String type, IStock[] inputs, IStock output) {
	}

	@Override
	public void reportDividend(double amount) {
	}

}
