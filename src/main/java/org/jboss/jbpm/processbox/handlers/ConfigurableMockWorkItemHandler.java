package org.jboss.jbpm.processbox.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.drools.event.ProcessNodeTriggeredEventImpl;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.jboss.jbpm.processbox.core.OutcomeConfigurationException;
import org.jboss.jbpm.processbox.core.ProcessBoxConfigurationException;
import org.jboss.jbpm.processbox.events.base.DefaultProcessBoxEvent;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.instance.DefaultProcessBoxInstanceEvent;
import org.jboss.jbpm.processbox.model.PBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableMockWorkItemHandler implements WorkItemHandler {
	
	protected final BlockingQueue<ProcessBoxEvent> queue;
	
	private static enum Mode {
		MULTI, SINGLE
	}
	
	private Mode mode = null;
	
	private Logger log = LoggerFactory.getLogger(ConfigurableMockWorkItemHandler.class);	
	
	private final boolean synchronous;
	
	private Map<String, Map<String, Object>> outcomes = new HashMap<String, Map<String, Object>>();
	
	private Map<String, Map<String, PBProperties>> processSpecificOutcomes = new HashMap<String, Map<String, PBProperties>>();
	
	

	public ConfigurableMockWorkItemHandler(BlockingQueue<ProcessBoxEvent> queue, final boolean sycnhronous) {
		this.queue = queue;
		this.synchronous = sycnhronous;
		this.withDefault = false;
	}
	
	public ConfigurableMockWorkItemHandler(BlockingQueue<ProcessBoxEvent> queue, final boolean sycnhronous, final boolean withDefault) {
		this.queue = queue;
		this.synchronous = sycnhronous;
		this.withDefault = withDefault;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		workItemManager.abortWorkItem(workItem.getId());
	}
	
	public ConfigurableMockWorkItemHandler outcome(String nodeName, Map<String, Object> outcomes) throws ProcessBoxConfigurationException{
		if (mode == null) {mode = Mode.SINGLE;}
		else if (mode == Mode.SINGLE) {
			this.outcomes.put(nodeName, outcomes);
		}
		else {
			throw new ProcessBoxConfigurationException("Should not mix SINGLE annd MULTI process configurations, aborting.");
		}
		return this;
	}
	
	private PBProperties defaultOutcome = new PBProperties();

	private boolean withDefault;
	
	public ConfigurableMockWorkItemHandler withDefault(PBProperties def){
		this.defaultOutcome = def;
		return this;
	}
	
	public ConfigurableMockWorkItemHandler withDefault(){
		withDefault = true;
		return this;
	}
	
	public ConfigurableMockWorkItemHandler outcome(String process, String node, PBProperties outcomes) throws ProcessBoxConfigurationException {
		
		if (mode == null) {mode = Mode.MULTI;}
		else if (mode == Mode.MULTI) {
			if (this.processSpecificOutcomes.get(process) == null ) {
				Map<String, PBProperties> nodeOutcomes = new HashMap<String, PBProperties>();
				this.processSpecificOutcomes.put(process, nodeOutcomes);
			}
			Map<String, PBProperties> nodeOutcomes = this.processSpecificOutcomes.get(process);
			nodeOutcomes.put(node, outcomes);
		}
		else {
			throw new ProcessBoxConfigurationException("Should not mix SINGLE annd MULTI process configurations, aborting.");
		}
		
		
		return this;
	}
	
	
	private PBProperties getOutcome(String processId, String nodeName) throws OutcomeConfigurationException {
		if (this.processSpecificOutcomes.get(processId) == null || this.processSpecificOutcomes.get(processId).get(nodeName) == null) {
			if (this.withDefault == true) { 
				return defaultOutcome; 
			} else {
				log.error(String.format("No outcome configured for process {%s} and node{%s}", processId, nodeName));
//				throw new OutcomeConfigurationException(String.format("No outcome configured for process {%s} and node{%s}", processId, nodeName)) ;
				queue.add(new DefaultProcessBoxEvent(nodeName, "ProcessBoxEvent", Events.ProcessBoxError.toString(), String.format("No outcome configured for process {%s} and node{%s}", processId, nodeName)));
//				Runtime.getRuntime().exit(1);
			}			
		}
		return this.processSpecificOutcomes.get(processId).get(nodeName);

		
	}
	
	private WorkItem workItem; 
	private WorkItemManager workItemManager;
	private String processId;
	private String nodeName;
	
	public void completeWorkItem(){
		this.workItemManager.completeWorkItem(this.workItem.getId(), getOutcome(processId, nodeName).asMap());
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
		this.workItem = workItem;
		this.workItemManager = workItemManager;

				
		Object evt = EventBuffer.get().getEvent();
										
		if (! (evt instanceof ProcessNodeTriggeredEventImpl) ) {
			try {
				throw new ProcessBoxNodeSignalingException("Last event received in the work item handler is not ProcessNodeTriggeredEventImpl");
			} catch (ProcessBoxNodeSignalingException e) {
				throw new RuntimeException(e);
			}
		}
		
		ProcessNodeTriggeredEvent event = (ProcessNodeTriggeredEventImpl)evt;
//		Environment env = event.getKnowledgeRuntime().getEnvironment();
//		MapGlobalResolver globals = (MapGlobalResolver) event.getKnowledgeRuntime().getGlobals();
//		
//		for (Entry<?, ?> entry: globals.getGlobals()) {
//			log.debug(String.format("Global {%s} = {%s}", entry.getKey(), entry.getValue()));
//		}
		
			
		NodeInstance node = event.getNodeInstance();
		String nodeName = node.getNodeName();
		String processId = node.getProcessInstance().getProcessId();
		
		this.nodeName = nodeName;
		this.processId = processId;
	
		log.debug(String.format("Executing for node: {%d} {%s}", node.getNodeId(), node.getNodeName()));
		
		if (synchronous) {
			log.debug("Synchronous mode, returning outcome");
			workItemManager.completeWorkItem(workItem.getId(), getOutcome(processId, nodeName).asMap());
		} else {
			log.debug("Asynchronous mode, no outcome returned until completeWorkItem() is invoked");
		}
		
		
		
//		String node = "";
//				
//		
//		if (synchronous == true) {
//			workItemManager.completeWorkItem(workItem.getId(), responses.get(node));
//		}
		
		
	}

}
