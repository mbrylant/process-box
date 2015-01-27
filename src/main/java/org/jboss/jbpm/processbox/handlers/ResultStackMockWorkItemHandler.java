package org.jboss.jbpm.processbox.handlers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.drools.event.ProcessNodeTriggeredEventImpl;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jboss.jbpm.processbox.core.OutcomeConfigurationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest.ProcessBoxEventQueue;
import org.jboss.jbpm.processbox.events.base.DefaultProcessBoxEvent;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxNodeInvocationEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxNodeReturnEvent;
import org.jboss.jbpm.processbox.model.PBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultStackMockWorkItemHandler implements WorkItemHandler {
	
	protected final ProcessBoxEventQueue queue;
	private Logger log = LoggerFactory
			.getLogger(ResultStackMockWorkItemHandler.class);
	
	private final Deque<PBProperties> propertiesStack;
	
	private final boolean synchrnonous = true;
	
	private boolean hasDefault;
	private PBProperties defaultOutcome;
	private int count=0; 
	
	private WorkItem workItem;
	private WorkItemManager workItemManager;
	private String processId;
	private String nodeName;
	
	public void completeWorkItem() {
		queue.add(new ProcessBoxNodeReturnEvent(processId, getNextOutcome(
				processId, nodeName).asMap()));
		this.count++;
		this.workItemManager.completeWorkItem(this.workItem.getId(),
				getNextOutcome(processId, nodeName).asMap());
	}
	
	
	public ResultStackMockWorkItemHandler(ProcessBoxEventQueue queue) {
		
		this.queue = queue;
		this.propertiesStack = new ArrayDeque<PBProperties>();
		this.hasDefault = false;
	}
	
	
	public ResultStackMockWorkItemHandler(ProcessBoxEventQueue queue, Deque<PBProperties> propertiesStack) {
		
		this.queue = queue;
		this.propertiesStack = propertiesStack;
		this.hasDefault = false;
	}
	
	public ResultStackMockWorkItemHandler withDefault(){
		this.defaultOutcome = new PBProperties();
		this.hasDefault = true;
		return this;
	}
	
	public ResultStackMockWorkItemHandler withDefault(PBProperties properties){
		this.defaultOutcome = properties;
		this.hasDefault = true;
		return this;
	}	
	
	public ResultStackMockWorkItemHandler outcome(PBProperties properties) {
		this.propertiesStack.add(properties);
		return this;
	}
	
	private PBProperties getNextOutcome(String processId, String nodeName) {
		log.debug(String.format(
				"Fetching next available outcome for process {%s} node {%s}",
				processId, nodeName));
		if (propertiesStack.isEmpty() == true) {
			if (hasDefault == true) {
				log.debug(String.format(
						"Returning default outcome for process {%s} node {%s} outcome {%s}",
						processId, nodeName, defaultOutcome.describe()));
				return defaultOutcome;
			} else {
				log.error(String.format(
						"No outcome available for process {%s} and node {%s}",
						processId, nodeName));
				queue.add(new DefaultProcessBoxEvent(
						nodeName,
						"ProcessBoxEvent",
						Events.ProcessBoxError.toString(),
						String.format(
								"No outcome available for process {%s} and node {%s}",
								processId, nodeName)));
				throw new OutcomeConfigurationException(String.format(
								"No outcome available for process {%s} and node {%s}",
								processId, nodeName));
			}
		}
		
		PBProperties outcome = propertiesStack.pollFirst();
		log.debug(String.format(
				"Returning next available outcome for process {%s} node {%s} outcome {%s}",
				processId, nodeName, outcome.describe()));
		return outcome;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		this.workItem = workItem;
		this.workItemManager = workItemManager;

		Object evt = EventBuffer.get().getEvent();
				
		if (!(evt instanceof ProcessNodeTriggeredEventImpl)) {			
			try {
				throw new ProcessBoxNodeSignalingException(
						"Last event received in the work item handler is not ProcessNodeTriggeredEventImpl");
			} catch (ProcessBoxNodeSignalingException e) {
				throw new RuntimeException(e);
			}
		}
		
		ProcessNodeTriggeredEvent event = (ProcessNodeTriggeredEventImpl) evt;

		NodeInstance node = event.getNodeInstance();
		String nodeName = node.getNodeName();
		String processId = node.getProcessInstance().getProcessId();

		this.nodeName = nodeName;
		this.processId = processId;
				
		queue.add(new ProcessBoxNodeInvocationEvent(processId, workItem.getParameters()));
				
		log.debug(String.format("Executing for node: {%d} {%s}",
				node.getNodeId(), node.getNodeName()));

		if (this.synchrnonous == true) {
			
			PBProperties outcome = getNextOutcome(processId, nodeName);
			log.debug(String.format("Synchronous mode, returning outcome {%s}", outcome.describe()));
			queue.add(new ProcessBoxNodeReturnEvent(processId, outcome));
			
			WorkflowProcessInstance instance = node.getProcessInstance();
			for (String variable: outcome.asMap().keySet()){
				instance.setVariable(variable, outcome.get(variable));
			}
			this.count++;
			workItemManager.completeWorkItem(workItem.getId(),null);
		} else {
			log.debug("Asynchronous mode, no outcome returned until completeWorkItem() is invoked");
		}
		
	}

	@Override
	public void abortWorkItem(WorkItem paramWorkItem, WorkItemManager paramWorkItemManager) {		
	}

}
