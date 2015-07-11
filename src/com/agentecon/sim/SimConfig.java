// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.sim;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.agentecon.api.Event;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.SimEvent;
import com.agentecon.good.Good;

public class SimConfig extends SimulationConfig {
	
	public static final boolean AGING = false;
	
	public static final double GOODS_PERSISTENCE = 1.0;
	
	public static final Good MONEY = new Good("Taler");
	public static final Good PIZZA = new Good("Pizza", GOODS_PERSISTENCE);
	public static final Good FONDUE = new Good("Fondue", GOODS_PERSISTENCE);
	public static final Good BEER = new Good("Beer", GOODS_PERSISTENCE);
	public static final Good SWISSTIME = new Good("Swiss man-hours", 0.0);
	public static final Good ITALTIME = new Good("Italian man-hours", 0.0);
	public static final Good GERTIME = new Good("German man-hours", 0.0);
	
	public SimConfig(int rounds, int seed) {
		super(rounds, seed);
	}
	
	public SimConfig(int rounds) {
		super(rounds);
	}
	
	public Queue<SimEvent> createEventQueue() {
		PriorityBlockingQueue<SimEvent> queue = new PriorityBlockingQueue<>();
		for (Event e: getEvents()){
			queue.add((SimEvent) e);
		}
		return queue;
	}
	
	@Override
	public boolean hasAging(){
		return AGING;
	}

}
