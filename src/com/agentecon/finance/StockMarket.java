package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.api.IAgent;
import com.agentecon.api.IMarket;
import com.agentecon.government.Government;
import com.agentecon.market.MarketListeners;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
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
		Government gov = ags.getGovernment();
		for (IShareholder shareholder : ags.getShareHolders()) {
			shareholder.getPortfolio().collectDividends(gov.getDividendTax());
		}
		gov.distributeWelfare(day, ags.getAllConsumers());
		DailyStockMarket dsm = new DailyStockMarket(listeners, world.getRand());
		for (MarketMaker mm : ags.getAllMarketMakers()) {
			// System.out.println(day + ": " + mm);
			mm.postOffers(dsm);
		}
		System.out.println(day + " trading stats " + dsm.getTradingStats());
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
