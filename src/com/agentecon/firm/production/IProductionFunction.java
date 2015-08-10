package com.agentecon.firm.production;

import com.agentecon.good.Good;
import com.agentecon.good.Inventory;

public interface IProductionFunction {

	public Good[] getInput();

	public Good getOutput();

	public double getWeight(Good input);

	public double produce(Inventory inventory);

}
