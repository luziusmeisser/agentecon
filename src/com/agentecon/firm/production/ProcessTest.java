package com.agentecon.firm.production;

public class ProcessTest {
	
	private double prevProfits;
	private double cash;
	private boolean save;
	
	public ProcessTest(double initial){
		this.cash = initial;
		this.save = true;
		this.prevProfits = calcProfits(initial);
	}
	
	public void next(){
		double profits = calcProfits(cash);
		cash += (profits - prevProfits) / 2;
		prevProfits = profits;
		System.out.println("New cash level: " + cash);
	}
	
	public double calcProfits(double cash){
		double spending = cash / 4;
		double production = prodFun(spending);
		return (production - spending);
	}

	private double prodFun(double spending) {
		return 2*Math.sqrt(spending);
	}
	
	public static void main(String[] args) {
		ProcessTest test = new ProcessTest(2.0);
		for (int i=0; i<1000; i++){
			test.next();
		}
	}
	

}
