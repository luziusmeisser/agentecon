package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.api.IFirm;
import com.agentecon.consumer.Consumer;
import com.agentecon.good.Stock;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.Agents;
import com.agentecon.world.World;

public class StockMarket extends SimulationListenerAdapter {

	private World world;
	private MarketMaker mm;

	public StockMarket(World world) {
		this.world = world;
		this.mm = new MarketMaker(new Stock(SimConfig.MONEY, 1000));
		createInitialOwnershipStructure(mm);
		world.getAgents().add(mm);
		world.addListener(this);
	}

	public void trade(int day) {
		Agents ags = world.getAgents();
		for (IPublicCompany firm : ags.getPublicCompanies()) {
			firm.payDividends(day);
		}
		DailyStockMarket dsm = new DailyStockMarket(mm);
		for (MarketMaker mm : ags.getAllMarketMakers()) {
			System.out.println(day + ": " + mm);
			mm.postOffers(dsm);
		}
		for (Consumer con : ags.getRandomConsumers()) {
			con.manageSavings(dsm);
		}
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		if (firm instanceof IPublicCompany) {
			createInitialOwnershipStructure((IPublicCompany) firm);
		}
	}

	private void createInitialOwnershipStructure(IPublicCompany comp) {
		ShareRegister register = comp.getShareRegister();
//		Collection<Consumer> cons = world.getAgents().getAllConsumers();
//		if (cons.size() > 0) {
//			mm.addPosition(register.obtain(Position.SHARES_PER_COMPANY / 100));
//			Position[] portfolios = register.split(cons.size());
//			int i = 0;
//			for (Consumer con : cons) {
//				con.getPortfolio().addPosition(portfolios[i++]);
//			}
//		} else {
			mm.addPosition(register.obtain(Position.SHARES_PER_COMPANY));
//		}
	}

}
