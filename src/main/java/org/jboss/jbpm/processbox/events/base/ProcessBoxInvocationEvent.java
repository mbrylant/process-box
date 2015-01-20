package org.jboss.jbpm.processbox.events.base;

import java.util.Map;
import org.jboss.jbpm.processbox.model.PBProperties;

public interface ProcessBoxInvocationEvent extends ProcessBoxEvent {

	public Map<String, Object> getParameters();
	
	public PBProperties getProperties();
}
