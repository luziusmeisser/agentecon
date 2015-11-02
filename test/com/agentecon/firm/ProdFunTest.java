// Created on Jun 2, 2015 by Luzius Meisser

package com.agentecon.firm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.agentecon.consumer.Weight;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.LogProdFun;
import com.agentecon.good.Good;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;

public class ProdFunTest {
	
	
	public static final Good MONEY = new Good("Taler");
	public static final Good PIZZA = new Good("Pizza", 1.0);
	public static final Good FONDUE = new Good("Fondue", 1.0);
	public static final Good SWISSTIME = new Good("Swiss man-hours", 0.0);
	public static final Good ITALTIME = new Good("Italian man-hours", 0.0);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLog() {
		LogProdFun f1 = new LogProdFun(FONDUE, new Weight(SWISSTIME, 10.0));
		Inventory inv = new Inventory(new Stock(SWISSTIME, 30.3683d));
		f1.produce(inv);
		System.out.println(inv.getStock(FONDUE));
		assert Math.abs(inv.getStock(FONDUE).getAmount() - 35.458) < 0.001;
	}
	
	@Test
	public void testCobbDouglas() {
		CobbDouglasProduction f1 = new CobbDouglasProduction(FONDUE, new Weight(SWISSTIME, 0.4), new Weight(ITALTIME, 0.5));
		Inventory inv = new Inventory(new Stock(SWISSTIME, 10), new Stock(ITALTIME, 10));
		double prod = f1.produce(inv);
		
		Inventory inv2 = new Inventory(new Stock(SWISSTIME, 50), new Stock(ITALTIME, 50));
		double prod2 = f1.produce(inv2);
		System.out.println(inv2.getStock(FONDUE));
		assert Numbers.equals(prod2, prod * Math.pow(5.0, f1.getReturnsToScale()));
	}
	
}
