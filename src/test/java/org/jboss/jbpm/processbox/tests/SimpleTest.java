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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxConfigurationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.handlers.ConfigurableMockWorkItemHandler;
import org.jboss.jbpm.processbox.model.PBProperties;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SimpleTest extends ProcessBoxTest {
	
	@Test
	public void testPerentChild() throws ContainerInitializationException, InterruptedException, ProcessBoxConfigurationException {
		
		Container container = new Container();
		ProcessInstance processInstance = 
				container
					.resource(process("reusableSubProcess.Parent", "org.plugtree.training.jbpm.reusablesubprocessparent", "com/sample/reusableSubProcess-Parent.bpmn"))
					.resource(processMock("reusableSubProcess.Child", "org.plugtree.training.jbpm.reusablesubprocesschild"))
					.init()
					.workItemHandler("Custom Service", new ConfigurableMockWorkItemHandler(queue, true).withDefault())
					.startProcess("org.plugtree.training.jbpm.reusablesubprocessparent", null);
		
		waitUntilEvent(Events.ProcessBoxNodeInvocationEvent);
		waitUntilEvent(Events.ProcessCompleted);				
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleProcess() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException, ProcessBoxConfigurationException {
		
		System.setProperty("processbox.debug.outcomes", "true");
									
		ConfigurableMockWorkItemHandler customServiceHandler = new ConfigurableMockWorkItemHandler(queue, true).withDefault();
				
		PBProperties serviceTask2result = 
				results()
					.result("serviceName", "ABC")
					.result("serviceParams", map())
					.result("contentMap", ImmutableMap.of("abort", true))
					.result("returnTypeMap", false)
					.result("serviceOperationName", "XYZ");
		
		customServiceHandler.outcome("process.simple", "Service Task 2", serviceTask2result);				
		
		Container container = new Container();
		ProcessInstance processInstance = 
				container
					.resource(process("Simple Process", "process.simple", "org/jboss/jbpm/processbox/tests/simple.bpmn"))
					.resource(processMock("Mock Sub Process", "sub.process"))		
					.init()
					.workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()))
					.workItemHandler("Custom Service", customServiceHandler)
					.startProcess("process.simple", serviceTask2result.asMap());
												
		waitUntilEvent(Events.ProcessStarted);
		
		assertProcessInstanceActive(processInstance.getId(), container.getSession());
		
		waitUntilEvent(Events.TaskStarted);
		TaskSummary john = getTask("john", "en-UK");
		taskService.start(john.getId(), "john");
		taskService.complete(john.getId(), "john", null);		
		waitUntilEvent(Events.TaskCompleted);
		
		waitUntilEvent(Events.ProcessBoxNodeInvocationEvent);
		
		waitUntilEvent(Events.ProcessBoxNodeReturnEvent);
		
		waitUntilEvent(Events.TaskStarted);		
		
		
		TaskSummary mary = getTask("mary", "en-UK");
		taskService.start(mary.getId(), "mary");		

		container.getSession().abortProcessInstance(processInstance.getId());	
		waitUntilEvent(Events.AfterProcessCompleted);		
		
		List<TaskSummary> tasksAfetrAboort = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(1, tasksAfetrAboort.size());
		
			
		container.close();
		
	}

}


