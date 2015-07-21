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

}
