package com.agentecon.sim;

import org.junit.Test;

public class SimulationTest {

	@Test
	public void test() {
		Simulation sim = new Simulation();
		while (!sim.isFinished()) {
			sim.forward(100);
		}
	}
	
//	@Test
//	public void testRepeat() {
//		TaxShockConfiguration config = new TaxShockConfiguration(10, 100, 1, 1, 237);
//		while(config.shouldTryAgain()){
//			Simulation sim = new Simulation(config.createNextConfig());
//			while (!sim.isFinished()) {
//				sim.forward(100);
//			}
//			System.out.println(config.getTradersProfit());
//		}
//	}
	

}
