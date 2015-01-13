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


public class BasicProcessBoxEvent implements ProcessBoxEvent {
	
	private String id = "";

	public BasicProcessBoxEvent() {}
	
	public BasicProcessBoxEvent(String id) {this.id = id;}

	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return "ProcessBoxEvent";
	}
	
	public String getSubType() {
		// TODO Auto-generated method stub
		return "BasicProcessBoxEvent";
	}

}
