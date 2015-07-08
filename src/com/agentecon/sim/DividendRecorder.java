// Created on Jun 2, 2015 by Luzius Meisser

package com.agentecon.sim;

import com.agentecon.api.IFirm;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.stats.DataRecorder;

public class DividendRecorder implements IFirmListener {
	
	private IFirm firm;
	private DataRecorder data;

	public DividendRecorder(IFirm firm, DataRecorder data) {
		this.firm = firm;
		this.data = data;
	}

	@Override
	public void notifyProduced(String type, IStock[] inputs, IStock output) {
	}

	@Override
	public void reportDividend(double amount) {
		data.record(firm.getName(), amount);
	}

}
