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

package org.jboss.jbpm.processbox.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.events.instance.typed.ProcessBoxInstanceCompletedEvent;
import org.jboss.jbpm.processbox.events.instance.typed.ProcessBoxInstanceStartEvent;
import org.jboss.jbpm.processbox.events.task.typed.ProcessBoxTaskCompletedEvent;
import org.jboss.jbpm.processbox.events.task.typed.ProcessBoxTaskCreatedEvent;
import org.jboss.jbpm.processbox.handlers.ConfigurableMockWorkItemHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

public class SimpleTest extends ProcessBoxTest {						
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleProcess() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException {
						
		
		Container container = new Container("org/jboss/jbpm/processbox/tests/simple.bpmn");
//		Container container = new Container("com/sample/simple.bpmn2");
		container.workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()));
//		container.workItemHandler("Service Task", new ConfigurableMockWorkItemHandler(true));
		ConfigurableMockWorkItemHandler customServiceHandler = new ConfigurableMockWorkItemHandler(true);
		
		Map<String, Object> serviceTask2outcome = new HashMap<String, Object>();
		String serviceName = "ABC";
		String serviceOperationName = "XYZ";
		Map<String, Object> serviceParams = new HashMap<String, Object>(); 		
		boolean isReturnTypeMap = false;
		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("abort", true);
		
		
		serviceTask2outcome.put("serviceParams", serviceParams);
		serviceTask2outcome.put("serviceName", serviceName);
		serviceTask2outcome.put("returnTypeMap", isReturnTypeMap);
		serviceTask2outcome.put("serviceOperationName", serviceOperationName);
		serviceTask2outcome.put("contentMap", contentMap);
		
		customServiceHandler.outcomeForNode("Service Task 2", serviceTask2outcome);
		
		container.workItemHandler("Custom Service", customServiceHandler);
		
		
					
		ProcessInstance processInstance = container.startProcess("process.simple", serviceTask2outcome);				
		waitUntilEvent(Events.AfterProcessStarted);
		
//		log.debug(String.format("Process Instance {%s} started in contaier", processBoxInstanceStartEvent.getId()));
		assertProcessInstanceActive(processInstance.getId(), container.getSession());
		
//		waitUntil(ProcessBoxTaskCreatedEvent.class);
		waitUntilEvent(Events.TaskStarted);
		TaskSummary john = getTask("john", "en-UK");
		taskService.start(john.getId(), "john");
		taskService.complete(john.getId(), "john", null);		
//		waitFor(ProcessBoxTaskCompletedEvent.class);
		waitUntilEvent(Events.TaskCompleted);
		
		waitUntilEvent(Events.TaskStarted);
//		waitUntil(ProcessBoxTaskCreatedEvent.class);
		
//		log.debug(String.format("HERE"));
		
//		waitUntilOrTimeout(nodeId("1"), 1, TimeUnit.MILLISECONDS);
		
		
		TaskSummary mary = getTask("mary", "en-UK");
		taskService.start(mary.getId(), "mary");		
//		taskService.complete(mary.getId(), "mary", null);		
//		waitFor(ProcessBoxTaskCompletedEvent.class);
		
		container.getSession().abortProcessInstance(processInstance.getId());
//		waitFor(ProcessBoxInstanceCompletedEvent.class);
		
		waitUntilEvent(Events.AfterProcessCompleted);
		
		
		List<TaskSummary> tasksAfetrAboort = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(1, tasksAfetrAboort.size());
		
		
		
		container.close();
		
	}

}


