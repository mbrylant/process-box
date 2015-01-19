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
import java.util.Map;

import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.junit.Test;

public class SimpleTest2 extends ProcessBoxTest {
	
	
	private MockCustomWebServiceWorkItemHandler mockCustomWebServiceWorkItemHandler;
	
	String serviceName = "ABC";
	String serviceOperationName = "XYZ";
	Map<String, Object> serviceParams = new HashMap<String, Object>(); 
	Map<String, Object> contentMap = new HashMap<String, Object>(); 
	boolean isReturnTypeMap = false;
	
	@Test
	public void testSimpleProcess2() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException {
		
		contentMap.put("abort", true);
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("serviceParams", serviceParams);
		params.put("serviceName", serviceName);
		params.put("returnTypeMap", isReturnTypeMap);
		params.put("serviceOperationName", serviceOperationName);
		params.put("contentMap", contentMap);
		
//		Container container = new Container("org/jboss/jbpm/processbox/tests/simple.bpmn");
//		Container container = new Container("com/sample/simple.bpmn2");
		Container container = new Container("com/sample/CustomWebService.bpmn2");
		container.workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()));
//		container.workItemHandler("Custom Service", new ConfigurableMockWorkItemHandler(true));
		
		
		mockCustomWebServiceWorkItemHandler = new MockCustomWebServiceWorkItemHandler(container.getSession());
		mockCustomWebServiceWorkItemHandler.setExpectedIsReturnTypeMap(isReturnTypeMap);
		mockCustomWebServiceWorkItemHandler.setExpectedServiceName(serviceName);
		mockCustomWebServiceWorkItemHandler.setExpectedServiceOperationName(serviceOperationName);
		mockCustomWebServiceWorkItemHandler.setExpectedServiceParams(serviceParams);
		mockCustomWebServiceWorkItemHandler.setWebserviceResult("DONE!");
		mockCustomWebServiceWorkItemHandler.setRetryFailedResult(true);
		
		

		
		container.workItemHandler("CustomService",mockCustomWebServiceWorkItemHandler);
		
//		ProcessInstance processInstance = container.startProcess("process.simple");				
		ProcessInstance processInstance = container.startProcess("CustomService", params);				
//		ProcessBoxInstanceStartEvent processBoxInstanceStartEvent = (ProcessBoxInstanceStartEvent)waitFor(ProcessBoxInstanceStartEvent.class);
//		log.debug(String.format("Process Instance {%s} started in container", processBoxInstanceStartEvent.getId()));
		Thread.sleep(6000);
//		assertNodeTriggered(processInstance.getId(), "StartProcess", "CustomWebService", "End");
		assertProcessInstanceActive(processInstance.getId(), container.getSession());
		
//		waitFor(ProcessBoxTaskCreatedEvent.class);				
//		TaskSummary john = getTask("john", "en-UK");
//		taskService.start(john.getId(), "john");
//		taskService.complete(john.getId(), "john", null);		
//		waitFor(ProcessBoxTaskCompletedEvent.class);		
//		
//		waitFor(ProcessBoxTaskCreatedEvent.class);	
//		TaskSummary mary = getTask("mary", "en-UK");
//		taskService.start(mary.getId(), "mary");		
//		taskService.complete(mary.getId(), "mary", null);		
//		waitFor(ProcessBoxTaskCompletedEvent.class);
		
//		container.getSession().abortProcessInstance(processInstance.getId());
//		waitFor(ProcessBoxInstanceCompletedEvent.class);
		
//		List<TaskSummary> tasksAfetrAboort = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
//		assertEquals(1, tasksAfetrAboort.size());
		
		container.close();
		
	}
}


