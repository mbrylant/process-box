package org.jboss.jbpm.processbox.events.base;

public enum Events {
	BeforeNodeTriggered("ProcessNodeTriggeredEventImpl"), AfterNodeTriggered("ProcessNodeTriggeredEventImpl"),
	NodeTriggered("ProcessNodeTriggeredEventImpl"),
	BeforeNodeLeft("ProcessNodeLeftEventImpl"), AfterNodeLeft("ProcessNodeLeftEventImpl"),
	NodeLeft("ProcessNodeLeftEventImpl"),
	BeforeVariableChanged("ProcessVariableChangedEventImpl"), AfterVariableChanged("ProcessVariableChangedEventImpl"),	
	VariableChanged("ProcessVariableChangedEventImpl"),
	
	BeforeProcessStarted("ProcessStartedEventImpl"), 
	AfterProcessStarted("ProcessStartedEventImpl"),
	ProcessStarted("ProcessStartedEventImpl"), 	
	
	BeforeProcessCompleted("ProcessCompletedEventImpl"), 
	AfterProcessCompleted("ProcessCompletedEventImpl"),
	ProcessCompleted("ProcessCompletedEventImpl"),
	
	TaskStarted("TaskClaimedEvent"),
	TaskFailed("TaskFailedEvent"), 
	TaskSkipped("TaskSkippedEvent"),
	TaskCompleted("TaskCompletedEvent"),
	
	ProcessBoxNodeInvocationEvent("ProcessBoxNodeInvocationEvent"),
	ProcessBoxNodeReturnEvent("ProcessBoxNodeReturnEvent"),
	ProcessBoxError("ProcessBoxError");
	
	
	
		
	private String name;
	
	Events(String name) { this.name=name; }

    public String toString() { return name; }
	
}
