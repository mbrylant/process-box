package org.jboss.jbpm.processbox.model;

public enum NodeMatcherType {	
	NODEID ("by id"), NODENAME ("by Name");
	
	private String descr;
	
	NodeMatcherType(String descr){
		this.descr = descr;
	}
	
	public String toStriong(){
		return this.descr;
	}
}
