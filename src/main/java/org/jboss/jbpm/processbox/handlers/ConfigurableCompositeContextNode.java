package org.jboss.jbpm.processbox.handlers;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.xml.sax.Attributes;

public class ConfigurableCompositeContextNode extends CompositeContextNode {

	private static final long serialVersionUID = 6315867615040145703L;

	public ConfigurableCompositeContextNode() {
		// TODO Auto-generated constructor stub
	}
	
	
	protected Node createNode(Attributes attrs) {
		return new ConfigurableCompositeContextNode();
	}

	public Class<CompositeContextNode> generateNodeFor() {
		return CompositeContextNode.class;
	}

}
