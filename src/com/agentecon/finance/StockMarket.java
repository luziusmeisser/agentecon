package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.api.IAgent;
import com.agentecon.api.IFirm;
import com.agentecon.api.IMarket;
import com.agentecon.consumer.Consumer;
import com.agentecon.market.MarketListeners;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.world.Agents;
import com.agentecon.world.World;

public class StockMarket extends SimulationListenerAdapter implements IMarket {

	private static final int MARKET_MAKERS = 5;

	private World world;
	private MarketListeners listeners;

	public StockMarket(World world) {
		this.listeners = new MarketListeners();
		this.world = world;
		this.world.addListener(this);
		for (int i = 0; i < MARKET_MAKERS; i++) {
			world.getAgents().add(new MarketMaker());
		}
	}

	public void trade(int day) {
		Agents ags = world.getAgents();
		for (IPublicCompany firm : ags.getPublicCompanies()) {
			firm.payDividends(day);
		}
		DailyStockMarket dsm = new DailyStockMarket(listeners);
		for (MarketMaker mm : ags.getAllMarketMakers()) {
			System.out.println(day + ": " + mm);
			mm.postOffers(dsm);
		}
		for (Consumer con : ags.getRandomConsumers()) {
			con.manageSavings(dsm);
		}
	}

	@Override
	public void notifyAgentCreated(IAgent firm) {
		if (firm instanceof IPublicCompany) {
			createInitialOwnershipStructure((IPublicCompany) firm);
		}
	}

	private void createInitialOwnershipStructure(IPublicCompany comp) {
		ShareRegister register = (ShareRegister)comp.getShareRegister();
		Collection<MarketMaker> mms = world.getAgents().getAllMarketMakers();
		Position[] poss = register.split(mms.size());
		int i = 0;
		for (MarketMaker mm : mms) {
			mm.addPosition(poss[i++]);
		}
	}

	@Override
	public void addMarketListener(IMarketListener listener) {
		listeners.add(listener);
	}

}
