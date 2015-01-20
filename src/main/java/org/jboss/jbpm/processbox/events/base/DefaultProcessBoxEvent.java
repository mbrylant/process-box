package org.jboss.jbpm.processbox.events.base;

public class DefaultProcessBoxEvent implements ProcessBoxEvent {

	protected final String description;
	protected final String subType;
	protected final String type;
	protected final String id;
	
	
	public DefaultProcessBoxEvent(String id, String type, String subType, String description) {
		this.description = description;
		this.subType = subType;
		this.type = type;
		this.id = id;
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

}
