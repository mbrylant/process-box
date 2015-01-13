package org.jboss.jbpm.processbox.handlers;

import org.drools.event.process.ProcessEvent;

public class EventBuffer {
	
	private ProcessEvent lastEvent;
	
	private static EventBuffer buffer;
	
	static EventBuffer get() {
		if (buffer == null) {buffer = new EventBuffer();}
		return buffer;
	}
	
	public void pushEvent(ProcessEvent event) {lastEvent = event;}
	public ProcessEvent getEvent() {return this.lastEvent;}

	private EventBuffer() {
		// TODO Auto-generated constructor stub
	}

}
