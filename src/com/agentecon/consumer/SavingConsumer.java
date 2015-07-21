package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.market.IPriceTakerMarket;

public class SavingConsumer extends Consumer {
	
	private static final int CHANGE = 500;
	
	private double savings = 0;
	private Stock stock;
	
	private double firstHalfConsumption = 0.0;
	private double totalConsumption = 0.0;
	
	public SavingConsumer(String type, Endowment end, IUtility utility, Good good) {
		this(type, end, utility, good, 0.0);
	}
	
	private SavingConsumer(String type, Endowment end, IUtility utility, Good good, double savingsPerDay) {
		super(type, end, utility);
		this.stock = new Stock(good);
		this.savings = savingsPerDay;
	}
	
	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		super.trade(inv.hide(stock.getGood(), savings), market);
	}
	
	@Override
	public double consume() {
		IStock inv = getStock(stock.getGood());
		if (getAge() >= CHANGE){
			double amount = Math.min(savings, stock.getAmount());
			inv.transfer(stock, amount);
		} else {
			double amount = Math.min(savings, inv.getAmount());
			stock.transfer(inv, amount);
			firstHalfConsumption += inv.getAmount();
		}
		totalConsumption += inv.getAmount();
		return super.consume();
	}
	
	public SavingConsumer getNextGeneration(Endowment end){
		double savingsPerDay = firstHalfConsumption / CHANGE - totalConsumption / (CHANGE + CHANGE);
		return new SavingConsumer(getType(), end, getUtilityFunction(), stock.getGood(), savingsPerDay);
	}

}
