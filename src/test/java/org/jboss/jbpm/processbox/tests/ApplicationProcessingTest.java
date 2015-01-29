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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxConfigurationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jboss.jbpm.processbox.events.base.Events;
import org.jboss.jbpm.processbox.handlers.ResultStackMockWorkItemHandler;
import org.jboss.jbpm.processbox.model.PBProperties;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.Test;

public class ApplicationProcessingTest extends ProcessBoxTest {

	
	@SuppressWarnings("deprecation")
	@Test
	public void testApplicationProcessSAF_InFlightServiceFail() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException, ProcessBoxConfigurationException {
		
		System.setProperty("processbox.debug.outcomes", "true");
		
		PBProperties processInvocationParameters = results().result("brn", "10000").result("applicationId", "12345");
		
		PBProperties serviceTask1Result = 
				results()
					.result("retryFailed", true)
					.result("serviceReturn", new HashMap<String, Object>());

		ResultStackMockWorkItemHandler customServiceHandler = 
				new ResultStackMockWorkItemHandler(queue)
					.withDefault()
					.outcome(serviceTask1Result);
		
		ResultStackSubProcessMockBuilder subProcessBuilder = processMockStack("Sample Process", "CustomService", "uk.gov.scotland.afrc.workmanagement.applications");
		
		Container container = new Container();
		WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
				container
					.resource(process("SAF Application Process", "application-process-saf", "com/sample/Application-Processing-SAF-Process.bpmn"))
					.resource(subProcessBuilder.getResource())		
					.init()
					.workItemHandler("CustomService", customServiceHandler)
					.workItemHandler(subProcessBuilder.getMockId(), customServiceHandler)
					.startProcess("application-process-saf", processInvocationParameters.asMap());
												
		waitUntilEvent(Events.ProcessStarted);
		
		assertProcessInstanceCompleted(processInstance.getId(), container.getSession());
		assertEquals(true,((Boolean) processInstance.getVariable("subProcessAborted")).booleanValue());
		assertEquals("10000", (String)processInstance.getVariable("brn"));
		assertEquals("12345", (String)processInstance.getVariable("applicationId"));
		printVariableValues(processInstance);
		container.close();
		
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testApplicationProcessSAF_ProcessIsInFlight_CallFail() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException, ProcessBoxConfigurationException {
		
		System.setProperty("processbox.debug.outcomes", "true");
		
		PBProperties processInvocationParameters = results().result("brn", "10000").result("applicationId", "12345");
		
		PBProperties serviceReturnResult1 = results().result("existingExternalInstanceId", "1111");
		PBProperties serviceTask1Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult1.asMap());
		
		
		PBProperties serviceReturnResult2 = results();
		PBProperties serviceTask2Result = 
				results()
					.result("retryFailed", true)
					.result("serviceReturn", serviceReturnResult2.asMap());

		ResultStackMockWorkItemHandler customServiceHandler = 
				new ResultStackMockWorkItemHandler(queue)
					.withDefault()
					.outcome(serviceTask1Result).outcome(serviceTask2Result);
		
		ResultStackSubProcessMockBuilder subProcessBuilder = processMockStack("Sample Process", "CustomService", "uk.gov.scotland.afrc.workmanagement.applications");
		
		Container container = new Container();
		WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
				container
					.resource(process("SAF Application Process", "application-process-saf", "com/sample/Application-Processing-SAF-Process.bpmn"))
					.resource(subProcessBuilder.getResource())		
					.init()
					.workItemHandler("CustomService", customServiceHandler)
					.workItemHandler(subProcessBuilder.getMockId(), customServiceHandler)
					.startProcess("application-process-saf", processInvocationParameters.asMap());
												
		waitUntilEvent(Events.ProcessStarted);
		
		assertProcessInstanceCompleted(processInstance.getId(), container.getSession());
		
		assertEquals(true,((Boolean) processInstance.getVariable("subProcessAborted")).booleanValue());
		assertEquals(false, (processInstance.getVariable("existingExternalInstanceId") == null 
				|| "".equals(processInstance.getVariable("existingExternalInstanceId"))));
		
		printVariableValues(processInstance);
		container.close();
		
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testApplicationProcessSAF_ProcessIsInFlight_call_Success() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException, ProcessBoxConfigurationException {
		
		System.setProperty("processbox.debug.outcomes", "true");
		
		PBProperties processInvocationParameters = results().result("brn", "10000").result("applicationId", "12345");
		
		PBProperties serviceReturnResult1 = results().result("existingExternalInstanceId", "1111");
		PBProperties serviceTask1Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult1.asMap());
		
		
		PBProperties serviceReturnResult2 = results();
		PBProperties serviceTask2Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult2.asMap());

