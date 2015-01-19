package org.jboss.jbpm.processbox.handlers;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.CallActivityHandler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class ConfigurableMockCallActivityHandler extends CallActivityHandler {

	public ConfigurableMockCallActivityHandler() {
		super();
	}

	protected void handleNode(final Node node, final Element element,
			final String uri, final String localName,
			final ExtensibleXmlParser parser) throws SAXException {
		System.out.println("HERE 2");
		super.handleNode(node, element, uri, localName, parser);

		// handleScript((StateBasedNode)node, element, "onExit");
	}

	protected Node createNode(Attributes attrs) {
		return new ProcessBoxSubProcessNode();
	}

	public Class<SubProcessNode> generateNodeFor() {
		return SubProcessNode.class;
	}

}
