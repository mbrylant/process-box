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

import org.drools.SystemEventListenerFactory;
import org.drools.process.core.Work;
import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxConfigurationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jboss.jbpm.processbox.core.ProcessBoxTest.ResultStackSubProcessMockBuilder;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.events.base.ProcessBoxNodeInvocationEvent;
import org.jboss.jbpm.processbox.handlers.NodeMockWorkItemHandler;
import org.jboss.jbpm.processbox.handlers.ResultStackMockWorkItemHandler;
import org.jboss.jbpm.processbox.model.PBProperties;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SimpleTest extends ProcessBoxTest {
	
	@Test
	public void testPerentChild() throws ContainerInitializationException, InterruptedException, ProcessBoxConfigurationException {
		
		ResultStackSubProcessMockBuilder subProcessBuilder = processMockStack("reusableSubProcess.Child", "org.plugtree.training.jbpm.reusablesubprocesschild", "defaultPackage");
		Container container = new Container();
		ProcessInstance processInstance = 
				container
					.resource(process("reusableSubProcess.Parent", "org.plugtree.training.jbpm.reusablesubprocessparent", "com/sample/reusableSubProcess-Parent.bpmn"))
//					.resource(processMock("reusableSubProcess.Child", "org.plugtree.training.jbpm.reusablesubprocesschild", "defaultPackage", new PBProperties()))
					.resource(subProcessBuilder.getResource())
					.init()
					.workItemHandler("Custom Service", new NodeMockWorkItemHandler(queue, true).withDefault())
					.workItemHandler(subProcessBuilder.getMockId(), subProcessBuilder.getHandler().withDefault())
					.startProcess("org.plugtree.training.jbpm.reusablesubprocessparent", null);
		
		waitUntilEvent(Events.ProcessBoxNodeInvocationEvent);
		waitUntilEvent(Events.ProcessCompleted);				
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleProcess() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException, ProcessBoxConfigurationException {
		
		System.setProperty("processbox.debug.outcomes", "true");
		
		PBProperties serviceTask2result = 
				results()
					.result("serviceName", "ABC")
					.result("serviceParams", map())
					.result("contentMap", ImmutableMap.of("abort", true))
					.result("returnTypeMap", false)
					.result("serviceOperationName", "XYZ");
									
//		NodeMockWorkItemHandler customServiceHandler = new NodeMockWorkItemHandler(queue, true).withDefault();
//		customServiceHandler.outcome("process.simple", "Service Task 2", serviceTask2result);		
		
		ResultStackMockWorkItemHandler customServiceHandler = 
				new ResultStackMockWorkItemHandler(queue)
					.withDefault()
					.outcome(serviceTask2result);
		
		ResultStackSubProcessMockBuilder subProcessBuilder = processMockStack("Mock Sub Process", "sub.process", "defaultPackage");
		
		Container container = new Container();
		ProcessInstance processInstance = 
				container
					.resource(process("Simple Process", "process.simple", "org/jboss/jbpm/processbox/tests/simple.bpmn"))
//					.resource(processMock("Mock Sub Process", "sub.process", "defaultPackage", new PBProperties()))
					.resource(subProcessBuilder.getResource())		
					.init()
					.workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()))
					.workItemHandler("Custom Service", customServiceHandler)
					.workItemHandler(subProcessBuilder.getMockId(), customServiceHandler)
					.startProcess("process.simple", serviceTask2result.asMap());
												
		waitUntilEvent(Events.ProcessStarted);
		
		assertProcessInstanceActive(processInstance.getId(), container.getSession());
		
		waitUntilEvent(Events.TaskStarted);
		TaskSummary john = getTask("john", "en-UK");
		
		System.err.println(john.getStatus().toString());
		
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		
		TaskClient client = new TaskClient(new MinaTaskClientConnector("client 1", new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 9123);
		
		
//		taskService.start(john.getId(), "john");
		
		client.start( john.getId(), "john", responseHandler );

		responseHandler.waitTillDone(1000); 
		
		

		
		
		
		
		TaskSummary john2 = getTask("john", "en-UK");
		System.err.println(john2.getStatus().toString());
		
		taskService.complete(john.getId(), "john", null);
		
		waitUntilEvent(Events.TaskCompleted);
		
		
		
//		waitUntil(nodeName("Call Sub Process"));
		waitUntilNode(nodeName(subProcessBuilder.getMockId()));
		
		waitForVariable("contentMap");
		
		//waitUntilEvent(Events.ProcessBoxNodeInvocationEvent);				
		//waitUntilEvent(Events.ProcessBoxNodeReturnEvent);		
		waitUntilEvent(Events.TaskStarted);
		
		
				
		TaskSummary mary = getTask("mary", "en-UK");
		taskService.start(mary.getId(), "mary");	
		

		container.abort(processInstance);	
		waitUntilEvent(Events.AfterProcessCompleted);		
		
		List<TaskSummary> tasksAfetrAboort = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(1, tasksAfetrAboort.size());
		
			
//		container.close();
		
	}

}


