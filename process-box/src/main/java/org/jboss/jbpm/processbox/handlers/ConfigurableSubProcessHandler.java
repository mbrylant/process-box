package org.jboss.jbpm.processbox.handlers;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.SubProcessHandler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConfigurableSubProcessHandler extends SubProcessHandler {

	@Override
	protected void handleCompositeContextNode(Node node, Element element,
			String uri, String localName, ExtensibleXmlParser parser)
			throws SAXException {
		// TODO Auto-generated method stub
//		super.handleCompositeContextNode(node, element, uri, localName, parser);
		System.out.println("ConfigurableSubProcessHandler.handleCompositeContextNode()");
	}

	@Override
	protected void handleForEachNode(Node arg0, Element arg1, String arg2,
			String arg3, ExtensibleXmlParser arg4) throws SAXException {
		System.out.println("ConfigurableSubProcessHandler.handleForEachNode()");
//		super.handleForEachNode(arg0, arg1, arg2, arg3, arg4);
	}

	public ConfigurableSubProcessHandler() {
		super();
		System.out.println("ConfigurableSubProcessHandler.init()");
	}
	
	
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
    	System.out.println("this is awsome");
        return ConfigurableCompositeContextNode.class;
    }	
	
	
	

}
