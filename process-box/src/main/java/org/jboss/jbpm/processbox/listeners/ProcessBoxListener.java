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
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.ProcessBoxEvent;

public abstract class ProcessBoxListener implements ProcessEventListener {
	
	protected static Logger log = LoggerFactory.getLogger(ProcessBoxListener.class);
	
	protected final BlockingQueue<ProcessBoxEvent> queue;
	
	public ProcessBoxListener(BlockingQueue<ProcessBoxEvent>  queue){
		this.queue = queue;
	}

	public void afterNodeLeft(ProcessNodeLeftEvent evt) {
		log.debug(evt.toString());
	}

	public void afterNodeTriggered(ProcessNodeTriggeredEvent evt) {
		log.debug(evt.toString());
	}

	public void afterProcessCompleted(ProcessCompletedEvent evt) {
		log.debug(evt.toString());
	}

	public void afterProcessStarted(ProcessStartedEvent evt) {
		log.debug(evt.toString());		
	}

	public void afterVariableChanged(ProcessVariableChangedEvent evt) {
		log.debug(evt.toString());
	}

	public void beforeNodeLeft(ProcessNodeLeftEvent evt) {
		log.debug(evt.toString());
	}

	public void beforeNodeTriggered(ProcessNodeTriggeredEvent evt) {
		log.debug(evt.toString());
	}

	public void beforeProcessCompleted(ProcessCompletedEvent evt) {
		log.debug(evt.toString());		
	}

	public void beforeProcessStarted(ProcessStartedEvent evt) {
		log.debug(evt.toString());		
	}

	public void beforeVariableChanged(ProcessVariableChangedEvent evt) {
		log.debug(evt.toString());		
	}

}
