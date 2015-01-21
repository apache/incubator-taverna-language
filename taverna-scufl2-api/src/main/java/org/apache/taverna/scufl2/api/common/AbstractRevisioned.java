package org.apache.taverna.scufl2.api.common;

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


import java.net.URI;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.apache.taverna.scufl2.api.annotation.Revision;
import org.apache.taverna.scufl2.api.annotation.Revisioned;


public abstract class AbstractRevisioned extends AbstractNamed implements
		Revisioned {
	private Revision currentRevision;

	protected URI generateNewIdentifier() {
		return getIdentifierRoot().resolve(UUID.randomUUID().toString() + "/");
	}

	protected abstract URI getIdentifierRoot();

	public AbstractRevisioned() {
		newRevision();
		String id = getIdentifierRoot().relativize(getIdentifier())
				.toASCIIString().replace("/", "");
		setName(id);
	}

	public AbstractRevisioned(String name) {
		super(name);
		newRevision();
	}

	@Override
	public void setCurrentRevision(Revision currentRevision) {
		this.currentRevision = currentRevision;
		if (currentRevision == null)
			newRevision();
	}

	@Override
	public Revision newRevision() {
		return newRevision(null);
	}

	@Override
	public Revision newRevision(URI revisionIdentifier) {
		GregorianCalendar created = null;
		if (revisionIdentifier == null) {
			revisionIdentifier = generateNewIdentifier();
			created = new GregorianCalendar();
		}
		Revision newRevision = new Revision(revisionIdentifier,
				getCurrentRevision());
		newRevision.setGeneratedAtTime(created);
		setCurrentRevision(newRevision);
		return newRevision;
	}

	@Override
	public Revision getCurrentRevision() {
		return currentRevision;
	}

	/**
	 * Returns the identifier.
	 * <p>
	 * The the default identifier is based on #getIdentifierRoot() plus a random
	 * UUID.
	 * 
	 * @see {@link #setIdentifier(URI)}
	 * @return the identifier
	 */
	@Override
	public URI getIdentifier() {
		if (getCurrentRevision() == null)
			return null;
		return getCurrentRevision().getIdentifier();
	}

	/**
	 * Set the identifier.
	 * <p>
	 * This will delete any previous revisions in {@link #getCurrentRevision()}
	 * 
	 * @see #getIdentifier()
	 * @see #getCurrentRevision()
	 * @param identifier
	 *            the identifier
	 */
	@Override
	public void setIdentifier(URI identifier) {
		setCurrentRevision(new Revision(identifier, null));
	}
}
