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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A Comparator of Iterables of comparables.
 * <p>
 * This comparator compares two {@link Iterable}s by comparing their items.
 * Iterables are compared on the first items, then on the second items, etc. If
 * Iterable A is larger than Iterable B, but all the items of B compared 0
 * (equal) with the corresponding items in A, then the comparator will sort the
 * smaller Iterable before the bigger Iterable.
 * <p>
 * Obviously this comparator would not give predictable results for
 * {@link Iterable}s which don't have a predictable iteration order, like
 * {@link HashSet}, but should work well with {@link List}s,
 * {@link LinkedHashSet}, etc.
 * <p>
 * The two Iterables don't need to contain items of the same classes, as long as
 * their element's {@link Comparable#compareTo(Object)} allow comparison. (For
 * instance a Iterable&lt;String&gt; would not compare against
 * Iterable&lt;Integer&gt;). As according to
 * {@link Comparator#compare(Object, Object)} a {@link ClassCastException} would
 * be thrown in such cases.
 * <p>
 * Items in the Iterable are compared using {@link NullSafeComparator}, so this
 * comparator will handle Iterables containing <code>null</code> items
 * predictably. This comparator also allows <code>null</code> in the same way,
 * so comparison of a {@link Iterable} and <code>null</code> will sort
 * <code>null</code> before the Iterable.
 * <p>
 * If an item in the Iterable is a {@link Iterable} which is not itself
 * {@link Comparable}, and it is being compared with a different Iterable, it
 * will be recursively compared with the same instance of
 * {@link IterableComparator}. However, if such {@link Iterable}s are compared
 * with other {@link Comparable}s a {@link ClassCastException} would be thrown.
 * <p>
 * If an item in a Iterable is not {@link Comparable} <code>null</code> and
 * could not be recursively compared, a {@link ClassCastException} will be
 * thrown, as according to {@link Comparator#compare(Object, Object)}
 * 
 * @author Stian Soiland-Reyes
 */
public class IterableComparator implements Comparator<Iterable<?>> {
	@Override
	public int compare(Iterable<?> a, Iterable<?> b) {
		Integer nullCompare = compareNulls(a, b);
		if (nullCompare != null)
			return nullCompare;
		
		Iterator<?> aIt = a.iterator();
		Iterator<?> bIt = b.iterator();
		
		while (aIt.hasNext() && bIt.hasNext()) {
			int itemCompare = compareItems(aIt.next(), bIt.next());
			if (itemCompare != 0)
				return itemCompare;
		}
		// Puh, compared all corresponding items
		
		if (aIt.hasNext())
			return 1; // a is bigger
		if (bIt.hasNext())
			return -1; // a is smaller
		// Both finished? Then we are equal!
		return 0;		
	}

	@SuppressWarnings("unchecked")
	protected int compareItems(Object a, Object b) {
		Integer nullCompare = compareNulls(a, b);
		if (nullCompare != null)
			return nullCompare;
		if (a instanceof Comparable && b instanceof Comparable)
			return ((Comparable<Object>) a).compareTo((Comparable<Object>) b);
		if (a instanceof Iterable && b instanceof Iterable)
			// Recurse
			return compare((Iterable<?>) a, (Iterable<?>) b);
		throw new ClassCastException(
				"Compared items must be null, or both be Comparable or Iterables");
	}

	protected Integer compareNulls(Object a, Object b) {
		if (a == null && b == null)
			return 0;
		if (a == null && b != null)
			return -1;
		if (a != null && b == null)
			return 1;
		return null;
	}
}
