package org.jboss.jbpm.processbox.handlers;

import java.util.LinkedList;
import java.util.List;

public class EventLog {
	
	private static EventLog eventLog;
	
	private List<Object> events;
	
	public static EventLog get() {
		if (eventLog == null) {eventLog = new EventLog();}
		return eventLog;
	}
	
	public void logEvent(Object event) {
			synchronized(eventLog) {
				this.events.add(event);
			}
		}
	public List<Object> getEvents() {return this.events;}

	private EventLog() {
		this.events = new LinkedList<Object>();
	}
	
	public void print(){
		for (Object event: events){
			System.out.println(String.format("Event => %s", event));
		}
	}
	
	

}
