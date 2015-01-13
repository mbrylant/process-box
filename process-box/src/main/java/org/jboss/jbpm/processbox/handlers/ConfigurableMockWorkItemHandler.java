package org.jboss.jbpm.processbox.handlers;

import java.util.HashMap;
import java.util.Map;

import org.drools.event.process.ProcessEvent;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.jboss.jbpm.processbox.listeners.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableMockWorkItemHandler implements WorkItemHandler {
	
	private Logger log = LoggerFactory.getLogger(ConfigurableMockWorkItemHandler.class);	
	
	private final boolean synchronous;
	
	private Map<String, Map<String, Object>> responses = new HashMap<String, Map<String, Object>>();

	public ConfigurableMockWorkItemHandler(final boolean sycnhronous) {
		this.synchronous = sycnhronous;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {		
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		log.debug("Executing ConfigurableMockWorkItemHandler with last process event: {%s}", EventBuffer.get().getEvent());						
		workItemManager.completeWorkItem(workItem.getId(), null);
		
		
//		String node = "";
//				
//		
//		if (synchronous == true) {
//			workItemManager.completeWorkItem(workItem.getId(), responses.get(node));
//		}
		
		
	}

}
