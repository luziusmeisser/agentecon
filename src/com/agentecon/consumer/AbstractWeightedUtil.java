package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collection;

import com.agentecon.good.Good;
import com.agentecon.good.IStock;

public abstract class AbstractWeightedUtil implements IUtility {

	private Weight[] weights;

	public AbstractWeightedUtil(Weight... weights) {
		this.weights = weights;
	}
	
	public AbstractWeightedUtil(Weight[] weights, Weight... moreWeights) {
		this.weights = new Weight[weights.length + moreWeights.length];
		System.arraycopy(weights, 0, this.weights, 0, weights.length);
		System.arraycopy(moreWeights, 0, this.weights, weights.length, moreWeights.length);
	}
	
	@Override
	public double[] getWeights() {
		double[] ws = new double[weights.length];
		for (int i=0; i<weights.length; i++){
			ws[i] = weights[i].weight;
		}
		return ws;
	}
	
	@Override
	public Good[] getGoods() {
		Good[] goods = new Good[weights.length];
		for (int i=0; i<goods.length; i++){
			goods[i] = weights[i].good;
		}
		return goods;
	}
	
	protected void normalizeWeights(double total) {
		double tot = getTotalWeight();
		if (tot != total){
			for (int i=0; i<weights.length; i++){
				weights[i] = new Weight(weights[i].good, weights[i].weight / tot * total);
			}
		}
		assert getTotalWeight() == total;
	}

	private double getTotalWeight() {
		double tot = 0.0;
		for (Weight w: weights){
			tot += w.weight;
		}
		return tot;
	}
	
	public int getWeightCount(){
		return weights.length;
	}

	protected double getWeight(Good good) {
		for (int i = 0; i < weights.length; i++) {
			if (weights[i].good == good) {
				return weights[i].weight;
			}
		}
		return 0.0;
	}
	
	@Override
	public double consume(Collection<IStock> goods) {
		double util = getUtility(goods);
		for (IStock good : goods) {
			if (getWeight(good.getGood()) != 0.0) {
				good.consume();
			}
		}
		return util;
	}

	@Override
	public void updateWeight(Weight weight) {
		for (int i=0; i<weights.length; i++){
			if (weights[i].good.equals(weight.good)){
				weights[i] = weight;
			}
		}
	}

	@Override
	public boolean isValued(Good good) {
		return getWeight(good) >= 0.0;
	}

	public String toString() {
		return "Log utility function with weights " + Arrays.toString(weights);
	}

}