package org.jboss.jbpm.processbox.events.base;

import org.drools.event.process.ProcessEvent;

public abstract class ProcessBoxInstanceEvent implements ProcessBoxEvent {
	
	protected final ProcessEvent event;
	
	protected ProcessBoxInstanceEvent(ProcessEvent event) {
		this.event = event;
	}	
	
	public String getId() {
		return String.format("%d", event.getProcessInstance().getId());
	}

	public String getType() {
		return "ProcessBoxInstanceEvent";
	}

	public String getSubType() {
		return event.getClass().getSimpleName();
	}

	public ProcessEvent getEvent() {
		return this.event;
	}

}
