package com.agentecon.trader;

import java.util.Collection;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Firm;
import com.agentecon.firm.SensorInputFactor;
import com.agentecon.firm.SensorOutputFactor;
import com.agentecon.good.Good;
import com.agentecon.market.Market;
import com.agentecon.price.ExpSearchPrice;
import com.agentecon.world.Trader;

public class VolumeTrader extends Trader {

	private int flipDate;
	private double amount;
	private double walletTargetAmount;

	private SensorInputFactor input;
	private SensorOutputFactor output;

	public VolumeTrader(Endowment end, double amount, Good good, int flipDate) {
		super("Volume Trader", end);
		this.flipDate = flipDate;
		this.amount = amount;
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
				output.createOffer(market, getMoney(), amount);
			}
		}
	}

	private boolean isBuying(int day) {
		return day < flipDate;
	}
	
	public void refillWallet(Collection<Firm> firms){
		double excess = getMoney().getAmount() - walletTargetAmount;
		for (Firm f: firms){
			f.getMoney().transfer(getMoney(), excess / firms.size());
		}
	}

	@Override
	public void notifyDayEnded(int day) {
		if (amount > 0.0) {
			if (isBuying(day)) {
				input.adaptPrice();
			} else {
				output.adaptPrice();
			}
		}
	}

	public String toString() {
		return super.toString();
	}

}
