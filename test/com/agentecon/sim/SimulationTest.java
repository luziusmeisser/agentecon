package com.agentecon.sim;

import org.junit.Test;

import sun.applet.Main;

public class SimulationTest {

	@Test
	public void test() {
		Simulation sim = new Simulation();
		while (!sim.isFinished()) {
			sim.forward(100);
		}
	}
	
	public static void main(String[] args) {
		TaxShockConfiguration config = new TaxShockConfiguration(10, 100, 1, 1, 232);
		while(config.shouldTryAgain()){
			Simulation sim = new Simulation(config.createNextConfig());
			while (!sim.isFinished()) {
				sim.forward(100);
			}
		}
	}

}
