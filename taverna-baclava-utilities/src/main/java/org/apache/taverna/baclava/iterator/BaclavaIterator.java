/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package org.apache.taverna.baclava.iterator;
/*
 *
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
 *
*/


// Utility Imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.taverna.baclava.DataThing;


/**
 * This provides an Iterator interface with a single additional method to allow
 * reset of the iterator to its starting state. This is required to produce
 * orthogonal joins between iterators used in the implicit iteration mechanism
 * within the enactor.
 * 
 * @author Tom Oinn
 */
public class BaclavaIterator implements ResumableIterator {

	private Collection underlyingCollection = null;

	private List indexList = new ArrayList();

	private Iterator indexIterator = indexList.iterator();

	private Iterator internalIterator = null;

	private int[] currentLocation = null;

	private DataThing parentThing = null;

	public BaclavaIterator(Collection c) {
		this.underlyingCollection = c;
		this.internalIterator = c.iterator();
	}

	public BaclavaIterator(Collection c, List indexList) {
		this(c);
		this.indexList = indexList;
		this.indexIterator = indexList.iterator();
	}

	/**
	 * Construct an iterator from a list of non data thing objects, a reference
	 * to the parent DataThing which contains them and an explicit index list.
	 * This avoids the overhead of creating all the datathing objects at
	 * iterator construction time, something that was causing some serious
	 * performance issues, mostly because of the metadata copy operations
	 */
	public BaclavaIterator(DataThing parent, Collection c, List indexList) {
		this(c, indexList);
		this.parentThing = parent;
	}

	public int[] getCurrentLocation() {
		if (currentLocation == null || indexList.isEmpty()) {
			// No location information available
			return new int[0];
		} else {
			return currentLocation;
		}
	}

	public synchronized boolean hasNext() {
		if (underlyingCollection.isEmpty()) {
			return false;
		}
		return this.internalIterator.hasNext();
	}

	public synchronized Object next() throws NoSuchElementException {
		if (indexIterator.hasNext()) {
			// Increment the current index
			currentLocation = (int[]) indexIterator.next();
		}
		if (parentThing == null) {
			return this.internalIterator.next();
		} else {
			// Construct a new datathing on the fly and return it
			DataThing newThing = new DataThing(this.internalIterator.next());
			newThing.linkMetadataFrom(parentThing);
			return newThing;
		}
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Remove operation not allowed in a BaclavaIterator.");
	}

	public synchronized void reset() {
		this.internalIterator = underlyingCollection.iterator();
		this.indexIterator = indexList.iterator();
	}

	public int size() {
		return this.underlyingCollection.size();
	}

}
