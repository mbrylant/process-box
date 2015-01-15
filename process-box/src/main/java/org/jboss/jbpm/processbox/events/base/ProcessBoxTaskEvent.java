package org.jboss.jbpm.processbox.events.base;

import org.jbpm.task.event.TaskUserEvent;

public abstract class ProcessBoxTaskEvent implements ProcessBoxEvent {
	
	protected final TaskUserEvent event;
	
	protected ProcessBoxTaskEvent(TaskUserEvent event){
		this.event = event;
	}
	
	public TaskUserEvent getEvent() { return this.event; }
	
	public String getId() {		
		return String.format("%d", event.getTaskId());
	}

	public String getType() {
		return "ProcessBoxTaskEvent";
	}

	public String getSubType() {
		return event.getClass().getSimpleName();
	}

}
