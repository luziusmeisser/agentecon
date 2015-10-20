package com.agentecon.finance;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.good.IStock;
import com.agentecon.metric.FirmListeners;
import com.agentecon.metric.IFirmListener;

public abstract class PublicCompany extends Agent implements IPublicCompany {

	private Ticker ticker;
	private ShareRegister register;
	protected FirmListeners monitor;
	
	public PublicCompany(String type, Endowment end) {
		super(type, end);
		this.ticker = new Ticker(type, getAgentId());
		this.register = new ShareRegister(ticker, getDividendWallet());
		this.monitor = new FirmListeners();
	}
	
	@Override
	public ShareRegister getShareRegister() {
		return register;
	}
	
	@Override
	public Ticker getTicker() {
		return ticker;
	}
	
	public void addFirmMonitor(IFirmListener prodmon) {
		this.monitor.add(prodmon);
	}
	
	@Override
	public void inherit(Position pos) {
		register.inherit(pos);
	}

	@Override
	public void raiseCapital(Object stockmarket) {
		register.raiseCapital((DailyStockMarket) stockmarket, getDividendWallet());
	}

	protected abstract double calculateDividends(int day);
	
	@Override
	public void payDividends(int day) {
		double dividend = calculateDividends(day);
		System.out.println(dividend);
		if (dividend > 0){
			monitor.reportDividend(this, dividend);
			register.payDividend(getDividendWallet(), dividend);
		}
	}

	protected IStock getDividendWallet() {
		return getMoney();
	}

}
