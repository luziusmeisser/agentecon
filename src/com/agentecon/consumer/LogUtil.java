// Created by Luzius on May 6, 2014

package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.sim.SimConfig;

public class LogUtil implements IUtility {

	private static final double ADJUSTMENT = 1.0; // to avoid negative utility

	private static final double GOODS_PREF = 10;
	private static final double LEISURE_PREF = Endowment.HOURS_PER_DAY - 10;

	private Weight[] weights;

	public LogUtil(double pizzaPref, Good leisureType) {
		this(new Weight(SimConfig.PIZZA, pizzaPref), new Weight(SimConfig.FONDUE, GOODS_PREF - pizzaPref), new Weight(leisureType, LEISURE_PREF));
	}
	
	public LogUtil(Weight[] weights, Weight... moreWeights) {
		this.weights = new Weight[weights.length + moreWeights.length];
		System.arraycopy(weights, 0, this.weights, 0, weights.length);
		System.arraycopy(moreWeights, 0, this.weights, weights.length, moreWeights.length);
	}

	public LogUtil(Weight... weights) {
		this.weights = weights;
	}

	private double getWeight(Good good) {
		for (int i = 0; i < weights.length; i++) {
			if (weights[i].good == good) {
				return weights[i].weight;
			}
		}
		return 0.0;
	}

	public double getUtility(Collection<IStock> goods) {
		double u = 0.0;
		for (IStock s : goods) {
			double weight = getWeight(s.getGood());
			if (weight > 0.0) {
				u += Math.log(s.getAmount() + ADJUSTMENT) * weight;
			}
		}
		return u;
	}
	
	@Override
	public void updateWeight(Weight weight) {
		for (int i=0; i<weights.length; i++){
			if (weights[i].good.equals(weight.good)){
				weights[i] = weight;
			}
		}
	}

	@Override
	public double consume(Collection<IStock> goods) {
		double util = getUtility(goods);
		for (IStock good : goods) {
			if (getWeight(good.getGood()) != 0.0) {
				good.consume();
			}
		}
		return util;
	}

	public double[] getOptimalAllocation(Inventory inv, Collection<IOffer> prices) {
		return getOptimalAllocation(inv, prices, new HashSet<Good>());
	}

	public double[] getOptimalAllocation(Inventory inv, Collection<IOffer> prices, HashSet<Good> ignorelist) {
		double endowment = inv.getStock(SimConfig.MONEY).getAmount();
		double totweight = getWeight(SimConfig.MONEY);

		// Note that goods in the inventory that have no price can be safely ignored
		// as one cannot buy or sell them anyway. Also, they do not influence the
		// quantities of other goods with log utility.
		for (IOffer offer : prices) {
			if (!ignorelist.contains(offer.getGood())) {
				endowment += (inv.getStock(offer.getGood()).getAmount() + ADJUSTMENT) * offer.getPrice().getPrice();
				totweight += getWeight(offer.getGood());
			}
		}
		double[] targetAmounts = new double[prices.size()];
		int pos = 0;
		for (IOffer offer : prices) {
			Good good = offer.getGood();
			double present = inv.getStock(good).getAmount();
			if (ignorelist.contains(good)) {
				targetAmounts[pos++] = present;
			} else {
				double target = getWeight(good) * endowment / totweight / offer.getPrice().getPrice() - ADJUSTMENT;
				if ((target > present && offer.isBid()) || (target < present && !offer.isBid())) {
					// We want more of something that is not for sale or we want less of something there are no bids for
					// Should happen rarely
					ignorelist.add(good);
					return getOptimalAllocation(inv, prices, ignorelist);
				}
				targetAmounts[pos++] = target;
			}
		}
		return targetAmounts;
	}

	public String toString() {
		return "Log utility function with weights " + Arrays.toString(weights);
	}

	@Override
	public boolean isValued(Good good) {
		return getWeight(good) >= 0.0;
	}

}
