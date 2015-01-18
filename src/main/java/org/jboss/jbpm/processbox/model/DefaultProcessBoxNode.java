package org.jboss.jbpm.processbox.model;

import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;

public class DefaultProcessBoxNode<T> extends ProcessBoxNode<T> {
	
	private NodeId id;
	private NodeMatcherType nodeMatcherType;

	@Override
	public boolean matches(ProcessBoxEvent event) {
		switch (nodeMatcherType) {
			case NODEID:
				if (id.getId() == event.getId()) { return true; }
				return false;				
			default:
				return false;
		}				
	}
	
	private DefaultProcessBoxNode(NodeId id){
		this.id = id;
		this.nodeMatcherType = NodeMatcherType.NODEID;
	}
	
	public static DefaultProcessBoxNode<Initialized> with(NodeId id){
		return new DefaultProcessBoxNode<Initialized>(id);
	}
}
