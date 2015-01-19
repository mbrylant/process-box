package org.jboss.jbpm.processbox.events.instance.typed;

import org.drools.event.process.ProcessEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxInstanceEvent;

public class ProcessBoxNodeTriggeredEvent extends ProcessBoxInstanceEvent {
	
	public ProcessBoxNodeTriggeredEvent(ProcessEvent event) {		
		super(event);
	}

}
