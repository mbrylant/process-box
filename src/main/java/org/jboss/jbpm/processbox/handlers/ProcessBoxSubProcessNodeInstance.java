package org.jboss.jbpm.processbox.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.RuntimeDroolsException;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.runtime.process.NodeInstance;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.VariableScopeResolverFactory;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.mvel2.MVEL;

public class ProcessBoxSubProcessNodeInstance extends SubProcessNodeInstance {

	public ProcessBoxSubProcessNodeInstance() {
		// TODO Auto-generated constructor stub
	}
	
private static final long serialVersionUID = 510l;
    
    private long processInstanceId;
	
    protected SubProcessNode getSubProcessNode() {
        return (SubProcessNode) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
    	super.internalTrigger(from, type);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A SubProcess node only accepts default incoming connections!");
        }
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (Iterator<DataAssociation> iterator =  getSubProcessNode().getInAssociations().iterator(); iterator.hasNext(); ) {
        	DataAssociation mapping = iterator.next();
        	Object parameterValue = null;
            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getSources().get(0));
            if (variableScopeInstance != null) {
                parameterValue = variableScopeInstance.getVariable(mapping.getSources().get(0));
            } else {
            	try {
            		parameterValue = MVEL.eval(mapping.getSources().get(0), new NodeInstanceResolverFactory(this));
            	} catch (Throwable t) {
            		System.err.println("Could not find variable scope for variable " + mapping.getSources().get(0));
                    System.err.println("when trying to execute SubProcess node " + getSubProcessNode().getName());
                    System.err.println("Continuing without setting parameter.");
            	}
            }
            if (parameterValue != null) {
            	parameters.put(mapping.getTarget(),parameterValue); 
            }
        }
        String processId = getSubProcessNode().getProcessId();
        // resolve processId if necessary
        Map<String, String> replacements = new HashMap<String, String>();
		Matcher matcher = PARAMETER_MATCHER.matcher(processId);
        while (matcher.find()) {
        	String paramName = matcher.group(1);
        	if (replacements.get(paramName) == null) {
            	VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                	resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                	String variableValueString = variableValue == null ? "" : variableValue.toString(); 
	                replacements.put(paramName, variableValueString);
                } else {
                	try {
                		Object variableValue = MVEL.eval(paramName, new NodeInstanceResolverFactory(this));
	                	String variableValueString = variableValue == null ? "" : variableValue.toString();
	                	replacements.put(paramName, variableValueString);
                	} catch (Throwable t) {
	                    System.err.println("Could not find variable scope for variable " + paramName);
	                    System.err.println("when trying to replace variable in processId for sub process " + getNodeName());
	                    System.err.println("Continuing without setting process id.");
                	}
                }
        	}
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
        	processId = processId.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        // start process instance
        Process process = ((ProcessInstance) getProcessInstance())
    		.getKnowledgeRuntime().getKnowledgeBase().getProcess(processId);
        if (process == null) {
        	System.err.println("Could not find process [] " + processId);
        	System.err.println("Aborting process");
        	System.out.println("HERE AGAIN");
        	((ProcessInstance) getProcessInstance()).setState(ProcessInstance.STATE_ABORTED);
        	throw new RuntimeDroolsException("Could not find process []" + processId);
        } else {
        	System.out.println("doing this now");
	    	ProcessInstance processInstance = ( ProcessInstance )
	    		((ProcessInstance) getProcessInstance()).getKnowledgeRuntime()
	    			.createProcessInstance(processId, parameters);
	    	this.processInstanceId = processInstance.getId();
	    	((ProcessInstanceImpl) processInstance).setMetaData("ParentProcessInstanceId", getProcessInstance().getId());
	    	((ProcessInstance) getProcessInstance()).getKnowledgeRuntime().startProcessInstance(processInstance.getId());
	    	if (!getSubProcessNode().isWaitForCompletion()) {
	    		triggerCompleted();
	    	} else if (processInstance.getState() == ProcessInstance.STATE_COMPLETED) {
	    		handleOutMappings(processInstance);
	    		triggerCompleted();
	    	} else {
	    		addProcessListener();
	    	}
        }
    }
    
    public void cancel() {
        super.cancel();
        if (getSubProcessNode() == null || !getSubProcessNode().isIndependent()) {
            ProcessInstance processInstance = (ProcessInstance)
                ((ProcessInstance) getProcessInstance()).getKnowledgeRuntime()
                    .getProcessInstance(processInstanceId);
            if (processInstance != null) {
            	processInstance.setState(ProcessInstance.STATE_ABORTED);
            }
        }
    }
    
    public long getProcessInstanceId() {
    	return processInstanceId;
    }
    
    public void internalSetProcessInstanceId(long processInstanceId) {
    	this.processInstanceId = processInstanceId;
    }

    public void addEventListeners() {
        super.addEventListeners();
        addProcessListener();
    }
    
    private void addProcessListener() {
    	getProcessInstance().addEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("processInstanceCompleted:" + processInstanceId, this, true);
    }

	public void signalEvent(String type, Object event) {
		if (("processInstanceCompleted:" + processInstanceId).equals(type)) {
			processInstanceCompleted((ProcessInstance) event);
		} else {
			super.signalEvent(type, event);
		}
	}
    
    public String[] getEventTypes() {
    	return new String[] { "processInstanceCompleted:" + processInstanceId };
    }
    
    public void processInstanceCompleted(ProcessInstance processInstance) {
        removeEventListeners();
        handleOutMappings(processInstance);
        triggerCompleted();
    }
    
    private void handleOutMappings(ProcessInstance processInstance) {
        VariableScopeInstance subProcessVariableScopeInstance = (VariableScopeInstance)
	        processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
	    for (Iterator<org.jbpm.workflow.core.node.DataAssociation> iterator= getSubProcessNode().getOutAssociations().iterator(); iterator.hasNext(); ) {
	    	org.jbpm.workflow.core.node.DataAssociation mapping = iterator.next();
	        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	            resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getTarget());
	        if (variableScopeInstance != null) {
	        	Object value = subProcessVariableScopeInstance.getVariable(mapping.getSources().get(0));
	        	if (value == null) {
	        		try {
	            		value = MVEL.eval(mapping.getSources().get(0), new VariableScopeResolverFactory(subProcessVariableScopeInstance));
	            	} catch (Throwable t) {
	            		// do nothing
	            	}
	        	}
	            variableScopeInstance.setVariable(mapping.getTarget(), value);
	        } else {
	            System.err.println("Could not find variable scope for variable " + mapping.getTarget());
	            System.err.println("when trying to complete SubProcess node " + getSubProcessNode().getName());
	            System.err.println("Continuing without setting variable.");
	        }
	    }
    }
    
    public String getNodeName() {
    	Node node = getNode();
    	if (node == null) {
    		return "[Dynamic] Sub Process";
    	}
    	return super.getNodeName();
    }

}
