package org.jboss.jbpm.processbox.events.task.typed;

import org.jboss.jbpm.processbox.events.base.ProcessBoxTaskEvent;
import org.jbpm.task.event.TaskUserEvent;

public class ProcessBoxTaskSkippedEvent extends ProcessBoxTaskEvent {

	public ProcessBoxTaskSkippedEvent(TaskUserEvent event) {
		super(event);
	}

}
