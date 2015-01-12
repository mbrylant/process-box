package org.jboss.jbpm.processbox.listeners;

import java.util.concurrent.BlockingQueue;

import org.jbpm.task.event.TaskClaimedEvent;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.BasicProcessBoxEvent;
import com.sample.ProcessBoxEvent;
import com.sample.ProcessBoxTaskCompletedEvent;
import com.sample.ProcessBoxTaskCreatedEvent;

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
