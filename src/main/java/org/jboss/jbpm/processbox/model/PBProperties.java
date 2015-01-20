package org.jboss.jbpm.processbox.model;

import java.util.HashMap;
import java.util.Map;

public class PBProperties extends HashMap<String, Object> {

	private static final long serialVersionUID = -965232695270319275L;

	public PBProperties() {
		super();
	}

	public PBProperties(int initialCapacity) {
		super(initialCapacity);
	}

	public PBProperties(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	public PBProperties(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	
	public PBProperties result(String key, Object value) {
		this.put(key, value);
		return this;
	}
	
	public Map<String, Object> asMap() {
		return new HashMap<String, Object>(this);
	}
	
	public String describe() {
		StringBuilder description = new StringBuilder("");
		for (String key : this.keySet()) {			
			if (description.length() != 0) {
				description.append(", ");
			}
			description.append(String.format("{ %s => %s}", key, this.get(key) ));
		}
		return description.toString();
	}

}
