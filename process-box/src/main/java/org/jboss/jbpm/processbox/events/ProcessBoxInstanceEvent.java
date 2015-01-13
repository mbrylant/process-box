package org.jboss.jbpm.processbox.events;

import org.drools.event.process.ProcessEvent;

public interface ProcessBoxInstanceEvent extends ProcessBoxEvent {
	
	public ProcessEvent getEvent();

}
