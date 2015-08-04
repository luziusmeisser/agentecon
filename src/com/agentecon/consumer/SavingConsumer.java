package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.sim.TaxShockConfiguration;
import com.agentecon.util.Average;

public class SavingConsumer extends Consumer {

	public static final int CHANGE = TaxShockConfiguration.TAX_EVENT;

	private double savings;
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
		this.savings = Math.max(0, savingsPerDay);
		this.smoothConsumption = smoothConsumption;
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		double soll = calculateRecommendedReserves();
		inv = inv.hide(good, soll);
		super.trade(inv, market);
	}

	@Override
	protected double doConsume(Inventory inv) {
		double soll = calculateRecommendedReserves();
		inv = inv.hide(good, soll);
		if (getAge() < CHANGE) {
			firstHalfConsumption += inv.getStock(good).getAmount();
		}
		leisure.add(1.0, getStock(soldGood).getAmount());
		totalConsumption += inv.getStock(good).getAmount();
		double cons = super.doConsume(inv);
		assert inv.getStock(good).isEmpty();
		return cons;
	}

	private double calculateRecommendedReserves() {
		int age = getAge();
		if (age < CHANGE) {
			return age * savings;
		} else {
			return (TaxShockConfiguration.ROUNDS - age - 1) * savings;
		}
	}

	public SavingConsumer getNextGeneration(Endowment end) {
		double smoothConsumption = totalConsumption / TaxShockConfiguration.ROUNDS;
		double savingsPerDay = firstHalfConsumption / CHANGE - smoothConsumption;
		assert getStock(good).getAmount() == 0.0;
		return new SavingConsumer(getType(), end, getUtilityFunction(), good, smoothConsumption, savingsPerDay + savings);
	}

	public double getDailySavings() {
		return savings;
	}

	public double getSmoothConsumption() {
		return smoothConsumption;
	}

	public double getAverageLeisure() {
		return leisure.getAverage();
	}

}
