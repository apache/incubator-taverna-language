package uk.org.taverna.scufl2.api.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * A lazy map, inspired by org.apache.commons.collections.map.LazyMap
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
HashMap<KeyType, ValueType> implements Map<KeyType, ValueType> {

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
