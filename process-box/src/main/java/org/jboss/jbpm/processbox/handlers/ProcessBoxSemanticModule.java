package org.jboss.jbpm.processbox.handlers;

import org.drools.xml.DefaultSemanticModule;
import org.jbpm.bpmn2.xml.ActionNodeHandler;
import org.jbpm.bpmn2.xml.AdHocSubProcessHandler;
import org.jbpm.bpmn2.xml.AssociationHandler;
import org.jbpm.bpmn2.xml.BoundaryEventHandler;
import org.jbpm.bpmn2.xml.BusinessRuleTaskHandler;
import org.jbpm.bpmn2.xml.CallActivityHandler;
import org.jbpm.bpmn2.xml.CatchLinkNodeHandler;
import org.jbpm.bpmn2.xml.ComplexGatewayHandler;
import org.jbpm.bpmn2.xml.CompositeContextNodeHandler;
import org.jbpm.bpmn2.xml.DataObjectHandler;
import org.jbpm.bpmn2.xml.DataStoreHandler;
import org.jbpm.bpmn2.xml.DefinitionsHandler;
import org.jbpm.bpmn2.xml.EndEventHandler;
import org.jbpm.bpmn2.xml.EndNodeHandler;
import org.jbpm.bpmn2.xml.ErrorHandler;
import org.jbpm.bpmn2.xml.EscalationHandler;
import org.jbpm.bpmn2.xml.EventBasedGatewayHandler;
import org.jbpm.bpmn2.xml.EventNodeHandler;
import org.jbpm.bpmn2.xml.ExclusiveGatewayHandler;
import org.jbpm.bpmn2.xml.FaultNodeHandler;
import org.jbpm.bpmn2.xml.ForEachNodeHandler;
import org.jbpm.bpmn2.xml.InMessageRefHandler;
import org.jbpm.bpmn2.xml.InclusiveGatewayHandler;
import org.jbpm.bpmn2.xml.InterfaceHandler;
import org.jbpm.bpmn2.xml.IntermediateCatchEventHandler;
import org.jbpm.bpmn2.xml.IntermediateThrowEventHandler;
import org.jbpm.bpmn2.xml.ItemDefinitionHandler;
import org.jbpm.bpmn2.xml.JoinHandler;
import org.jbpm.bpmn2.xml.LaneHandler;
import org.jbpm.bpmn2.xml.ManualTaskHandler;
import org.jbpm.bpmn2.xml.MessageHandler;
import org.jbpm.bpmn2.xml.OperationHandler;
import org.jbpm.bpmn2.xml.ParallelGatewayHandler;
import org.jbpm.bpmn2.xml.ProcessHandler;
import org.jbpm.bpmn2.xml.PropertyHandler;
import org.jbpm.bpmn2.xml.ReceiveTaskHandler;
import org.jbpm.bpmn2.xml.ScriptTaskHandler;
import org.jbpm.bpmn2.xml.SendTaskHandler;
import org.jbpm.bpmn2.xml.SequenceFlowHandler;
import org.jbpm.bpmn2.xml.ServiceTaskHandler;
import org.jbpm.bpmn2.xml.SplitHandler;
import org.jbpm.bpmn2.xml.StartEventHandler;
import org.jbpm.bpmn2.xml.StateNodeHandler;
import org.jbpm.bpmn2.xml.SubProcessHandler;
import org.jbpm.bpmn2.xml.TaskHandler;
import org.jbpm.bpmn2.xml.ThrowLinkNodeHandler;
import org.jbpm.bpmn2.xml.TimerNodeHandler;
import org.jbpm.bpmn2.xml.TransactionHandler;
import org.jbpm.bpmn2.xml.UserTaskHandler;
import org.jbpm.bpmn2.xml.WorkItemNodeHandler;
import org.jbpm.compiler.xml.processes.SubProcessNodeHandler;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;

public class ProcessBoxSemanticModule  extends DefaultSemanticModule {
	
	public static final String BPMN2_URI = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	public ProcessBoxSemanticModule() {
		super(BPMN2_URI);		
		addHandler("definitions", new DefinitionsHandler());        
        addHandler("process", new ProcessHandler());        
        addHandler("property", new PropertyHandler());
        addHandler("lane", new LaneHandler());
        addHandler("startEvent", new StartEventHandler());
        addHandler("endEvent", new EndEventHandler());
        addHandler("exclusiveGateway", new ExclusiveGatewayHandler());
        addHandler("inclusiveGateway", new InclusiveGatewayHandler());
        addHandler("parallelGateway", new ParallelGatewayHandler());
		addHandler("eventBasedGateway", new EventBasedGatewayHandler());
        addHandler("complexGateway", new ComplexGatewayHandler());
        addHandler("scriptTask", new ScriptTaskHandler());
        addHandler("task", new TaskHandler());
        addHandler("userTask", new UserTaskHandler());
        addHandler("manualTask", new ManualTaskHandler());
        addHandler("serviceTask", new ServiceTaskHandler());
        addHandler("sendTask", new SendTaskHandler());
        addHandler("receiveTask", new ReceiveTaskHandler());
        addHandler("businessRuleTask", new BusinessRuleTaskHandler());
//        addHandler("callActivity", new ConfigurableMockCallActivityHandler());
//        addHandler("subProcess", new ConfigurableSubProcessHandler());
        
        addHandler("callActivity", new CallActivityHandler());
        addHandler("subProcess", new SubProcessNodeHandler());
        
        addHandler("adHocSubProcess", new AdHocSubProcessHandler());
        addHandler("intermediateThrowEvent", new IntermediateThrowEventHandler());
        addHandler("intermediateCatchEvent", new IntermediateCatchEventHandler());
        addHandler("boundaryEvent", new BoundaryEventHandler());
        addHandler("dataObject", new DataObjectHandler());
        addHandler("transaction", new TransactionHandler());
        addHandler("sequenceFlow", new SequenceFlowHandler());
        addHandler("itemDefinition", new ItemDefinitionHandler());
        addHandler("message", new MessageHandler());
        addHandler("interface", new InterfaceHandler());
        addHandler("operation", new OperationHandler());
        addHandler("inMessageRef", new InMessageRefHandler());
        addHandler("escalation", new EscalationHandler());
        addHandler("error", new ErrorHandler());
        addHandler("dataStore", new DataStoreHandler());
        addHandler("association", new AssociationHandler());
        
        handlersByClass.put(Split.class, new SplitHandler());
        handlersByClass.put(Join.class, new JoinHandler());
        handlersByClass.put(EventNode.class, new EventNodeHandler());
        handlersByClass.put(TimerNode.class, new TimerNodeHandler());
        handlersByClass.put(EndNode.class, new EndNodeHandler());
        handlersByClass.put(FaultNode.class, new FaultNodeHandler());
        handlersByClass.put(WorkItemNode.class, new WorkItemNodeHandler());
        handlersByClass.put(ActionNode.class, new ActionNodeHandler());
        handlersByClass.put(StateNode.class, new StateNodeHandler());
        handlersByClass.put(CompositeContextNode.class, new CompositeContextNodeHandler());
        handlersByClass.put(ForEachNode.class, new ForEachNodeHandler());
        handlersByClass.put(ThrowLinkNode.class, new ThrowLinkNodeHandler());
        handlersByClass.put(CatchLinkNode.class, new CatchLinkNodeHandler());
	}

}
