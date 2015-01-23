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
	
	public String describe(){
		String source = event.getSource().toString();
		String name = event.getUserId();
		long taskId = event.getTaskId();
		return String.format("Task Event id {%s} type {%s} subtype {%s} generated from {%s} for username name {%s} task id {%d}", this.getId(), this.getType(), this.getSubType(), source, name, taskId);
}
		

}
