package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.sim.TaxShockConfiguration;
import com.agentecon.util.Average;

public class SavingConsumer extends Consumer {

	public static final int CHANGE = TaxShockConfiguration.TAX_EVENT;

	private double savings;
	private double smoothConsumption;
	private Average leisure;
	private Stock reserve;

	private double firstHalfConsumption = 0.0;
	private double totalConsumption = 0.0;

	public SavingConsumer(String type, Endowment end, IUtility utility, Good good) {
		this(type, end, utility, good, 0.0, 0.0);
	}

	public SavingConsumer(String type, Endowment end, IUtility utility, Good good, double smoothConsumption, double savingsPerDay) {
		super(type, end, utility);
		this.leisure = new Average();
		this.reserve = new Stock(good);
		this.savings = Math.max(0, savingsPerDay);
		this.smoothConsumption = smoothConsumption;
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		int age = getAge();
		if (age >= CHANGE) {
			int daysLeft = TaxShockConfiguration.ROUNDS - age;
			assert daysLeft > 0;
			double additional = reserve.getAmount() / daysLeft;
			IStock here = getStock(reserve.getGood());
			here.transfer(reserve, additional);
			super.trade(inv, market);
		} else {
			super.trade(inv.hide(reserve.getGood(), savings), market);
		}
	}

	@Override
	public double consume() {
		IStock inv = getStock(reserve.getGood());
		if (getAge() < CHANGE) {
			if (savings > 0.0 && inv.getAmount() > savings) {
//				double amount = inv.getAmount() - smoothConsumption;
//				if (amount > 0){
//					reserve.transfer(inv, amount);
//				} else if (amount < 0 && reserve.getAmount() > -amount){
//					reserve.transfer(inv, amount);
//				}
				reserve.transfer(inv, savings);
			}
			firstHalfConsumption += inv.getAmount();
		}
		leisure.add(1.0, getStock(soldGood).getAmount());
		totalConsumption += inv.getAmount();
		double cons = super.consume();
		assert inv.getAmount() == 0;
		return cons;
	}

	public SavingConsumer getNextGeneration(Endowment end) {
		double smoothConsumption = totalConsumption / TaxShockConfiguration.ROUNDS;
		double savingsPerDay = firstHalfConsumption / CHANGE - smoothConsumption;
		assert reserve.getAmount() == 0.0;
		return new SavingConsumer(getType(), end, getUtilityFunction(), reserve.getGood(), smoothConsumption, savingsPerDay + savings);
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
