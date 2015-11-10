package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.api.IAgent;
import com.agentecon.api.IMarket;
import com.agentecon.consumer.Consumer;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.MarketListeners;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.Agents;
import com.agentecon.world.World;

public class StockMarket extends SimulationListenerAdapter implements IMarket {

	private World world;
	private MarketListeners listeners;

	public StockMarket(World world) {
		this.listeners = new MarketListeners();
		this.world = world;
		this.world.addListener(this);
	}

	public void trade(int day) {
		Agents ags = world.getAgents();
		for (IPublicCompany firm : ags.getPublicCompanies()) {
			firm.payDividends(day);
		}
		Collection<MarketMaker> mms = ags.getAllMarketMakers();
		if (mms.isEmpty()) {
			// Assume model without stock market, distribute dividends proportionally among consumers
			distributeDividendsEqually(day, ags);
		} else {
			runDailyMarket(day, ags, mms);
		}
	}

	private void distributeDividendsEqually(int day, Agents ags) {
		IStock wallet = new Stock(SimConfig.MONEY);
		for (IPublicCompany firm : ags.getPublicCompanies()) {
			((ShareRegister)firm.getShareRegister()).collectRootDividend(wallet);
		}
		Collection<Consumer> consumers = ags.getAllConsumers();
		double dividend = wallet.getAmount() / consumers.size();
		for (Consumer cons: consumers){
			cons.getMoney().transfer(wallet, dividend);
		}
		if (!wallet.isEmpty()){
			consumers.iterator().next().getMoney().absorb(wallet);
		}
	}

	protected void runDailyMarket(int day, Agents ags, Collection<MarketMaker> mms) {
		DailyStockMarket dsm = new DailyStockMarket(listeners, world.getRand());
		for (MarketMaker mm : mms) {
			// System.out.println(day + ": " + mm);
			mm.postOffers(dsm);
		}
		// System.out.println(day + " trading stats " + dsm.getTradingStats());
		for (IPublicCompany pc : ags.getPublicCompanies()) {
			pc.raiseCapital(dsm);
		}
		for (IStockMarketParticipant con : ags.getRandomStockMarketParticipants()) {
			con.managePortfolio(dsm);
		}
	}

	@Override
	public void notifyAgentCreated(IAgent firm) {
		if (firm instanceof IPublicCompany) {
			notifyMarketMakers((IPublicCompany) firm);
		}
	}

	private void notifyMarketMakers(IPublicCompany comp) {
		ShareRegister register = (ShareRegister) comp.getShareRegister();
		Collection<MarketMaker> mms = world.getAgents().getAllMarketMakers();
		for (MarketMaker mm : mms) {
			mm.addPosition(register.createPosition());
		}
	}

	@Override
	public void addMarketListener(IMarketListener listener) {
		listeners.add(listener);
	}

}
