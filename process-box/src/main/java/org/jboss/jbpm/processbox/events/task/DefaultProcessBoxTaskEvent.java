package org.jboss.jbpm.processbox.events.task;

import org.jboss.jbpm.processbox.events.base.ProcessBoxTaskEvent;
import org.jbpm.task.event.TaskUserEvent;

public class DefaultProcessBoxTaskEvent extends ProcessBoxTaskEvent {

	public DefaultProcessBoxTaskEvent(TaskUserEvent event) {
		super(event);
	}

}
