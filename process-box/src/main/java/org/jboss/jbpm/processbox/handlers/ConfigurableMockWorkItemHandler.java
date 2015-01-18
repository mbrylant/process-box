package org.jboss.jbpm.processbox.handlers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.base.MapGlobalResolver;
import org.drools.event.ProcessNodeTriggeredEventImpl;
import org.drools.event.process.ProcessEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.Globals;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.listeners.other.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableMockWorkItemHandler implements WorkItemHandler {
	
	private Logger log = LoggerFactory.getLogger(ConfigurableMockWorkItemHandler.class);	
	
	private final boolean synchronous;
	
	private Map<String, Map<String, Object>> outcomes = new HashMap<String, Map<String, Object>>();

	public ConfigurableMockWorkItemHandler(final boolean sycnhronous) {
		this.synchronous = sycnhronous;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {		
	}
	
	public ConfigurableMockWorkItemHandler outcomeForNode(String nodeName, Map<String, Object> outcome){
		this.outcomes.put(nodeName, outcome);
		return this;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
				
		Object evt = EventBuffer.get().getEvent();
										
		if (! (evt instanceof ProcessNodeTriggeredEventImpl) ) {
			try {
				throw new ProcessBoxNodeSignalingException("Last event received in the work item handler is not ProcessNodeTriggeredEventImpl");
			} catch (ProcessBoxNodeSignalingException e) {
				throw new RuntimeException(e);
			}
		}
		
		ProcessNodeTriggeredEvent event = (ProcessNodeTriggeredEventImpl)evt;
		Environment env = event.getKnowledgeRuntime().getEnvironment();
		MapGlobalResolver globals = (MapGlobalResolver) event.getKnowledgeRuntime().getGlobals();
		
		for (Entry entry: globals.getGlobals()) {
			log.debug(String.format("Global {%s} = {%s}", entry.getKey(), entry.getValue()));
		}
		
		
//		PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
//		WorkItemInfo workItemInfo = context.findWorkItemInfo( workItem.getId() );
//		Date __dttm__ = workItemInfo.getCreationDate();
//		
		NodeInstance node = event.getNodeInstance();
	
//		log.debug(String.format("Executing ConfigurableMockWorkItemHandler with last process event: {%s}", event.getClass().getName()));
		log.debug(String.format("Executing for node: {%d} {%s}", node.getNodeId(), node.getNodeName()));
		
//		if (synchronous) {
			
			workItemManager.completeWorkItem(workItem.getId(), this.outcomes.get(node.getNodeName()));
//		}
		
		
//		String node = "";
//				
//		
//		if (synchronous == true) {
//			workItemManager.completeWorkItem(workItem.getId(), responses.get(node));
//		}
		
		
	}

}
