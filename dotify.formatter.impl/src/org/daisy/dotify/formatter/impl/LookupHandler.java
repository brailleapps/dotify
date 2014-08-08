package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LookupHandler<K, V> {
	private final Map<K, V> keyValueMap;
	private final Set<K> requestedKeys;
	private boolean dirty;
	
	LookupHandler() {
		this.keyValueMap = new HashMap<K, V>();
		this.requestedKeys = new HashSet<K>();
		this.dirty = false;
	}

	V get(K key) {
		requestedKeys.add(key);
		V ret = keyValueMap.get(key);
		if (ret==null) {
			dirty = true;
		}
		return ret;
	}
	
	void put(K key, V value) {
		V prv = keyValueMap.put(key, value);
		if (requestedKeys.contains(key) && prv!=null && !prv.equals(value)) {
			dirty = true;
		}
	}

	boolean isDirty() {
		return dirty;
	}
	
	void setDirty(boolean value) {
		if (!value) {
			requestedKeys.clear();
		}
		dirty = value;
	}
}
