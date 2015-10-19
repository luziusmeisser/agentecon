package com.agentecon.firm.sensor;

public class SensorAccuracy {
	
	public static final double UP_FACTOR = 2;
	public static final double DOWN_FACTOR = 1.005;
	public static final double SUCCESS_RATIO = 1 - Math.log(DOWN_FACTOR)/Math.log(UP_FACTOR);
	
	public static final double MIN = 0.01;
	public static final double MAX = 0.5;
	public static final double DEFAULT = MAX / 2;
	
	private double accuracy;
	
	public SensorAccuracy(){
		this(DEFAULT);
	}
	
	public SensorAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getOfferSize() {
		return getAccuracy();
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void lessAccurate() {
		accuracy = Math.min(MAX, accuracy * UP_FACTOR);
	}
	
	int count = 0;

	public void moreAccurate() {
		accuracy = Math.max(MIN, accuracy / DOWN_FACTOR);		
	}
	
}
