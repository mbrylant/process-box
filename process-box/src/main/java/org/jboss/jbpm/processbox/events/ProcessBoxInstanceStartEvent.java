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

package org.jboss.jbpm.processbox.events;

import org.drools.event.process.ProcessEvent;


public class ProcessBoxInstanceStartEvent implements ProcessBoxInstanceEvent {
	
	private final ProcessEvent event;
	
	public ProcessBoxInstanceStartEvent(ProcessEvent event) {		
		this.event = event;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return String.format("%d", event.getProcessInstance().getId());
	}

	public String getType() {
		// TODO Auto-generated method stub
		return "ProcessBoxInstanceEvent";
	}


	public String getSubType() {
		// TODO Auto-generated method stub
		return "ProcessBoxInstanceStartEvent";
	}

	public ProcessEvent getEvent() {
		// TODO Auto-generated method stub
		return this.event;
	}

}
