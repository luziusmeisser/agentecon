package com.agentecon.price;

public enum EPrice {

	CONSTANT, CONSTANTPERCENTAGE, CONSTANTFACTOR, RANDOMIZED, EXPSEARCH, HISTORICHINT, RATIONAL;

	public String getName(){
		switch(this){
		default:
		case CONSTANT:
			return "constant";
		case CONSTANTFACTOR:
			return "constant factor";
		case CONSTANTPERCENTAGE:
			return "constant percentage";
		case EXPSEARCH:
			return "exponential";
		case HISTORICHINT:
			return "historic hint";
		case RANDOMIZED:
			return "randomized factor";
		case RATIONAL:
			return "rational expectations";
		}
	}

}
