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
		private double price;
		private double volume;
		
		public DataPoint(Good good, double price, double volume) {
			this.good = good;
			this.price = price;
			this.volume = volume;
		}
		
	}
	
}
