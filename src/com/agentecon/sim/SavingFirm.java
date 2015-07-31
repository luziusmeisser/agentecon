package com.agentecon.sim;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.IProductionFunction;
import com.agentecon.firm.SensorFirm;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.IPriceFactory;

public class SavingFirm extends SensorFirm {
	
	private double amount;
	private Stock secret;

	public SavingFirm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices, double amount) {
		super(type, end, prod, prices);
		this.amount = amount;
		this.secret = new Stock(getGood());
	}
	
	@Override
	public double produce(int day) {
		double prod = super.produce(day);
		if (day < TaxShockConfiguration.TAX_EVENT){
			IStock present = getStock(secret.getGood());
			double transfer = Math.min(present.getAmount(), amount);
			secret.transfer(present, transfer);
		} else {
			IStock present = getStock(secret.getGood());
			double transfer = Math.min(secret.getAmount(), amount);
			present.transfer(secret, transfer);
		}
		return prod;
	}

}
