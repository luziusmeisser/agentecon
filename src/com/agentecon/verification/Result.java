package com.agentecon.verification;

import java.util.TreeMap;

import com.agentecon.good.Good;

public class Result {

	private TreeMap<Good, DataPoint> map;
	
	public Result(){
		this.map = new TreeMap<>();
	}

	public void include(Good good, double price, double volume) {
		this.map.put(good, new DataPoint(good, price, volume));
	}

	class DataPoint {

		private Good good;
		private double weight = 1.0;
		private double price;
		private double volume;
		
		public DataPoint(Good good, double price, double volume) {
			this.good = good;
			this.price = price;
			this.volume = volume;
		}
		
		public String toString(){
			return good + " costs " + price + " (volume " + volume + ")";
		}

		public void absorb(DataPoint dp) {
			assert good.equals(dp.good);
			this.price = (price * weight + dp.price * dp.weight) / (weight + dp.weight);
			this.volume = (volume * weight + dp.volume * dp.weight) / (weight + dp.weight);
			this.weight = weight + dp.weight;
		}
		
	}

	public double getPrice(Good good) {
		DataPoint dp = map.get(good);
		return dp == null ? Double.NaN : dp.price;
	}
	
	public double getAmount(Good good) {
		DataPoint dp = map.get(good);
		return dp == null ? Double.NaN : dp.volume;
	}

	public Result normalize(Good good) {
		double price = getPrice(good);
		Result norm = new Result();
		for (DataPoint p: map.values()){
			norm.include(p.good, p.price / price, p.volume);
		}
		return norm;
	}
	
	public double getRatio(Good i, Good s) {
		return getPrice(i) / getPrice(s);
	}
	
	public String toString(){
		return map.values().toString();
	}

	public void absorb(Result res) {
		for (DataPoint dp: res.map.values()){
			DataPoint existing = map.get(dp.good);
			if (existing == null){
				include(dp.good, dp.price, dp.volume);
			} else {
				existing.absorb(dp);
			}
		}
	}

}
