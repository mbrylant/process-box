package org.jboss.jbpm.processbox.runners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import static org.junit.Assert.*;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.h2.tools.Server;
import org.jboss.jbpm.processbox.listeners.DefaultAgendaEventListener;
import org.jboss.jbpm.processbox.listeners.DefaultProcessBoxListener;
import org.jboss.jbpm.processbox.listeners.DefaultWorkingMemoryEventListener;
import org.jboss.jbpm.processbox.listeners.ProcessBoxTaskListener;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.sample.ProcessBoxEvent;

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

		public UnexpectedProcessBoxEventException(String string) {
			// TODO Auto-generated constructor stub
		}}
	
	public TaskSummary getTask (String userName, String encoding){
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(userName, encoding); 
		assertThat(tasks, is(not(empty())));
		TaskSummary task = tasks.get(0);
		return task;
	}
	
	public static class Container {
		
		private Map<String, ProcessInstance> instances = new HashMap<String, ProcessInstance>();
		private StatefulKnowledgeSession ksession;
		
		private KnowledgeRuntimeLogger logger = null;
		
		public StatefulKnowledgeSession getSession() {
			return ksession;
		}

		public Container(String model){
			start(model);
		}
				
		private void addDefaultListeners(){
			ksession.addEventListener(new DefaultProcessBoxListener(queue));		
			ksession.addEventListener(new DefaultAgendaEventListener());
			ksession.addEventListener(new DefaultWorkingMemoryEventListener());
		}
		
		public void close(){
//			logger.close();
		}
		
		private void start(String model){
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource("com/sample/sample.bpmn"), ResourceType.BPMN2);
			KnowledgeBase kbase = kbuilder.newKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			addDefaultListeners();
		

//			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "bpmslogfile", 1000);
//			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
//			GenericHTWorkItemHandler taskHandler = new GenericHTWorkItemHandler(taskService, ksession);	
		}
		
		
		public Container workItemHandler(String taskName, WorkItemHandler handler) {
			this.ksession.getWorkItemManager().registerWorkItemHandler(taskName, handler);
			return this;
			
		}


		public ProcessInstance startProcess(String processName) {
			ProcessInstance processInstance = ksession.startProcess(processName, null);
			instances.put(processName, processInstance);
			return processInstance;
			
		}
		
	}
	
	protected void waitFor(Class<? extends ProcessBoxEvent> eventClass) throws UnexpectedProcessBoxEventException, InterruptedException {
		
		ProcessBoxEvent event = null;
		event = queue.take();

		
		if (event.getClass() != eventClass) {
			throw new UnexpectedProcessBoxEventException(String.format("Expecting ProcessBoxEvent of type {%s}, but got {%s} ", eventClass.getSimpleName(), event.getClass().getSimpleName()) );
		}
		
		log.debug("Received event: {" + event + "}");
	}

}
