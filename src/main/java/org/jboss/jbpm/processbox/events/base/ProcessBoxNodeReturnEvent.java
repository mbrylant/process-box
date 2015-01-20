package org.jboss.jbpm.processbox.events.base;

import java.util.Map;

import org.jboss.jbpm.processbox.model.PBProperties;

public class ProcessBoxNodeReturnEvent extends ProcessBoxNodeInvocationEvent {

	public ProcessBoxNodeReturnEvent(String id, Map<String, Object> parameters) {
		super(id, parameters);
		this.description = String.format("Node {%s} invoked with parameters %s", id, describe(parameters));
		this.subType = "ProcessBoxNodeReturnEvent";
		this.type = "ProcessBoxInvocationEvent";
		this.id = id;
		this.parameters = parameters;
	}
	
	public ProcessBoxNodeReturnEvent(String id, PBProperties parameters) {
		super(id, parameters);
		this.description = String.format("Node {%s} invoked with parameters %s", id, parameters.describe());
		this.subType = "ProcessBoxNodeReturnEvent";
		this.type = "ProcessBoxInvocationEvent";
		this.id = id;
		this.parameters = parameters.asMap();
	}

}
