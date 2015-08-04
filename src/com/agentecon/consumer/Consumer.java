// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.finance.Portfolio;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceFilter;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.sim.SimConfig;
import com.agentecon.stats.Numbers;
import com.agentecon.util.MovingAverage;

public class Consumer extends Agent implements IConsumer {

	public static final boolean AGING = SimConfig.AGING;
	public static final int MAX_AGE = 500;
	public static final int RETIREMENT_AGE = MAX_AGE / 5 * 3;

	private int age;
	protected Good soldGood;
	private IUtility utility;
	private double lifetimeUtility;
	private Portfolio savings;
	private MovingAverage dailySpendings;

	private ConsumerListeners listeners;

	public Consumer(String type, Endowment end, IUtility utility) {
		super(type, end);
		this.soldGood = end.getDaily()[0].getGood();
		this.utility = utility;
		this.dailySpendings = new MovingAverage(0.95);
		this.listeners = new ConsumerListeners();
	}

	public void addListener(IConsumerListener listener) {
		this.listeners.add(listener);
	}

	public IUtility getUtilityFunction() {
		return utility;
	}

	public void setUtilityFunction(LogUtil utility) {
		this.utility = utility;
	}

	public void collectDividend(double dividend) {
		getMoney().add(dividend);
	}

	public void maximizeUtility(IPriceTakerMarket market) {
		Inventory inv = getInventory();
		IStock money = getMoney();
		double cash = money.getAmount();
		if (isRetired()) {
			int daysLeft = MAX_AGE - age + 1;
			double toSpend = cash / daysLeft;
			double toKeep = cash - toSpend;
			inv = inv.hide(money.getGood(), toKeep);
			inv = inv.hide(soldGood);
			trade(inv, market);
		} else {
			if (AGING) {
				double retirementSavingsGoal = dailySpendings.getAverage() * (MAX_AGE - RETIREMENT_AGE);
				double bynow = retirementSavingsGoal * (age + 10) / RETIREMENT_AGE;
				double missing = bynow - cash;
				if (missing > 0) {
					// we lack savings, need to save more
					inv = inv.hide(money.getGood(), missing / 10);
				} else {
					// we have excess savings, let's spend 5% of that
					inv = inv.hide(money.getGood(), bynow + missing / 20);
				}
			}
			trade(inv, market);
		}
	}

	protected void trade(Inventory inv, IPriceTakerMarket market) {
		boolean trading = true;
		double spendings = 0.0;
		while (trading) {
			trading = false;
			Collection<IOffer> offers = market.getOffers(new IPriceFilter() {

				@Override
				public boolean isAskPricePreferred(Good good) {
					return !good.equals(soldGood);
				}

				@Override
				public boolean isOfInterest(Good good) {
					if (soldGood.equals(good)) {
						return !isRetired();
					} else {
						return utility.isValued(good);
					}
				}
			});

			double[] allocs = utility.getOptimalAllocation(inv, offers);
			assert allocs.length == offers.size();

			boolean completedSales = true;
			int pos = 0;
			for (IOffer offer : offers) {
				IStock s = inv.getStock(offer.getGood());
				double excessStock = s.getAmount() - allocs[pos];
				if (excessStock > Numbers.EPSILON) {
					assert offer.getGood() == soldGood;
					double amountAcquired = offer.accept(getMoney(), s, excessStock);
					completedSales &= amountAcquired == excessStock;
					trading = true;
				}
				pos++;
			}
			if (!completedSales) {
				continue;
			}
			pos = 0;
			for (IOffer offer : offers) {
				IStock s = inv.getStock(offer.getGood());
				double difference = allocs[pos] - s.getAmount();
				if (difference > Numbers.EPSILON) {
					assert offer.getGood() != soldGood;
					offer.accept(getMoney(), s, difference);
					spendings += difference * offer.getPrice().getPrice();
					trading = true;
				}
				pos++;
			}
		}
		dailySpendings.add(spendings);
	}

	public double consume() {
		double u = utility.consume(getInventory().getAll());
		assert!Double.isNaN(u);
		assert u >= 0.0;
		lifetimeUtility += u;
		return u;
	}

	@SuppressWarnings("unused")
	public boolean age() {
		this.age++;
		return AGING && age > MAX_AGE;
	}

	@SuppressWarnings("unused")
	public boolean isRetired() {
		return AGING && age > RETIREMENT_AGE;
	}

	public Inventory notifyDied() {
		return dispose();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public double getTotalExperiencedUtility() {
		return lifetimeUtility;
	}

	@Override
	public int getAge() {
		return age;
	}

}
