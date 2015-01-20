package org.jboss.jbpm.processbox.events.base;

import java.util.Map;

import org.jboss.jbpm.processbox.model.PBProperties;

public class ProcessBoxNodeInvocationEvent implements ProcessBoxInvocationEvent {
	
	protected String description;
	protected String subType;
	protected String type;
	protected String id;
	protected Map<String, Object> parameters;
	
	protected String describe(Map<String, Object> parameters) {
		StringBuilder description = new StringBuilder("");
		for (String key : parameters.keySet()) {			
			if (description.length() != 0) {
				description.append(", ");
			}
			description.append(String.format("{ %s => %s}", key, parameters.get(key) ));
		}
		return description.toString();
	}
	
	public ProcessBoxNodeInvocationEvent(String id, Map<String, Object> parameters) {
		
		this.description = String.format("Node {%s} invoked with parameters %s", id, describe(parameters));
		this.subType = "ProcessBoxNodeInvocationEvent";
		this.type = "ProcessBoxInvocationEvent";
		this.id = id;
		this.parameters = parameters;
	}

	public String getId() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}

	public String getSubType() {
		return this.subType;
	}

	public String getDescription() {
		return this.description;
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public PBProperties getProperties() {
		return new PBProperties(this.parameters);
	}

}
