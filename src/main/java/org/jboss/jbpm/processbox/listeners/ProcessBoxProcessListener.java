/*
 * (C) Copyright 2014 Mariusz Brylant (mbrylant@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jboss.jbpm.processbox.listeners;

import java.util.concurrent.BlockingQueue;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeEvent;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.instance.DefaultProcessBoxInstanceEvent;
import org.jboss.jbpm.processbox.handlers.EventBuffer;
import org.jboss.jbpm.processbox.handlers.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessBoxProcessListener implements ProcessEventListener {
	
	protected static Logger log = LoggerFactory.getLogger(ProcessBoxProcessListener.class);
	
	protected final BlockingQueue<ProcessBoxEvent> queue;
	
	public ProcessBoxProcessListener(BlockingQueue<ProcessBoxEvent>  queue){
		this.queue = queue;
	}
	
	private void handleEvent(ProcessEvent evt, String message){
		String id = evt instanceof ProcessNodeEvent ? ((ProcessNodeEvent)evt).getNodeInstance().getNodeName() : evt.getProcessInstance().getProcessId();
		log.debug(String.format("intercepted {%s} event on node {%s}", message, id));	
		EventBuffer.get().pushEvent(evt);
		EventLog.get().logEvent(evt);
		queue.add(new DefaultProcessBoxInstanceEvent(evt));
	}
	

	public void afterNodeLeft(ProcessNodeLeftEvent evt) {
		handleEvent(evt, "afterNodeLeft");	
	}

	public void afterNodeTriggered(ProcessNodeTriggeredEvent evt) {
		handleEvent(evt, "afterNodeTriggered");		
	}

	public void afterProcessCompleted(ProcessCompletedEvent evt) {
		handleEvent(evt, "afterProcessCompleted");	
	}

	public void afterProcessStarted(ProcessStartedEvent evt) {
		handleEvent(evt, "afterProcessStarted");	
	}

	public void afterVariableChanged(ProcessVariableChangedEvent evt) {
		handleEvent(evt, "afterVariableChanged");	
	}

	public void beforeNodeLeft(ProcessNodeLeftEvent evt) {
		handleEvent(evt, "beforeNodeLeft");
	}

	public void beforeNodeTriggered(ProcessNodeTriggeredEvent evt) {
		handleEvent(evt, "beforeNodeTriggered");
	}

	public void beforeProcessCompleted(ProcessCompletedEvent evt) {
		handleEvent(evt, "beforeProcessCompleted");
	}

	public void beforeProcessStarted(ProcessStartedEvent evt) {
		handleEvent(evt, "beforeProcessStarted");
	}

	public void beforeVariableChanged(ProcessVariableChangedEvent evt) {
		handleEvent(evt, "beforeVariableChanged");
	}

}