		ResultStackMockWorkItemHandler customServiceHandler = 
				new ResultStackMockWorkItemHandler(queue)
					.withDefault()
					.outcome(serviceTask1Result).outcome(serviceTask2Result);
		
		ResultStackSubProcessMockBuilder subProcessBuilder = processMockStack("Sample Process", "CustomService", "uk.gov.scotland.afrc.workmanagement.applications");
		
		Container container = new Container();
		WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
				container
					.resource(process("SAF Application Process", "application-process-saf", "com/sample/Application-Processing-SAF-Process.bpmn"))
					.resource(subProcessBuilder.getResource())		
					.init()
					.workItemHandler("CustomService", customServiceHandler)
					.workItemHandler(subProcessBuilder.getMockId(), customServiceHandler)
					.startProcess("application-process-saf", processInvocationParameters.asMap());
												
		waitUntilEvent(Events.ProcessStarted);
		
		assertProcessInstanceCompleted(processInstance.getId(), container.getSession());
		
		assertEquals(false,((Boolean) processInstance.getVariable("subProcessAborted")).booleanValue());
		assertEquals(false, (processInstance.getVariable("existingExternalInstanceId") == null 
				|| "".equals(processInstance.getVariable("existingExternalInstanceId"))));
		printVariableValues(processInstance);
		container.close();
		
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testApplicationProcessSAF_ProcessHappyPath() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException, ExecutionException, TimeoutException, ProcessBoxConfigurationException {
		
		System.setProperty("processbox.debug.outcomes", "true");
		
		PBProperties processInvocationParameters = results().result("brn", "10000").result("applicationId", "12345");
		
		PBProperties serviceReturnResult1 = results();
		PBProperties serviceTask1Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult1.asMap());
		
		
		PBProperties serviceReturnResult2 = results();
		PBProperties serviceTask2Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult2.asMap());
		
		
		PBProperties serviceReturnResult3 = results().result("currentProcessingStatus", "Submitted").result("currentSuspensionStatus", "Notsubmitted");
		PBProperties serviceTask3Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult3.asMap());
		
		PBProperties serviceReturnResult4 = results().result("hasInspectionSelectionErrors", Boolean.valueOf(false)).result("hasProcessSAFErrors", Boolean.valueOf(false));
		PBProperties serviceTask4Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult4.asMap());
		
		PBProperties serviceReturnResult5 = results();
		PBProperties serviceTask5Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult5.asMap());
					
		
		PBProperties serviceReturnResult6 = results().result("hasInspectionSelectionErrors", Boolean.valueOf(false)).result("hasProcessSAFErrors", Boolean.valueOf(false));
		PBProperties serviceTask6Result = 
				results()
					.result("retryFailed", false)
					.result("serviceReturn", serviceReturnResult6.asMap());
		

		ResultStackMockWorkItemHandler customServiceHandler = 
				new ResultStackMockWorkItemHandler(queue)
					.withDefault()
					.outcome(serviceTask1Result).outcome(serviceTask2Result).outcome(serviceTask3Result)
					.outcome(serviceTask4Result).outcome(serviceTask5Result).outcome(serviceTask6Result);
		
		ResultStackSubProcessMockBuilder subProcessBuilder = processMockStack("Sample Process", "CustomService", "uk.gov.scotland.afrc.workmanagement.applications");
		
		Container container = new Container();
		WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl)
				container
					.resource(process("SAF Application Process", "application-process-saf", "com/sample/Application-Processing-SAF-Process.bpmn"))
					.resource(subProcessBuilder.getResource())		
					.init()
					.workItemHandler("CustomService", customServiceHandler)
					.workItemHandler(subProcessBuilder.getMockId(), customServiceHandler)
					.startProcess("application-process-saf", processInvocationParameters.asMap());
												
		waitUntilEvent(Events.ProcessStarted);
		
		waitUntilEvent(Events.ProcessBoxNodeInvocationEvent);				
		waitUntilEvent(Events.ProcessBoxNodeReturnEvent);		
		
		assertProcessInstanceCompleted(processInstance.getId(), container.getSession());
		assertEquals(false,((Boolean) processInstance.getVariable("subProcessAborted")).booleanValue());
		assertEquals(false, ((Boolean) processInstance.getVariable("hasInspectionSelectionErrors")).booleanValue());
		assertEquals( false ,((Boolean) processInstance.getVariable("hasProcessSAFErrors")).booleanValue());
		
		printVariableValues(processInstance);		
		container.close();
		
	}
	
	
	private void printVariableValues(WorkflowProcessInstanceImpl processInstance){
		Map<String, Object> vars =  processInstance.getVariables();
		System.out.println("##Process Variables and Values##");
		for(String key : vars.keySet()){
			System.out.println( key+ "  :  "+vars.get(key));
		}
	}

}


