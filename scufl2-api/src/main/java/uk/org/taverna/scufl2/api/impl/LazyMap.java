package uk.org.taverna.scufl2.api.impl;

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
