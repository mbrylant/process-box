/*
 * (C) Copyright 2014 Mariusz Brylant (mbrylant@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jboss.jbpm.processbox.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.xml.DefaultSemanticModule;
import org.h2.tools.Server;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.listeners.ProcessBoxProcessListener;
import org.jboss.jbpm.processbox.listeners.ProcessBoxTaskListener;
import org.jboss.jbpm.processbox.listeners.other.DefaultAgendaEventListener;
import org.jboss.jbpm.processbox.listeners.other.DefaultWorkingMemoryEventListener;
import org.jboss.jbpm.processbox.model.DefaultProcessBoxNode;
import org.jboss.jbpm.processbox.model.Initialized;
import org.jboss.jbpm.processbox.model.NodeId;
import org.jboss.jbpm.processbox.model.ProcessBoxNode;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class ProcessBoxTest extends JbpmJUnitTestCase{
	
	protected Logger log = LoggerFactory.getLogger(ProcessBoxTest.class);
	
	protected TaskService ts;
	protected LocalTaskService taskService;	
	protected PoolingDataSource datasource;
	protected Server dbServer;
	
	protected static BlockingQueue<ProcessBoxEvent> queue = new LinkedBlockingQueue<ProcessBoxEvent>();
	
	@Before
	public void setup(){		
		dbServer = JBPMHelper.startH2Server();
        datasource = JBPMHelper.setupDataSource();
        ts = JBPMHelper.startTaskService();
        ts.addEventListener(new ProcessBoxTaskListener(queue));
        taskService = new LocalTaskService(ts);				
	}
	
	protected void waitFor() {
		ProcessBoxEvent event = null;
		try {
			event = queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		log.debug("Received event: {" + event + "}");
	}
	
	public static class UnexpectedProcessBoxEventException extends Exception {
		private static final long serialVersionUID = 2453218521987820847L;
		public UnexpectedProcessBoxEventException(String message) {
			super(message);
		}
	}
	
	public TaskSummary getTask (String userName, String encoding){
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(userName, encoding); 
		assertThat(tasks, is(not(empty())));
		TaskSummary task = tasks.get(0);
		return task;
	}
	
	public static class Container {
		
		private Map<String, ProcessInstance> instances = new HashMap<String, ProcessInstance>();
		private StatefulKnowledgeSession ksession;
		
		private KnowledgeRuntimeLogger logger;
		
		private org.drools.audit.KnowledgeRuntimeLoggerProviderImpl provider;
		private boolean INITIALIZED = false;
		
		public StatefulKnowledgeSession getSession() {
			return ksession;
		}

		public Container(String model) throws ContainerInitializationException{
			start(model);
			this.INITIALIZED = true;
		}
		
		public Container(List<String> models) throws ContainerInitializationException{
			start(models);
			this.INITIALIZED = true;
		}
		
		public Container(String... models) throws ContainerInitializationException{
			start(Arrays.asList(models));
			this.INITIALIZED = true;
		}
		
		public Container() {			
		}
		
		public void getWorkItemHanlders(){			
		}
				
		private void addDefaultListeners(){
			ksession.addEventListener(new ProcessBoxProcessListener(queue));		
			ksession.addEventListener(new DefaultAgendaEventListener());
			ksession.addEventListener(new DefaultWorkingMemoryEventListener());
		}
		
		public void close(){
			logger.close();
			ksession.dispose();
		}
		
		
		private Container start(List<String> models) throws ContainerInitializationException {	
			KnowledgeBuilderConfiguration conf =
					KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
//					 ((PackageBuilderConfiguration)conf).loadSemanticModule("ProcessBoxBPMN2SemanticModule.conf");
					 
//					 DefaultSemanticModule module = new DefaultSemanticModule("ProcessBoxBPMN2SemanticModule.conf");
			
			((PackageBuilderConfiguration)conf).initSemanticModules();
			
			
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
			for (String model: models){
				kbuilder.add(ResourceFactory.newClassPathResource(model), ResourceType.BPMN2);
			}
			KnowledgeBase kbase = kbuilder.newKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession,"trace.plog");
			addDefaultListeners();
			return this;
		}
		
		private Container start(String model) throws ContainerInitializationException{
			List<String> models = new ArrayList<String>();
			models.add(model);
			return this.start(models);
		}
		
		
		public Container workItemHandler(String taskName, WorkItemHandler handler) {
			this.ksession.getWorkItemManager().registerWorkItemHandler(taskName, handler);
			return this;			
		}


		public ProcessInstance startProcess(String processName) throws ContainerInitializationException {
			return this.startProcess(processName, null);			
		}
		
		
		public ProcessInstance startProcess(String processName, Map<String, Object> params) throws ContainerInitializationException {
			if (this.INITIALIZED == false) throw new ContainerInitializationException();
			ProcessInstance processInstance = ksession.startProcess(processName, params);
			instances.put(processName, processInstance);
			return processInstance;			
		}				
	}
	
	
	@Async
	public Future<ProcessBoxEvent> getAnyEvent() throws InterruptedException {	    
	        return new AsyncResult<ProcessBoxEvent>( this.queue.take());
	}
	
	@Async
	public Future<ProcessBoxEvent> getSpecificEvent(Events eventType) throws InterruptedException {	    
	        return new AsyncResult<ProcessBoxEvent>( waitUntilEvent(eventType));
	}
	
	protected ProcessBoxEvent waitUntilOrTimeout(Events eventType, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		Future<ProcessBoxEvent> eventFuture = getSpecificEvent(eventType);
		return eventFuture.get(timeout, unit);
		
	}
	
	
	protected ProcessBoxEvent waitUntilEvent(Events eventType) throws InterruptedException {
		ProcessBoxEvent event = null;
		event = queue.take();
		
		log.debug(String.format("Listening for events of type {%s}", eventType.toString()));
		
		while (event== null || !event.getSubType().equalsIgnoreCase(eventType.toString())) {
			log.debug(String.format("Skipping event {%s}", event.getDescription()));
			event = queue.take();
		}		
		log.debug(String.format("Matched event {%s}", event.getDescription()));
		return event;
	}
	
	
	protected ProcessBoxNode<Initialized> nodeId(String id){
		return DefaultProcessBoxNode.with(new NodeId(id));
	}
	
	
	
	@Async
	public Future<ProcessBoxEvent> getSpecificNode(ProcessBoxNode<Initialized> node) throws InterruptedException {	    
	        return new AsyncResult<ProcessBoxEvent>( waitUntil(node));
	}
	
	protected ProcessBoxEvent waitUntilOrTimeout(ProcessBoxNode<Initialized> node, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		Future<ProcessBoxEvent> eventFuture = getSpecificNode(node);
		return eventFuture.get(timeout, unit);
	}
	
	protected ProcessBoxEvent waitUntil(ProcessBoxNode<Initialized> node) throws InterruptedException {
		ProcessBoxEvent event = null;
		event = queue.take();
		
		while (event== null || !node.matches(event)) {
			log.debug("Skipping event: {" + event + "}");
			event = queue.take();
		}		
		log.debug(String.format("Received event {%s} matching node activation {%s}", event, node));
		return event;
	}
	
	protected ProcessBoxEvent waitFor(Class<? extends ProcessBoxEvent> eventClass) throws UnexpectedProcessBoxEventException, InterruptedException {		
		ProcessBoxEvent event = null;
		event = queue.take();
		
		if (event.getClass() != eventClass) {
			throw new UnexpectedProcessBoxEventException(String.format("Expecting ProcessBoxEvent of type {%s}, but got {%s} ", eventClass.getSimpleName(), event.getClass().getSimpleName()) );
		}
		
		log.debug("Received event: {" + event + "}");
		
		return event;
	}

}
