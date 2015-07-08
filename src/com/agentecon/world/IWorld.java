package com.agentecon.world;

import java.util.Random;


public interface IWorld {

	public IConsumers getConsumers();

	public IFirms getFirms();

	public Random getRand();

	public int getDay();

}
