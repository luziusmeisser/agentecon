// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceFilter;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.stats.Numbers;
import com.agentecon.util.MovingAverage;

public class Consumer extends Agent implements IConsumer {

	private int age, maxAge;
	protected Good soldGood;
	private IUtility utility;
	private double lifetimeUtility;
	// private Portfolio savings; clone?
	private MovingAverage dailySpendings;

	// private ConsumerListeners listeners; clone?

	public Consumer(String type, Endowment end, IUtility utility) {
		this(type, Integer.MAX_VALUE, end, utility);
	}

	public Consumer(String type, int maxAge, Endowment end, IUtility utility) {
		super(type, end);
		this.maxAge = maxAge;
		this.soldGood = end.getDaily()[0].getGood();
		this.utility = utility;
		this.dailySpendings = new MovingAverage(0.95);
		// this.listeners = new ConsumerListeners();
	}

	public void addListener(IConsumerListener listener) {
		// this.listeners.add(listener);
	}

	public IUtility getUtilityFunction() {
		return utility;
	}

	public void setUtilityFunction(LogUtil utility) {
		this.utility = utility;
	}

	public void maximizeUtility(IPriceTakerMarket market) {
		Inventory inv = getInventory();
		IStock money = getMoney();
		double cash = money.getAmount();
		if (isRetired()) {
			int daysLeft = maxAge - age + 1;
			double toSpend = cash / daysLeft;
			double toKeep = cash - toSpend;
			inv = inv.hide(money.getGood(), toKeep);
			inv = inv.hide(soldGood);
			trade(inv, market);
		} else {
			if (isMortal()) {
				int retirementAge = getRetirementAge();
				double retirementSavingsGoal = dailySpendings.getAverage() * (maxAge - retirementAge);
				double bynow = retirementSavingsGoal * (age + 10) / retirementAge;
				inv = inv.hide(money.getGood(), (19 * cash + bynow) / 20); // 5% step towards savings goal
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

	public final double consume() {
		return doConsume(getInventory());
	}

	protected double doConsume(Inventory inv) {
		double u = utility.consume(inv.getAll());
		assert!Double.isNaN(u);
		assert u >= 0.0;
		lifetimeUtility += u;
		return u;
	}

	public boolean isMortal() {
		return maxAge < Integer.MAX_VALUE;
	}

	@SuppressWarnings("unused")
	public boolean age() {
		this.age++;
		return age > maxAge;
	}

	@SuppressWarnings("unused")
	public boolean isRetired() {
		return age > getRetirementAge();
	}

	private int getRetirementAge() {
		return maxAge / 5 * 3;
	}

	public Inventory notifyDied() {
		return dispose();
	}

	@Override
	public Consumer clone() {
		Consumer klon = (Consumer) super.clone();
		klon.dailySpendings = dailySpendings.clone();
		return klon;
	}

	@Override
	public double getTotalExperiencedUtility() {
		return lifetimeUtility;
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
