package com.agentecon.finance;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.metric.FirmListeners;
import com.agentecon.metric.IFirmListener;

public abstract class PublicFirm extends Agent implements IPublicCompany {

	private ShareRegister register;
	protected FirmListeners monitor;
	
	public PublicFirm(String type, Endowment end) {
		super(type, end);
		this.register = new ShareRegister(getName(), getMoney());
		this.monitor = new FirmListeners();
	}

	@Override
	public ShareRegister getShareRegister() {
		return register;
	}
	
	@Override
	public Ticker getTicker() {
		return register.getTicker();
	}
	
	public void addFirmMonitor(IFirmListener prodmon) {
		this.monitor.add(prodmon);
	}

	protected abstract double calculateDividends(int day);
	
	@Override
	public void payDividends(int day) {
		double dividend = calculateDividends(day);
		if (dividend > 0){
			monitor.reportDividend(dividend);
			register.payDividend(getMoney(), dividend);
		}
	}

}
