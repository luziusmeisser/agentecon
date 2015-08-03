package com.agentecon.trader;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.SensorInputFactor;
import com.agentecon.firm.SensorOutputFactor;
import com.agentecon.good.Good;
import com.agentecon.market.Market;
import com.agentecon.price.ExpSearchPrice;
import com.agentecon.sim.TaxShockConfiguration;
import com.agentecon.world.Trader;

public class VolumeTrader extends Trader {

	private int flipDate;
	private double amount;
	private double cost, income;
	private double totPurchases;
	private double walletTargetAmount;

	private SensorInputFactor input;
	private SensorOutputFactor output;

	public VolumeTrader(Endowment end, double amount, Good good, int flipDate) {
		super("Volume Trader", end);
		this.flipDate = flipDate;
		this.amount = amount;
		this.income = 0.0;
		this.cost = 0.0;
		this.walletTargetAmount = getMoney().getAmount();
		this.input = new SensorInputFactor(getStock(good), new ExpSearchPrice(0.1));
		this.output = new SensorOutputFactor(getStock(good), new ExpSearchPrice(0.1));
	}

	public void offer(Market market, int day) {
		if (amount > 0.0) {
			if (isBuying(day)) {
				double bid = amount * input.getPrice();
				input.createOffers(market, getMoney(), bid);
			} else {
				int daysLeft = TaxShockConfiguration.ROUNDS - day;
				double goodsLeft = output.getStock().getAmount();
				output.createOffer(market, getMoney(), goodsLeft / daysLeft);
			}
		}
	}

	private boolean isBuying(int day) {
		return day < flipDate;
	}
	
	public double refillWallet(double dividends){
		double missing = walletTargetAmount - getMoney().getAmount();
		double transfer = Math.min(dividends, missing);
		if (transfer > 0){
			this.cost += transfer;
		} else {
			this.income -= transfer;
		}
		getMoney().add(transfer);
		return dividends - transfer;
	}

	@Override
	public void notifyDayEnded(int day) {
		if (amount > 0.0) {
			if (isBuying(day)) {
				totPurchases += input.getQuantity();
				input.adaptPrice();
			} else {
				output.adaptPrice();
			}
		}
	}
	
	public double getIncome(){
		return income;
	}
	
	public double getCost(){
		return cost;
	}
	
	public double getPrice1(){
		return input.getPrice();
	}
	
	public double getPrice2(){
		return output.getPrice();
	}
	
	public double getTotalPurchases(){
		return totPurchases;
	}
	
	public double getTotalSales(){
		return totPurchases - output.getStock().getAmount();
	}

	public String toString() {
		return amount + "\t" + income + "\t" + cost + "\t" + input.getPrice() + "\t" + output.getPrice();
//		return "Volume trader buying " + amount + " per day, having made " + getProfits()/cost*100 + "% or " + getProfits() + "$, or " + getWorth() + ", or " + getTotalWorth();
	}

	public double getProfits() {
		return income - cost;
	}

}
