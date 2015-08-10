package com.agentecon.firm.production;

import com.agentecon.good.Good;
import com.agentecon.good.Inventory;

public interface IProductionFunction {

	public Good[] getInput();

	public Good getOutput();

	public double produce(Inventory inventory);
	
	public double getCostOfMaximumProfit(IPriceProvider prices);
	
	public double getExpenses(Good good, double price, double totalSpendings);
	
}
