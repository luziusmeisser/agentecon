// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.sim.SimConfig;
import com.agentecon.stats.Numbers;

public class LogUtilTest {
	
	@Test
	public void testLogUtil(){
		LogUtil util = new LogUtil(new Weight(SimConfig.PIZZA, 8.0), new Weight(SimConfig.FONDUE, 2.0), new Weight(SimConfig.SWISSTIME, 14.0));
		assert util.getUtility(Collections.<IStock>emptyList()) == 0.0;
		Stock s1 = new Stock(SimConfig.SWISSTIME, 10);
		Stock s2 = new Stock(SimConfig.PIZZA, 10);
		assert util.getUtility(Arrays.<IStock>asList(s1, s2)) > 0.0;
		assert util.getUtility(Arrays.<IStock>asList(s1, s2)) == util.getUtility(Arrays.<IStock>asList(s1)) + util.getUtility(Arrays.<IStock>asList(s2));
	}
	
	@Test
	public void testLogUtil2(){
		LogUtil util = new LogUtil(new Weight(SimConfig.PIZZA, 8.0), new Weight(SimConfig.SWISSTIME, 14.0));
		assert util.getUtility(Collections.<IStock>emptyList()) == 0.0;
		Stock s1 = new Stock(SimConfig.SWISSTIME, 24.0 - 2.27272727273);
		Stock s2 = new Stock(SimConfig.PIZZA, 1.99997508071);
		assert Numbers.equals(util.getUtility(Arrays.<IStock>asList(s1, s2)), 52.51875088854);
	}
	
	@Test
	public void testEquilibrium(){
		LogUtil utilFun = new LogUtil(new Weight(SimConfig.FONDUE, 10), new Weight(SimConfig.SWISSTIME, 14));
		Stock s1 = new Stock(SimConfig.SWISSTIME, 24 - 3.03683);
		Stock s2 = new Stock(SimConfig.FONDUE, 3.5458);
		double utility = utilFun.getUtility(Arrays.<IStock>asList(s1, s2));
		assert Numbers.equals(utility, 58.39317473172329);
		
		Endowment end = ConsumerTest.createEndowment();
		Inventory inv = end.getInitialEndowment();
		inv.receive(end.getDaily());
		double[] alloc = utilFun.getOptimalAllocation(inv, Arrays.asList(ConsumerTest.createAsk(), ConsumerTest.createBid()));
		assert Numbers.equals(alloc[0], 3.61564375); 
		assert Numbers.equals(alloc[1], 20.7362388); 
	}
}
