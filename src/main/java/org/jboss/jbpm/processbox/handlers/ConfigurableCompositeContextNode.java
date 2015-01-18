package org.jboss.jbpm.processbox.handlers;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.xml.sax.Attributes;

public class ConfigurableCompositeContextNode extends CompositeContextNode {

	public ConfigurableCompositeContextNode() {
		// TODO Auto-generated constructor stub
	}
	
	
	protected Node createNode(Attributes attrs) {
		return new ConfigurableCompositeContextNode();
	}

	public Class generateNodeFor() {
		return CompositeContextNode.class;
	}

}
