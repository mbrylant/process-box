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

import org.jboss.jbpm.processbox.events.ProcessBoxEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxTaskCompletedEvent;
import org.jboss.jbpm.processbox.events.ProcessBoxTaskCreatedEvent;
import org.jbpm.task.event.TaskClaimedEvent;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessBoxTaskListener implements TaskEventListener {
	private static Logger log = LoggerFactory.getLogger(ProcessBoxListener.class);
	
	private final BlockingQueue<ProcessBoxEvent> queue;
	
	public ProcessBoxTaskListener(BlockingQueue<ProcessBoxEvent> queue) {
		this.queue = queue;
	}

	public void taskClaimed(TaskClaimedEvent evt) {
		log.debug(evt.toString());
		queue.add(new ProcessBoxTaskCreatedEvent());
	}

	public void taskCompleted(TaskCompletedEvent evt) {
		log.debug(evt.toString());
		queue.add(new ProcessBoxTaskCompletedEvent());
	}

	public void taskFailed(TaskFailedEvent evt) {
		log.debug(evt.toString());
	}

	public void taskSkipped(TaskSkippedEvent evt) {
		log.debug(evt.toString());		
	}

}
