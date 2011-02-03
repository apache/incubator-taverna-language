package uk.org.taverna.scufl2.api.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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
public class NamedSet<T extends Named> extends TreeSet<T> implements Set<T> {

	protected transient HashMap<String, T> namedMap;

	/**
	 * Construct an empty NamedSet.
	 */
	public NamedSet() {
		namedMap = new HashMap<String, T>();
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
		namedMap = new HashMap<String, T>(Math.max(
				(int) (collection.size() / .75f) + 1, 16));
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
		NamedSet<T> copy = (NamedSet<T>) super.clone();
		copy.namedMap = (HashMap<String, T>) this.namedMap.clone();
		return copy;
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
	public boolean isEmpty() {
		return namedMap.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return namedMap.values().iterator();
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

}
