// Created on 25.10.2015 by Luzius Meisser

package com.agentecon.agent;

public class AgentRef {

	private Agent agent;
	
	public AgentRef(Agent agent){
		this.agent = agent;
	}
	
	public void set(Agent agent) {
		assert this.agent.getAgentId() == agent.getAgentId();
		this.agent = agent;
	}
	
	public Agent get(){
		return agent;
	}

}
