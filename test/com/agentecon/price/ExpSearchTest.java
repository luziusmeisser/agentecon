// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.price;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExpSearchTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		ExpSearchPrice price = new ExpSearchPrice(0.05);
		double target = 100;
		int steps = 0;
		while (Math.abs(price.getPrice() - target) > ExpSearchPrice.MIN_ADAPTION_FACTOR * target){
			price.adapt(price.getPrice() < target);
			System.out.println(price);
			assert steps++ < 100;
		}
		System.out.println(steps);
	}
	
	@Test
	public void test2() {
		ExpSearchPrice price = new ExpSearchPrice(0.05);
		double target = 100;
		for (int i=0; i<1000; i++){
			price.adapt(price.getPrice() < target);
		}
		System.out.println("Current factor " + price.getFactor(false));
		target = 1000;
		int steps = 0;
		while (Math.abs(price.getPrice() - target) > 1.0){
			price.adapt(price.getPrice() < target);
			System.out.println(price);
			steps++;
//			assert steps < 100;
		}
		System.out.println(steps);
	}

}
