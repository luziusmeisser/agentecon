package com.agentecon.finance;

import java.util.Collection;
import java.util.PriorityQueue;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IAgent;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.Bid;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class Fundamentalist extends PublicFirm implements IAgent, IStockMarketParticipant {

	private static final int CASH = 1000;
	private static final int NUMBER_OF_POSITIONS = 5;

	private IWorld world;
	private double dividend;
	private Portfolio portfolio;

	public Fundamentalist(IWorld world) {
		super("Fundamentalist", new Endowment(new IStock[] { new Stock(SimConfig.MONEY, CASH) }, new IStock[] {}));
		this.world = world;
		this.dividend = 0.0;
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
		portfolio.collectDividends();

		double outerValue = calcOuterValue(dsm);
		double innerValue = calcInnerValue(dsm);
		boolean buyingAllowed = 2 * outerValue > innerValue;
		boolean sellingAllowed = outerValue < 2 * innerValue;
		double excessValue = innerValue - outerValue;

		Collection<IPublicCompany> comps = world.getAgents().getPublicCompanies();
		IPublicCompany best = findBestDeal(dsm, comps);
		IPublicCompany worst = findWorstPosition(dsm);

		if (sellingAllowed && worst != null && best != null && portfolio.getPositions().size() == NUMBER_OF_POSITIONS && getYield(dsm, worst, false) < getYield(dsm, best, true)) {
			Position pos = portfolio.getPosition(worst.getTicker());
			dsm.sell(pos, getMoney(), pos.getAmount());
			if (pos.isEmpty()) {
				portfolio.disposePosition(pos.getTicker());
			}
		}
		if (excessValue > 0) {
			dividend = excessValue / 5;
		}
		if (buyingAllowed && getMoney().getAmount() > dividend && best != null) {
			Position pos = portfolio.getPosition(best.getTicker());
			if (pos != null || portfolio.getPositions().size() < NUMBER_OF_POSITIONS) {
				portfolio.addPosition(dsm.buy(best.getTicker(), pos, getMoney(), getMoney().getAmount() - dividend));
			}
		}
		System.out.println(this);
	}

	private double price = 10.0;

	protected double calcOuterValue(IStockMarket dsm) {
		Bid bid = dsm.getBid(getTicker());
		if (bid != null) {
			price = bid.getPrice().getPrice();
		}
		return price * Position.SHARES_PER_COMPANY;
	}

	private double getYield(IStockMarket dsm, IPublicCompany best, boolean b) {
		return new YieldComparator(dsm, b).getYield(best);
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

	protected IPublicCompany findBestDeal(IStockMarket dsm, Collection<IPublicCompany> comps) {
		PriorityQueue<IPublicCompany> queue = new PriorityQueue<>(comps.size(), new YieldComparator(dsm, true));
		for (IPublicCompany pc : comps) {
			if (dsm.hasAsk(pc.getTicker())) {
				queue.add(pc);
			}
		}
		return queue.peek();
	}

	@Override
	protected double calculateDividends(int day) {
		return Math.min(getMoney().getAmount(), dividend);
	}

	@Override
	public Fundamentalist clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public String toString(){
		return getTicker() + " with " + portfolio;
	}
	
}
