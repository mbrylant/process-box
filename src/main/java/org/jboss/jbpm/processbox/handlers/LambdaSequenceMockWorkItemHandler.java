package org.jboss.jbpm.processbox.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

public class LambdaSequenceMockWorkItemHandler implements WorkItemHandler {
	
	private List<Function> lambdas; 
	
	public LambdaSequenceMockWorkItemHandler() {
		this.lambdas = new LinkedList<Function>();
	}
	
	public LambdaSequenceMockWorkItemHandler lambda(Function lambda) {
		this.lambdas.add(lambda);
		return this;
	}

	@Override
	public void executeWorkItem(WorkItem paramWorkItem,
			WorkItemManager paramWorkItemManager) {

	}

	@Override
	public void abortWorkItem(WorkItem paramWorkItem,
			WorkItemManager paramWorkItemManager) {

	}

}
