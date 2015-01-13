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
import org.drools.event.process.ProcessStartedEvent;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jboss.jbpm.processbox.events.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxInstanceCompletedEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxInstanceStartEvent;

public class DefaultProcessBoxListener extends ProcessBoxListener {

	public DefaultProcessBoxListener(BlockingQueue<ProcessBoxEvent> queue) {
		super(queue);
	}
	
	@Override
	public void afterProcessStarted(ProcessStartedEvent evt) {
		super.afterProcessStarted(evt);
		queue.add(new ProcessBoxInstanceStartEvent(evt));		
	}
	
	@Override
	public void afterProcessCompleted(ProcessCompletedEvent evt) {
		super.afterProcessCompleted(evt);
		queue.add(new ProcessBoxInstanceCompletedEvent());
	}	

}
