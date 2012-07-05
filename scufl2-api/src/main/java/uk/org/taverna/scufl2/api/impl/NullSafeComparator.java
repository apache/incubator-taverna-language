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
 * 
 * 
 * @author Stian Soiland-Reyes
 * 
 * @param <T>
 */
public class NullSafeComparator<T extends Comparable<T>> implements
		Comparator<T> {

	@Override
	public int compare(T a, T b) {
		if (a == null && b == null) {
			return 0;
		}
		if (a == null && b != null) {
			return -1;
		}
		if (a != null && b == null) {
			return 1;
		}
		return a.compareTo(b);
	}

}
