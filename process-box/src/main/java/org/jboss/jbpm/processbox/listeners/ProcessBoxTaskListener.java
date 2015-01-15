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

import org.drools.event.process.ProcessEvent;
import org.drools.event.process.ProcessNodeEvent;
import org.jboss.jbpm.processbox.events.base.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.instance.DefaultProcessBoxInstanceEvent;
import org.jboss.jbpm.processbox.events.task.DefaultProcessBoxTaskEvent;
import org.jboss.jbpm.processbox.events.task.typed.ProcessBoxTaskCompletedEvent;
import org.jboss.jbpm.processbox.events.task.typed.ProcessBoxTaskCreatedEvent;
import org.jboss.jbpm.processbox.events.task.typed.ProcessBoxTaskFailedEvent;
import org.jboss.jbpm.processbox.events.task.typed.ProcessBoxTaskSkippedEvent;
import org.jboss.jbpm.processbox.handlers.EventBuffer;
import org.jboss.jbpm.processbox.handlers.EventLog;
import org.jbpm.task.event.TaskClaimedEvent;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.event.TaskUserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessBoxTaskListener implements TaskEventListener {
	private static Logger log = LoggerFactory.getLogger(ProcessBoxTaskListener.class);
	
	private final BlockingQueue<ProcessBoxEvent> queue;
	
	public ProcessBoxTaskListener(BlockingQueue<ProcessBoxEvent> queue) {
		this.queue = queue;
	}
	
	private void handleEvent(TaskUserEvent evt, String message){
		String id = String.format("%d",  evt.getTaskId());
		log.debug(String.format("intercepted {%s} event on task {%s}", message, id));
		EventBuffer.get().pushEvent(evt);
		EventLog.get().logEvent(evt);
		queue.add(new DefaultProcessBoxTaskEvent(evt));
	}

	public void taskClaimed(TaskClaimedEvent evt) {
		handleEvent(evt, evt.getClass().getSimpleName());
	}

	public void taskCompleted(TaskCompletedEvent evt) {
		handleEvent(evt, evt.getClass().getSimpleName());
	}

	public void taskFailed(TaskFailedEvent evt) {
		handleEvent(evt, evt.getClass().getSimpleName());
	}

	public void taskSkipped(TaskSkippedEvent evt) {
		handleEvent(evt, evt.getClass().getSimpleName());
	}

}
