package com.agentecon.price;

import java.util.ArrayList;

import com.agentecon.util.Average;

public class RationalExpectationsPrice extends ExpSearchPrice {
	
	private static final int FORESIGHT = 10;
	
	private int pos;
	private double initialFactor;
	private ArrayList<Double> prevIter;
	private ArrayList<Double> history;
	
	public RationalExpectationsPrice(double initialFactor){
		super(initialFactor);
		this.initialFactor = initialFactor;
		this.history = new ArrayList<>();
		this.pos = 0;
	}
	
	public RationalExpectationsPrice(double initialFactor, double initialPrice, ArrayList<Double> history){
		super(initialFactor, initialPrice);
		this.initialFactor = initialFactor;
		this.prevIter = history;
		this.history = new ArrayList<>();
		this.pos = 0;
	}

	@Override
	public void adapt(boolean increase) {
		super.adapt(increase);
		if (prevIter != null){
			int future = pos + FORESIGHT;
			if (future < prevIter.size()){
				double futureP = prevIter.get(future);
				super.adapt(futureP, 0.1);
			}
		}
		history.add(getPrice());
		pos++;
	}
	
	public RationalExpectationsPrice createNextGeneration(){
		return new RationalExpectationsPrice(initialFactor, history.get(FORESIGHT), history);
	}

	public double getAverage() {
		return new Average(history).getAverage();
	}

}
