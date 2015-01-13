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

import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbiox.container.ContainerInitializationException;
import org.jboss.jbpm.processbox.core.ProcessBoxTest;
import org.jboss.jbpm.processbox.events.ProcessBoxInstanceCompletedEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxInstanceStartEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxTaskCompletedEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxTaskCreatedEvent;
import org.jboss.jbpm.processbox.handlers.ConfigurableMockWorkItemHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

public class SimpleTest extends ProcessBoxTest {						
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleProcess() throws InterruptedException, UnexpectedProcessBoxEventException, ContainerInitializationException {
						
		
		Container container = new Container("org/jboss/jbpm/processbox/tests/simple.bpmn");
//		Container container = new Container("com/sample/simple.bpmn2");
		container.workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()));
//		container.workItemHandler("Service Task", new ConfigurableMockWorkItemHandler(true));
		container.workItemHandler("SVC", new ServiceTaskHandler());
					
		ProcessInstance processInstance = container.startProcess("process.simple");				
		ProcessBoxInstanceStartEvent processBoxInstanceStartEvent = (ProcessBoxInstanceStartEvent)waitFor(ProcessBoxInstanceStartEvent.class);
		log.debug(String.format("Process Instance {%s} started in contaier", processBoxInstanceStartEvent.getId()));
		assertProcessInstanceActive(processInstance.getId(), container.getSession());
		
		waitFor(ProcessBoxTaskCreatedEvent.class);				
		TaskSummary john = getTask("john", "en-UK");
		taskService.start(john.getId(), "john");
		taskService.complete(john.getId(), "john", null);		
		waitFor(ProcessBoxTaskCompletedEvent.class);		
		
		waitFor(ProcessBoxTaskCreatedEvent.class);	
		TaskSummary mary = getTask("mary", "en-UK");
		taskService.start(mary.getId(), "mary");		
//		taskService.complete(mary.getId(), "mary", null);		
//		waitFor(ProcessBoxTaskCompletedEvent.class);
		
		container.getSession().abortProcessInstance(processInstance.getId());
		waitFor(ProcessBoxInstanceCompletedEvent.class);
		
		List<TaskSummary> tasksAfetrAboort = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(1, tasksAfetrAboort.size());
		
		container.close();
		
	}

}


