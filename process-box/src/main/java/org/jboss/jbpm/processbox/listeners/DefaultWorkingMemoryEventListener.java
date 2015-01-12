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

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWorkingMemoryEventListener implements org.drools.event.WorkingMemoryEventListener, org.drools.event.rule.WorkingMemoryEventListener {

	private Logger log = LoggerFactory.getLogger(DefaultWorkingMemoryEventListener.class);
	

	public void objectInserted(org.drools.event.ObjectInsertedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectRetracted(org.drools.event.ObjectRetractedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectUpdated(org.drools.event.ObjectUpdatedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectInserted(ObjectInsertedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectRetracted(ObjectRetractedEvent arg0) {
		log.debug(arg0.toString());
		
	}

	public void objectUpdated(ObjectUpdatedEvent arg0) {
		log.debug(arg0.toString());
		
	}

}
