package org.jboss.jbpm.processbox.events.instance;

import org.drools.event.process.ProcessEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxInstanceEvent;

public class DefaultProcessBoxInstanceEvent extends ProcessBoxInstanceEvent {
	
	public DefaultProcessBoxInstanceEvent(ProcessEvent event){
		super(event);
	}

}
