package org.jboss.jbpm.processbox.listeners;

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWorkingMemoryEventListener implements org.drools.event.WorkingMemoryEventListener, org.drools.event.rule.WorkingMemoryEventListener {

	private Logger log = LoggerFactory.getLogger(DefaultWorkingMemoryEventListener.class);
	

	public void objectInserted(org.drools.event.ObjectInsertedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectRetracted(org.drools.event.ObjectRetractedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectUpdated(org.drools.event.ObjectUpdatedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectInserted(ObjectInsertedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectRetracted(ObjectRetractedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectUpdated(ObjectUpdatedEvent arg0) {
		log.debug(arg0.toString());
		
	}

}
