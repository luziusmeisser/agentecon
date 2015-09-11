// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IOffer;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;

public class CobbDouglasUtilTest {
	
	@Test
	public void testLogUtil(){
		CobbDouglasUtil util = new CobbDouglasUtil(new Weight(SimConfig.PIZZA, 8.0), new Weight(SimConfig.FONDUE, 2.0), new Weight(SimConfig.SWISSTIME, 14.0));
		assert util.getUtility(Collections.<IStock>emptyList()) == 0.0;
		Stock s1 = new Stock(SimConfig.SWISSTIME, 10);
		Stock s2 = new Stock(SimConfig.PIZZA, 10);
		assert util.getUtility(Arrays.<IStock>asList(s1, s2)) == 0.0;
		Stock s3 = new Stock(SimConfig.FONDUE, 10);
		assert util.getUtility(Arrays.<IStock>asList(s1, s2, s3)) == 10.0;
	}
	
	@Test
	public void testEquilibrium(){
		CobbDouglasUtil utilFun = new CobbDouglasUtil(new Weight(SimConfig.FONDUE, 10), new Weight(SimConfig.SWISSTIME, 15));
		Stock s1 = new Stock(SimConfig.SWISSTIME, 24 - 3.03683);
		Stock s2 = new Stock(SimConfig.FONDUE, 3.5458);
		double utility = utilFun.getUtility(Arrays.<IStock>asList(s1, s2));
		assert Numbers.equals(utility, 10.298165523700048);
		
		Inventory inv = new Inventory(new Stock(SimConfig.SWISSTIME, 24));
		double[] alloc = utilFun.getOptimalAllocation(inv, Arrays.asList(createAsk(10), createBid(5)));
		double endow = 5*24;
		assert Numbers.equals(alloc[0], 10.0 / 25.0 * endow / 10); 
		assert Numbers.equals(alloc[1], 15.0 / 25.0 * endow / 5); 
	}
	
	public static IOffer createBid(double price) {
		return new Bid(new Stock(SimConfig.MONEY, 10000), new Stock(SimConfig.SWISSTIME), new Price(SimConfig.SWISSTIME, price), 1000);
	}

	public static IOffer createAsk(double price) {
		return new Ask(new Stock(SimConfig.MONEY), new Stock(SimConfig.FONDUE, 1000), new Price(SimConfig.FONDUE, price), 1000);
	}
}
