package org.jboss.jbpm.processbox.model;

import org.drools.event.ProcessNodeTriggeredEventImpl;
import org.drools.event.process.ProcessEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.runtime.process.NodeInstance;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxInstanceEvent;
import org.jboss.jbpm.processbox.handlers.NodeMockWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProcessBoxNode<T extends State> extends ProcessBoxNode<T> {
	
	
	private Logger log = LoggerFactory
			.getLogger(DefaultProcessBoxNode.class);
	
	private NodeId id;
	private NodeMatcherType nodeMatcherType;
	
	private boolean isInstanceEvent(ProcessBoxEvent event){
		if (event instanceof ProcessBoxInstanceEvent) { return true; }
		return false;
	}
	
	private ProcessEvent getProcessEvent(ProcessBoxEvent event) {
		ProcessBoxInstanceEvent _evt = (ProcessBoxInstanceEvent)event;
		return _evt.getEvent();
	}
	
	public String value(){
		return this.id.getId();
	}

	@Override
	public boolean matches(ProcessBoxEvent event) {
		log.debug(String.format("Matching event {%s}", event.describe()));
		if (isInstanceEvent(event) == false ) {
			log.debug(String.format("Not an instance event, not matched {%s}", event.describe()));
			return false;
		}		
		if (!event.getSubType().equals("ProcessNodeTriggeredEventImpl")){ 
			log.debug(String.format("Not a node triggered event, not matched {%s}", event.describe()));
			return false; 
		}
		ProcessEvent processEvent = getProcessEvent(event);
		boolean isProcessNodeTriggeredEventImpl = processEvent instanceof ProcessNodeTriggeredEventImpl;
		if (isProcessNodeTriggeredEventImpl != true) {
			log.debug(String.format("Not implemented as ProcessNodeTriggeredEventImpl, not matched {%s}", processEvent.getClass().getSimpleName()));
			return false;
		}
		ProcessNodeTriggeredEvent evt = (ProcessNodeTriggeredEventImpl) getProcessEvent(event);

		NodeInstance node = evt.getNodeInstance();
		
		switch (nodeMatcherType) {
			case NODENAME:
				
				log.debug(String.format("Compariing by name expected {%s} received {%s}", id.getId(), node.getNodeName() ));
				
				if (id.getId().equalsIgnoreCase(node.getNodeName())) { 
					log.debug(String.format("Matched node on name {%s}", id.getId() ));
					return true; 
				}
				return false;
		
			default:
				return false;
		}				
	}
	
	public String matchedBy(){
		return this.nodeMatcherType.toString();
	}
	
	private DefaultProcessBoxNode(NodeId id){
		this.id = id;
		this.nodeMatcherType = NodeMatcherType.NODENAME;
	}
	
	public static DefaultProcessBoxNode<Initialized> with(NodeId id){
		return new DefaultProcessBoxNode<Initialized>(id);
	}
}
