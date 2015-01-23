package org.jboss.jbpm.processbox.events.base;

import org.drools.event.ProcessNodeTriggeredEventImpl;
import org.drools.event.process.ProcessEvent;
import org.drools.runtime.process.NodeInstance;

public abstract class ProcessBoxInstanceEvent implements ProcessBoxEvent {
	
	protected final ProcessEvent event;
	
	protected ProcessBoxInstanceEvent(ProcessEvent event) {
		this.event = event;
	}	
	
	public String getId() {
		return String.format("%d", event.getProcessInstance().getId());
	}

	public String getType() {
		return "ProcessBoxInstanceEvent";
	}

	public String getSubType() {
		return event.getClass().getSimpleName();
	}

	public ProcessEvent getEvent() {
		return this.event;
	}
	
	public String describe(){
		
		if (this.event instanceof ProcessNodeTriggeredEventImpl  ) {
			ProcessNodeTriggeredEventImpl processNodeTriggeredEvent = (ProcessNodeTriggeredEventImpl)event;
			NodeInstance node = processNodeTriggeredEvent.getNodeInstance();
			long nodeId = node.getNodeId();
			String nodeName = node.getNodeName();
			String source = processNodeTriggeredEvent.getSource().toString();
			return String.format("Event id {%s} type {%s} subtype {%s} generated from {%s} on node name {%s} id {%d}", this.getId(), this.getType(), this.getSubType(), source, nodeName, nodeId);
		}
		return String.format("Event id {%s} type {%s} subtype {%s}", this.getId(), this.getType(), this.getSubType());
	}

}
