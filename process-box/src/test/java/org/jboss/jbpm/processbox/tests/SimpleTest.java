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
import java.util.concurrent.TimeoutException;

import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.handlers.ConfigurableMockWorkItemHandler;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

public class SimpleTest extends ProcessBoxTest {						
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleProcess() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException {
							
//		Container container = new Container("org/jboss/jbpm/processbox/tests/simple.bpmn");
//		Container container = new Container("org/jboss/jbpm/processbox/tests/simple.bpmn", "org/jboss/jbpm/processbox/tests/subprocess.bpmn2");
//		Container container = new Container("com/sample/simple.bpmn2");
		
		ConfigurableMockWorkItemHandler customServiceHandler = new ConfigurableMockWorkItemHandler(true);

		Map<String, Object> serviceTask2outcome = new HashMap<String, Object>();
		final String serviceName = "ABC";
		final String serviceOperationName = "XYZ";
		final Map<String, Object> serviceParams = new HashMap<String, Object>(); 		
		boolean isReturnTypeMap = false;
		final Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("abort", true);
				
		serviceTask2outcome.put("serviceParams", serviceParams);
		serviceTask2outcome.put("serviceName", serviceName);
		serviceTask2outcome.put("returnTypeMap", isReturnTypeMap);
		serviceTask2outcome.put("serviceOperationName", serviceOperationName);
		serviceTask2outcome.put("contentMap", contentMap);
		
		customServiceHandler.outcomeForNode("Service Task 2", serviceTask2outcome);
		
		
		Container container = new Container();
		ProcessInstance processInstance = container.resource(process("org/jboss/jbpm/processbox/tests/simple.bpmn"))
				 .resource(subProcess("Mock Sub Process", "sub.process"))		
				 .init()
				 .workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()))
				 .workItemHandler("Custom Service", customServiceHandler)
				 .startProcess("process.simple", serviceTask2outcome);
												
		waitUntilEvent(Events.ProcessStarted);
		
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
		
		assertNodeTriggered(processInstance.getId(), "BLAH");
		
		
		
		container.close();
		
	}

}


