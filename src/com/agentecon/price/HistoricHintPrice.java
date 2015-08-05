package com.agentecon.price;

import java.util.ArrayList;

import com.agentecon.util.Average;

public class HistoricHintPrice extends ExpSearchPrice implements IEvolvable {

	private int pos;
	private int anticipation;
	private double initialFactor;
	private ArrayList<Double> prevIter;
	private ArrayList<Double> history;

	public HistoricHintPrice(double initialFactor) {
		super(initialFactor);
		this.initialFactor = initialFactor;
		this.history = new ArrayList<>();
		this.anticipation = 100;
		this.pos = 0;
	}

	public HistoricHintPrice(double initialFactor, double initialPrice, int anticipation, ArrayList<Double> history) {
		super(initialFactor, initialPrice);
		this.initialFactor = initialFactor;
		this.anticipation = Math.max(1, anticipation);
		this.prevIter = history;
		this.history = new ArrayList<>();
		this.pos = 0;
	}

	@Override
	public void adapt(boolean increase) {
		super.adapt(increase);
		if (prevIter != null) {
			double hint = getHint(prevIter, pos);
			boolean hintIncreases = hint > getPrice();
			if (hintIncreases == increase) {
				super.adapt(hint, 0.1);
			}
		}
		history.add(getPrice());
		pos++;
	}

	private double getHint(ArrayList<Double> source, int current) {
		int end = Math.min(current + anticipation, source.size());
		double tot = 0.0;
		for (int i = current; i < end; i++) {
			tot += source.get(i);
		}
		return tot / (end - current);
	}

	@Override
	public IPrice clone() {
		throw new java.lang.RuntimeException(new CloneNotSupportedException());
	}

	public HistoricHintPrice createNextGeneration() {
		return new HistoricHintPrice(initialFactor, getHint(history, 0), anticipation * 4 / 5, history);
	}

	public double getAverage() {
		return new Average(history).getAverage();
	}

}
