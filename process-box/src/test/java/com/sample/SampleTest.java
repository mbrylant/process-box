package com.sample;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jboss.jbpm.processbox.listeners.DefaultAgendaEventListener;
import org.jboss.jbpm.processbox.listeners.DefaultProcessBoxListener;
import org.jboss.jbpm.processbox.listeners.DefaultWorkingMemoryEventListener;
import org.jboss.jbpm.processbox.runners.ProcessBoxTest;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;

public class SampleTest extends ProcessBoxTest {						
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSampleProcess() throws InterruptedException, UnexpectedProcessBoxEventException {
						
		
		Container container = new Container("com/sample/sample.bpmn");
		container.workItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(container.getSession()));
					
		ProcessInstance processInstance = container.startProcess("com.sample.bpmn.hello");				
		waitFor(ProcessBoxInstanceStartEvent.class);		
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


