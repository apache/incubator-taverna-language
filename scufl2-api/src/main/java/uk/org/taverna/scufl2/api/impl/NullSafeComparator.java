package uk.org.taverna.scufl2.api.impl;

import java.util.Comparator;

/**
 * A Comparator that is null-safe
 * <p>
 * This comparator performs {@link Comparable#compareTo(Object)} if both objects
 * are non-null, and sorts <code>null</code> to be -1 (before the non-null), and compares two
 * <code>null</code>s as equal.
 * </p>
 * <p>
 * The static method {@link #compareObjects(Object, Object)} can be used if such a comparison
 * is needed without having to instantiate this class, for instance because it is used within
 * a {@link Comparable#compareTo(Object)} or because the two arguments are not of an agreeable
 * subclass &lt<T&gt; of {@link Comparable} (but still can be compared).
 * </p>
 * 
 * @author Stian Soiland-Reyes
 * 
 * @param <T> The common type of the objects to be compared.
 */
public class NullSafeComparator<T extends Comparable<T>> implements
		Comparator<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compareObjects(Object a, Object b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null && b != null) {
			return -1;
		}
		if (a != null && b == null) {
			return 1;
		}
		return ((Comparable)a).compareTo(b);
	}
	
	@Override
	public int compare(T a, T b) {
		return compareObjects(a, b);
	}

}
