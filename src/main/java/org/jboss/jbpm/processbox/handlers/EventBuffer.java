package org.jboss.jbpm.processbox.handlers;


public class EventBuffer {
	
	private Object lastEvent;
	
	private static EventBuffer buffer;
	
	public static EventBuffer get() {
		if (buffer == null) {buffer = new EventBuffer();}
		return buffer;
	}
	
	public void pushEvent(Object event) {lastEvent = event;}
	public Object getEvent() {return this.lastEvent;}

	private EventBuffer() { }

}
