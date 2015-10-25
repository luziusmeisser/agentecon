package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.finance.DailyStockMarket;
import com.agentecon.good.Good;
import com.agentecon.good.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.sim.config.SavingConsumerConfiguration;
import com.agentecon.util.Average;

public class SavingConsumer extends Consumer {

	private static final int START = 100;
	private double phaseOneDailySavings;
	private double smoothConsumption;
	private Average leisure;
	private Good good;

	private double firstHalfConsumption = 0.0;
	private double totalConsumption = 0.0;

	public SavingConsumer(String type, Endowment end, IUtility utility, Good good) {
		this(type, end, utility, good, 0.0);
	}

	public SavingConsumer(String type, Endowment end, IUtility utility, Good good, double savingsPerDay) {
		this(type, end, utility, good, 0.0, savingsPerDay);
	}

	public SavingConsumer(String type, Endowment end, IUtility utility, Good good, double smoothConsumption, double savingsPerDay) {
		super(type, end, utility);
		this.good = good;
		this.leisure = new Average();
		this.phaseOneDailySavings = Math.max(0, savingsPerDay);
		this.smoothConsumption = smoothConsumption;
	}

	double reserves;

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		int age = getAge();
		if (age < START){
			reserves = 0.0;
		} else if (age < SavingConsumerConfiguration.SHOCK){
			reserves = inv.getStock(good).getAmount() + phaseOneDailySavings;
			inv = inv.hide(good, reserves);
		} else {
			double left = inv.getStock(good).getAmount();
			int daysLeft = SavingConsumerConfiguration.ROUNDS - age;
			reserves = left / daysLeft * (daysLeft - 1);
			inv = inv.hide(good, reserves);
		}
		super.trade(inv, market);
	}

	@Override
	protected double doConsume(Inventory inv) {
		inv = inv.hide(good, reserves);
		if (getAge() >= START) {
			if (getAge() < SavingConsumerConfiguration.SHOCK) {
				firstHalfConsumption += inv.getStock(good).getAmount() + LogUtil.ADJUSTMENT;
			}
			totalConsumption += inv.getStock(good).getAmount() + LogUtil.ADJUSTMENT;
			leisure.add(1.0, getStock(soldGood).getAmount());
		}
		double cons = super.doConsume(inv);
		assert inv.getStock(good).isEmpty();
		return cons;
	}

	public SavingConsumer getNextGeneration(IUtility util, Endowment end) {
		double smoothConsumption = totalConsumption / 1500; // TEMP
		double savingsPerDay = firstHalfConsumption / 500 - smoothConsumption;
		assert getStock(good).getAmount() == 0.0;
		// return new SavingConsumer(getType(), end, util, good);
		return new SavingConsumer(getType(), end, util, good, smoothConsumption, savingsPerDay + phaseOneDailySavings);
	}

	public double getDailySavings() {
		return phaseOneDailySavings;
	}

	public double getAverageConsumption() {
		return totalConsumption / 1500;
	}

	public double getAverageLeisure() {
		return leisure.getAverage();
	}

}
