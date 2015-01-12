package org.jboss.jbpm.processbox.listeners;

import java.util.concurrent.BlockingQueue;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessStartedEvent;

import com.sample.BasicProcessBoxEvent;
import com.sample.ProcessBoxEvent;
import com.sample.ProcessBoxInstanceCompletedEvent;
import com.sample.ProcessBoxInstanceStartEvent;

public class DefaultProcessBoxListener extends ProcessBoxListener {

	public DefaultProcessBoxListener(BlockingQueue<ProcessBoxEvent> queue) {
		super(queue);
	}
	
	@Override
	public void afterProcessStarted(ProcessStartedEvent evt) {
		queue.add(new ProcessBoxInstanceStartEvent());
	}
	
	@Override
	public void afterProcessCompleted(ProcessCompletedEvent evt) {
		queue.add(new ProcessBoxInstanceCompletedEvent());
	}	

}
