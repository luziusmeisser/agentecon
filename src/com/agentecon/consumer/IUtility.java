// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.market.IOffer;


public interface IUtility {
	
	public double getUtility(Collection<IStock> quantities);
	
	public double consume(Collection<IStock> goods);

	public double[] getOptimalAllocation(Inventory inv, Collection<IOffer> offers);

	public void updateWeight(Weight weight);

	public boolean isValued(Good good);

}
