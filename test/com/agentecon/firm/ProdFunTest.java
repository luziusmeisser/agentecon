// Created on Jun 2, 2015 by Luzius Meisser

package com.agentecon.firm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.agentecon.consumer.Weight;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.sim.SimConfig;

public class ProdFunTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		LogProdFun f1 = new LogProdFun(SimConfig.FONDUE, new Weight(SimConfig.SWISSTIME, 10.0));
		Inventory inv = new Inventory(new Stock(SimConfig.SWISSTIME, 30.3683d));
		f1.produce(inv);
		System.out.println(inv.getStock(SimConfig.FONDUE));
		assert Math.abs(inv.getStock(SimConfig.FONDUE).getAmount() - 35.458) < 0.001;
	}

}
