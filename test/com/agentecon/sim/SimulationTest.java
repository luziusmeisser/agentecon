package com.agentecon.sim;

import org.junit.Test;

import com.agentecon.metric.SimulationListenerAdapter;

public class SimulationTest extends SimulationListenerAdapter {
	
	
	public SimulationTest(){
		
	}

	@Test
	public void test() {
		Simulation sim = new Simulation();
		while (!sim.isFinished()) {
			sim.forward(100);
		}
	}
	
	public static void main(String[] args) {
		new SimulationTest().runAll();
	}

	private void runAll() {
		run(new Simulation(new TaxShockConfiguration(13)));
		run(new Simulation(new SavingConsumerConfiguration(13)));
		run(new Simulation(new VolumeTraderConfiguration(13)));		
	}

	private void run(Simulation simulation) {
		simulation.addListener(this);
		simulation.run();
	}

}
