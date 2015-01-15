package org.jboss.jbpm.processbox.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;

public class MockCustomWebServiceWorkItemHandler implements WorkItemHandler {

	KnowledgeRuntime session;
	
	String expectedServiceName;
	String expectedServiceOperationName;
	Map<String, Object> expectedServiceParams; 
	boolean expectedIsReturnTypeMap;
	
	Object webserviceResult;
	boolean retryFailedResult;
	String sysAdminQId;
	
	public MockCustomWebServiceWorkItemHandler(KnowledgeRuntime session) {
		this.session = session;
	}
	
	
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		workItemManager.abortWorkItem(workItem.getId());
	}

	@SuppressWarnings("unchecked")
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		assertNotNull(session);
		assertEquals(expectedIsReturnTypeMap, workItem.getParameter("IsReturnTypeMap"));
		assertEquals(expectedServiceName, workItem.getParameter("ServiceName"));
		assertEquals(expectedServiceOperationName, workItem.getParameter("ServiceOperationName"));
		assertEquals(expectedServiceParams, workItem.getParameter("ServiceParams"));
		
		WorkflowProcessInstance wpi = (WorkflowProcessInstance) session.getProcessInstance(workItem.getProcessInstanceId());
		Map<String, Object> contentMap = (HashMap<String, Object>) wpi.getVariable("contentMap");
		contentMap.put("retryFailed", retryFailedResult);
		contentMap.put("sysAdminQId", "admin");
		
		wpi.setVariable("serviceReturn", webserviceResult);
		wpi.setVariable("contentMap", contentMap);
		
		
		workItemManager.completeWorkItem(workItem.getId(), null);
	}
	
	public void setExpectedIsReturnTypeMap(boolean expectedIsReturnTypeMap) {
		this.expectedIsReturnTypeMap = expectedIsReturnTypeMap;
	}
	
	public void setExpectedServiceName(String expectedServiceName) {
		this.expectedServiceName = expectedServiceName;
	}
	
	public void setExpectedServiceOperationName(
			String expectedServiceOperationName) {
		this.expectedServiceOperationName = expectedServiceOperationName;
	}
	
	public void setExpectedServiceParams(
			Map<String, Object> expectedServiceParams) {
		this.expectedServiceParams = expectedServiceParams;
	}
	
	public void setWebserviceResult(Object webserviceResult) {
		this.webserviceResult = webserviceResult;
	}
	
	public void setRetryFailedResult(boolean retryFailedResult) {
		this.retryFailedResult = retryFailedResult;
	}

}
