package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IAgent;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.Bid;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class Fundamentalist extends PublicCompany implements IAgent, IStockMarketParticipant {

	private static final int CASH = 1000;

	private IWorld world;
	private double reserve;
	private Portfolio portfolio;

	public Fundamentalist(IWorld world) {
		super("Fundamentalist", new Endowment(new IStock[] { new Stock(SimConfig.MONEY, CASH) }, new IStock[] {}));
		this.world = world;
		this.portfolio = new Portfolio(getMoney());
	}

	private double calcInnerValue(IStockMarket dsm) {
		double innerValue = getMoney().getAmount();
		YieldComparator yieldComp2 = new YieldComparator(dsm, false);
		for (Position pos : portfolio.getPositions()) {
			if (dsm.hasBid(pos.getTicker())) {
				innerValue += yieldComp2.getPrice(pos.getTicker()) * pos.getAmount();
			}
		}
		return innerValue;
	}

	public void managePortfolio(IStockMarket dsm) {
		IStock money = getMoney().hide(reserve);
		
		double outerValue = calcOuterValue(dsm);
		double innerValue = calcInnerValue(dsm);
		boolean buyingAllowed = 1.5 * outerValue > innerValue;
		boolean sellingAllowed = outerValue < 1.5 * innerValue;

		Collection<IPublicCompany> comps = world.getAgents().getPublicCompanies();
		PriorityQueue<IPublicCompany> queue = getOfferQueue(dsm, comps);
		int count = queue.size() / 5;
		if (sellingAllowed) {
			sellBadShares(money, dsm, queue, count);
		}
		while (queue.size() > count) {
			queue.poll();
		}
		if (buyingAllowed) {
			buyGoodShares(money, dsm, queue);
		}
	}

	protected void sellBadShares(IStock money, IStockMarket dsm, PriorityQueue<IPublicCompany> queue, int count) {
		for (int i = 0; i < count; i++) {
			IPublicCompany pc = queue.poll();
			Position pos = portfolio.getPosition(pc.getTicker());
			if (pos != null && !pos.isEmpty()) {
				dsm.sell(pos, money, pos.getAmount());
				if (pos.isEmpty()) {
					portfolio.disposePosition(pos.getTicker());
				}
			}
		}
	}

	protected void buyGoodShares(IStock money, IStockMarket dsm, PriorityQueue<IPublicCompany> queue) {
		ArrayList<IPublicCompany> list = new ArrayList<>(queue);
		for (int i = list.size() - 1; i >= 0 && !money.isEmpty(); i--) {
			IPublicCompany pc = list.get(i);
			Position pos = portfolio.getPosition(pc.getTicker());
			Position pos2 = dsm.buy(pc.getTicker(), pos, money, money.getAmount());
			portfolio.addPosition(pos2);
		}
	}

	private double price = 10.0;

	protected double calcOuterValue(IStockMarket dsm) {
		Bid bid = dsm.getBid(getTicker());
		if (bid != null) {
			price = bid.getPrice().getPrice();
		}
		return price * IRegister.SHARES_PER_COMPANY;
	}

	protected IPublicCompany findWorstPosition(IStockMarket dsm) {
		Collection<Position> pos = portfolio.getPositions();
		if (pos.isEmpty()) {
			return null;
		} else {
			PriorityQueue<IPublicCompany> queue = new PriorityQueue<>(pos.size(), new YieldComparator(dsm, false));
			for (Position p : pos) {
				if (dsm.hasBid(p.getTicker())) {
					queue.add(world.getAgents().getCompany(p.getTicker()));
				}
			}
			return queue.peek();
		}
	}

	protected PriorityQueue<IPublicCompany> getOfferQueue(IStockMarket dsm, Collection<IPublicCompany> comps) {
		PriorityQueue<IPublicCompany> queue = new PriorityQueue<>(comps.size(), new YieldComparator(dsm, true));
		for (IPublicCompany pc : comps) {
			if (dsm.hasAsk(pc.getTicker()) && !pc.getTicker().equals(getTicker())) {
				queue.add(pc);
			}
		}
		return queue;
	}

	@Override
	protected double calculateDividends(int day) {
		double excessCash = getMoney().getAmount() - CASH;
		if (excessCash > 0) {
			double dividend = excessCash / 3;
//			this.reserve = excessCash - dividend;
			return dividend;
		} else {
			this.reserve = 0.0;
			return 0.0;
		}
	}

	@Override
	public Fundamentalist clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public String toString() {
		return getTicker() + " with " + portfolio;
	}

}
