package uk.org.taverna.scufl2.api.common;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * A {@link Set} of {@link Named} objects.
 * <p>
 * This set will guarantee to never contain more than one {@link Named} object
 * per {@link Named#getName()}.
 * <p>
 * It is also possible to retrieve values by name using
 * {@link #getByName(String)}, or remove using {@link #removeByName(String)}.
 * The names can also be found in {@link #getNames()} and
 * {@link #nameIterator()}
 * <p>
 * Internally this set is backed by a {@link HashMap}.
 *
 * @author Stian Soiland-Reyes
 *
 * @param <T>
 *            Subclass of {@link Named} to keep in this set.
 */
public class NamedSet<T extends Named> extends AbstractSet<T> implements
		SortedSet<T> {

	protected transient SortedMap<String, T> namedMap;

	/**
	 * Construct an empty NamedSet.
	 */
	public NamedSet() {
		namedMap = new TreeMap<String, T>();
	}

	/**
	 * Construct a named set containing all the elements of the given
	 * collection.
	 * <p>
	 * If the collection contains several {@link Named} elements with the same
	 * name, only the last of those elements will be in the new NamedSet.
	 *
	 * @param collection
	 *            Collection which elements are to be added to the set.
	 */
	public NamedSet(Collection<? extends T> collection) {
		namedMap = new TreeMap<String, T>();
		addAll(collection);
	}

	@Override
	public boolean add(T named) {
		return namedMap.put(named.getName(), named) == null;
	}

	@Override
	public void clear() {
		namedMap.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public NamedSet<T> clone() {
		NamedSet<T> copy;
		try {
			copy = (NamedSet<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
		if (!(this.namedMap instanceof TreeMap)) {
			throw new IllegalStateException("Can't clone submap");
		}
		copy.namedMap = (SortedMap<String, T>) ((TreeMap) this.namedMap)
				.clone();
		return copy;
	}

	@Override
	public Comparator<? super T> comparator() {
		return null;
	}

	/**
	 * Return <code>true</code> if the NamedSet contains the given object, as
	 * compared using its {@link Object#equals(Object)} method.
	 * <p>
	 * Note that if a different object with the same name exist, this method
	 * will return <code>false</code>. To check for existence of a name, use
	 * {@link #containsName(String)}.
	 *
	 * @see Collection#contains(Object)
	 * @param o
	 * @return
	 */
	@Override
	public boolean contains(Object o) {
		if (!(o instanceof Named)) {
			return false;
		}
		Named named = (Named) o;
		return named.equals(namedMap.get(named.getName()));
	}

	/**
	 * Return <code>true</code> if the NamedSet contains an element with the
	 * given name.
	 *
	 * @param name
	 *            Name of object
	 * @return <code>true</code> if an element with given name is in set
	 */
	public boolean containsName(String name) {
		return namedMap.containsKey(name);
	}

	@Override
	public T first() {
		return namedMap.get(namedMap.firstKey());
	}

	/**
	 * Return the element with the given name from the set.
	 *
	 * @param name
	 * @return
	 */
	public T getByName(String name) {
		return namedMap.get(name);
	}

	public Set<String> getNames() {
		return namedMap.keySet();
	}

	@Override
	public SortedSet<T> headSet(T toElement) {
		// FIXME: Return a view instead of a copy
		NamedSet<T> headSet = new NamedSet<T>();
		headSet.namedMap = namedMap.headMap(toElement.getName());
		return headSet;
	}

	@Override
	public boolean isEmpty() {
		return namedMap.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return namedMap.values().iterator();
	}

	@Override
	public T last() {
		return namedMap.get(namedMap.lastKey());
	}

	public Iterator<String> nameIterator() {
		return namedMap.keySet().iterator();
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof Named)) {
			return false;
		}
		Named named = (Named) o;
		String name = named.getName();
		T exists = namedMap.get(name);
		if (named.equals(exists)) {
			return namedMap.remove(named.getName()) != null;
		}
		return false;
	}

	public T removeByName(String name) {
		return namedMap.remove(name);
	}

	@Override
	public int size() {
		return namedMap.size();
	}

	@Override
	public SortedSet<T> subSet(T fromElement, T toElement) {
		NamedSet<T> headSet = new NamedSet<T>();
		headSet.namedMap = namedMap.subMap(fromElement.getName(),
				toElement.getName());
		return headSet;
	}

	@Override
	public SortedSet<T> tailSet(T fromElement) {
		NamedSet<T> headSet = new NamedSet<T>();
		headSet.namedMap = namedMap.tailMap(fromElement.getName());
		return headSet;
	}

}
