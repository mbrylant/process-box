package org.jboss.jbpm.processbox.model;

import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;

public abstract class ProcessBoxNode<T> {

	public abstract boolean matches(ProcessBoxEvent event);

}
