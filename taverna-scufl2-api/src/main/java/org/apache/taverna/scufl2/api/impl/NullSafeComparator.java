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

/**
 * A Comparator that is null-safe
 * <p>
 * This comparator performs {@link Comparable#compareTo(Object)} if both objects
 * are non-null, and sorts <code>null</code> to be -1 (before the non-null), and
 * compares two <code>null</code>s as equal.
 * </p>
 * <p>
 * The static method {@link #compareObjects(Object, Object)} can be used if such
 * a comparison is needed without having to instantiate this class, for instance
 * because it is used within a {@link Comparable#compareTo(Object)} or because
 * the two arguments are not of an agreeable subclass &lt<T&gt; of
 * {@link Comparable} (but still can be compared).
 * 
 * @author Stian Soiland-Reyes
 * @param <T>
 *            The common type of the objects to be compared.
 */
public class NullSafeComparator<T extends Comparable<T>> implements
		Comparator<T> {
	/**
	 * Compare two objects for <code>null</code>ity only.
	 * <p>
	 * If both parameters are <code>null</code>, return <code>0</code>. If only a
	 * is <code>null</code>, return <code>-1</code>, if only b is
	 * <code>null</code>, return <code>1</code>. If none are <code>null</code>,
	 * return <code>null</code>.
	 * </p>
	 * The <code>null</code> return might sound counter-intuitive, but it
	 * basically means that the objects could not be compared on nullity alone,
	 * and must be further compared.
	 * 
	 * @param a
	 *            First object to compare
	 * @param b
	 *            Second object to compare
	 * @return 0 if both are null, -1 if a is null, 1 if b is null, otherwise
	 *         <code>null</code>.
	 */
	public static Integer nullCompare(Object a, Object b) {
		if (a == null && b == null)
			return 0;
		if (a == null && b != null)
			return -1;
		if (a != null && b == null)
			return 1;
		return null;
	}

	/**
	 * Compare any two objects, null-safe.
	 * <p>
	 * If any of the parameters are <code>null</code>, results are returned as
	 * for {@link #nullCompare(Object, Object)}. Otherwise,
	 * {@link Comparable#compareTo(Object)} is called on the first parameter
	 * against the second.
	 * 
	 * @param a
	 *            First object to compare. Must be instance of
	 *            {@link Comparable}.
	 * @param b
	 *            Second object to compare. Must be
	 * @return 0 if both are null, -1 if a is null, 1 if b is null, otherwise
	 *         the result of a.compareTo(b).
	 * @throws ClassCastException
	 *             if the specified object's type prevents it from being
	 *             compared to this object.
	 */
	@SuppressWarnings("unchecked")
	public static int compareObjects(Object a, Object b) {
		Integer diff = nullCompare(a, b);
		if (diff != null)
			return diff;
		return ((Comparable<Object>) a).compareTo(b);
	}

	@Override
	public int compare(T a, T b) {
		return compareObjects(a, b);
	}
}
