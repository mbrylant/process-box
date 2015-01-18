package org.jboss.jbpm.processbox.events.task.typed;

import org.jboss.jbpm.processbox.events.base.ProcessBoxTaskEvent;
import org.jbpm.task.event.TaskUserEvent;

public class ProcessBoxTaskFailedEvent extends ProcessBoxTaskEvent {
		
	public ProcessBoxTaskFailedEvent(TaskUserEvent event) {
		super(event);
	}

}
