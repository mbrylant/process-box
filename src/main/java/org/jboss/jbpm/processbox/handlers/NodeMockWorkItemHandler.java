package org.jboss.jbpm.processbox.handlers;

import java.util.HashMap;
import java.util.Map;
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
import org.jboss.jbpm.processbox.events.base.ProcessBoxNodeInvocationEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxNodeReturnEvent;
import org.jboss.jbpm.processbox.model.PBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeMockWorkItemHandler implements WorkItemHandler {

	protected final BlockingQueue<ProcessBoxEvent> queue;
	private Logger log = LoggerFactory
			.getLogger(NodeMockWorkItemHandler.class);

	private static enum Mode {
		MULTI, SINGLE
	}

	private Mode mode = null;

	

	private final boolean synchronous;


	private Map<String, Map<String, PBProperties>> processSpecificOutcomes = new HashMap<String, Map<String, PBProperties>>();

	public NodeMockWorkItemHandler(BlockingQueue<ProcessBoxEvent> queue, final boolean synchronous) {
		this.queue = queue;
		this.synchronous = synchronous;
		this.withDefault = false;
	}

	public NodeMockWorkItemHandler(
			BlockingQueue<ProcessBoxEvent> queue, final boolean synchronous,
			final boolean withDefault) {
		this.queue = queue;
		this.synchronous = synchronous;
		this.withDefault = withDefault;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		workItemManager.abortWorkItem(workItem.getId());
	}

	private PBProperties defaultOutcome = new PBProperties();

	private boolean withDefault;

	public NodeMockWorkItemHandler withDefault(PBProperties def) {
		this.defaultOutcome = def;
		return this;
	}

	public NodeMockWorkItemHandler withDefault() {
		withDefault = true;
		return this;
	}
	
	// TODO: Add support for Synchronous and asynchronous per node configuration
	public NodeMockWorkItemHandler outcome(String process, String node,
			PBProperties outcomes) throws ProcessBoxConfigurationException {

		if (mode == null) {
			log.debug("No mode set, assuming multiprocess");
			mode = Mode.MULTI;
		}
		if (mode == Mode.MULTI) {
			log.debug("Adding multiprocess outcomes");
			if (this.processSpecificOutcomes.get(process) == null) {
				log.debug(String.format("Initiating processSpecificOutcomes storage for process {%s}", process));
				Map<String, PBProperties> nodeOutcomes = new HashMap<String, PBProperties>();
				this.processSpecificOutcomes.put(process, nodeOutcomes);
			}
			Map<String, PBProperties> nodeOutcomes = this.processSpecificOutcomes
					.get(process);
			log.debug(String.format("Populating process {%s} node {%s} outcomes {%s}", process, node, outcomes.describe()));
			nodeOutcomes.put(node, outcomes);
		} else {
			throw new ProcessBoxConfigurationException(
					"Should not mix SINGLE annd MULTI process configurations, aborting.");
		}

		return this;
	}

	private PBProperties getOutcome(String processId, String nodeName)
			throws OutcomeConfigurationException {
		log.debug(String.format(
				"Fetching outcome for process {%s} node {%s}",
				processId, nodeName));
		if (this.processSpecificOutcomes.get(processId) == null
				|| this.processSpecificOutcomes.get(processId).get(nodeName) == null) {
			if (this.withDefault == true) {
				log.debug(String.format(
						"Returning default outcome for process {%s} node {%s} outcome {%s}",
						processId, nodeName, defaultOutcome.describe()));
				return defaultOutcome;
			} else {
				log.error(String.format(
						"No outcome configured for process {%s} and node {%s}",
						processId, nodeName));
				queue.add(new DefaultProcessBoxEvent(
						nodeName,
						"ProcessBoxEvent",
						Events.ProcessBoxError.toString(),
						String.format(
								"No outcome configured for process {%s} and node {%s}",
								processId, nodeName)));
				throw new OutcomeConfigurationException(String.format(
								"No outcome configured for process {%s} and node {%s}",
								processId, nodeName));
			}
		}
		
		PBProperties outcome = this.processSpecificOutcomes.get(processId).get(nodeName);
		log.debug(String.format(
				"Returning configured outcome for process {%s} node {%s} outcome {%s}",
				processId, nodeName, outcome.describe()));
		return outcome;

	}

	private WorkItem workItem;
	private WorkItemManager workItemManager;
	private String processId;
	private String nodeName;

	public void completeWorkItem() {
		queue.add(new ProcessBoxNodeReturnEvent(processId, getOutcome(
				processId, nodeName).asMap()));
		this.workItemManager.completeWorkItem(this.workItem.getId(),
				getOutcome(processId, nodeName).asMap());
	}

	public void logOutcomes() {
		log.debug("Dumping outcomes");
		if ("true".equals(System.getProperty("processbox.debug.outcomes"))) {
			log.debug(String.format("Dumping outcomes indeed processes {%s}", this.processSpecificOutcomes.keySet()));
			for (String processId : this.processSpecificOutcomes.keySet()) {
				log.debug(String.format("Dumping outcomes indeed processes {%s} node {%s}", processId, this.processSpecificOutcomes.get(processId).keySet()));
				for (String node : this.processSpecificOutcomes.get(processId)
						.keySet()) {
					log.debug(String
							.format("Have outcome for process {%s} node {%s} outcome {%s}",
									this.processId, this.nodeName,
									this.processSpecificOutcomes.get(processId)
											.get(node).describe()));
				}

			}
		}
	}

	public void executeWorkItem(WorkItem workItem,
			WorkItemManager workItemManager) {

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

		if (synchronous == true) {
			PBProperties outcome = getOutcome(processId, nodeName);
			log.debug(String.format("Synchronous mode, returning outcome {%s}", outcome.describe()));
			queue.add(new ProcessBoxNodeReturnEvent(processId, outcome));
			workItemManager.completeWorkItem(workItem.getId(),outcome.asMap());
		} else {
			log.debug("Asynchronous mode, no outcome returned until completeWorkItem() is invoked");
		}
	}

}
