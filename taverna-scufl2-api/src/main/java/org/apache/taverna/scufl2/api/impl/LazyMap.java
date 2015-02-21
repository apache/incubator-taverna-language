package org.apache.taverna.scufl2.api.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A lazy TreeMap, inspired by org.apache.commons.collections.map.LazyMap
 * <p>
 * On {@link #get(Object)}, if a key is not found in the map,
 * {@link #getDefault(Object)} will be called to create the value for the given
 * key. This value is subsequently inserted into the map before being returned.
 * <p>
 * Call {@link #containsKey(Object)} to check if a key is in the map.
 * <p>
 * Implementations of this class must implement {@link #getDefault(Object)} to
 * specify the default value to create.
 * 
 * @author Stian Soiland-Reyes
 * 
 * @param <KeyType>
 *            Type of key
 * @param <ValueType>
 *            Type of value
 */
public abstract class LazyMap<KeyType, ValueType> extends
		TreeMap<KeyType, ValueType> implements Map<KeyType, ValueType> {
	private static final long serialVersionUID = 3284689384208221667L;

	public LazyMap() {
		super();
	}

	public LazyMap(Comparator<? super KeyType> comparator) {
		super(comparator);
	}

	public LazyMap(Map<? extends KeyType, ? extends ValueType> m) {
		super(m);
	}

	public LazyMap(SortedMap<KeyType, ? extends ValueType> m) {
		super(m);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ValueType get(Object key) {
		ValueType value = super.get(key);
		if (value == null) {
			value = getDefault((KeyType) key);
			put((KeyType) key, value);
		}
		return value;
	}

	public abstract ValueType getDefault(KeyType key);
}
