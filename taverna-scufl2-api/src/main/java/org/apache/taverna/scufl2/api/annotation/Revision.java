package org.apache.taverna.scufl2.api.annotation;

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
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

import org.apache.taverna.scufl2.api.common.AbstractCloneable;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;


/**
 * A record of a revision.
 * <p>
 * {@link Revisioned} workflow beans point to their latest Revision, which
 * {@link #getIdentifier()} identifiers this version of the revisioned bean.
 * <p>
 * It is important that the identifier is world-wide unique, but also that it
 * always identifies the same revision. It is not a requirement that the actual
 * revision of the bean can be retrieved from the URI. {@link Revisioned}
 * objects can mint UUID based URIs within namespaces of
 * <code>http://ns.taverna.org.uk/</code> when using
 * {@link Revisioned#newRevision()}.
 * <p>
 * The {@link #getPreviousRevision()} indicates the previous step in the chain
 * of revisions which led to the current version. The revision only provides
 * metadata about that older version, not the actual representation. (that is
 * out of scope for scufl2, and should be handled by regular version control
 * systems such as git).
 * <p>
 * A revision might note that compared to its previous revision, it has added (
 * {@link #getAdditionOf()}), removed ( {@link #getRemovalOf()}) and/or modified
 * ( {@link #getModificationsOf()}) resources. For instance, in a revision for a
 * {@link Workflow}, the Revision might record the addition of a
 * {@link Processor}; and in revision of a Profile, a modification of a
 * {@link Configuration}.
 * <p>
 * Higher level, the revisions of a {@link WorkflowBundle} would record addition
 * of a {@link Workflow} (which itself would have a separate Revision chain) -
 * in this case {@link #getHadOriginalSources()} can indicate the workflow
 * bundle that nested workflow and its configurations came from.
 * <p>
 * Revisions might be given a custom {@link #getChangeSpecificationType()} to
 * indicate a particular kind of edit, for instance insertion of a nested
 * workflow.
 * 
 * @author Stian Soiland-Reyes
 */
public class Revision extends AbstractCloneable implements WorkflowBean {
	private Set<URI> additionOf = new LinkedHashSet<>();
	private URI changeSpecificationType;
	private Calendar generatedAtTime;
	private Set<Revision> hadOriginalSources = new LinkedHashSet<>();
	private URI identifier;
	private Set<URI> modificationsOf = new LinkedHashSet<>();
	private Revision previousRevision;
	private Set<URI> removalOf = new LinkedHashSet<>();
	private Set<URI> wasAttributedTo = new LinkedHashSet<>();

	public Revision() {
	}

	public Revision(URI identifier, Revision previousRevision) {
		this.identifier = identifier;
		this.previousRevision = previousRevision;
	}

	public Set<URI> getAdditionOf() {
		return additionOf;
	}

	public URI getChangeSpecificationType() {
		return changeSpecificationType;
	}

	public Calendar getGeneratedAtTime() {
		return generatedAtTime;
	}

	public Set<Revision> getHadOriginalSources() {
		return hadOriginalSources;
	}

	public URI getIdentifier() {
		return identifier;
	}

	public Set<URI> getModificationsOf() {
		return modificationsOf;
	}

	public Revision getPreviousRevision() {
		return previousRevision;
	}

	public Set<URI> getRemovalOf() {
		return removalOf;
	}

	public Set<URI> getWasAttributedTo() {
		return wasAttributedTo;
	}

	public void setAdditionOf(Set<URI> additionOf) {
		this.additionOf.clear();
		this.additionOf.addAll(additionOf);
	}

	public void setChangeSpecificationType(URI changeSpecificationType) {
		this.changeSpecificationType = changeSpecificationType;
	}

	public void setGeneratedAtTime(Calendar generatedAtTime) {
		this.generatedAtTime = generatedAtTime;
	}

	public void setHadOriginalSources(Set<Revision> hadOriginalSources) {
		this.hadOriginalSources.clear();
		this.hadOriginalSources.addAll(hadOriginalSources);
	}

	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}

	public void setModificationsOf(Set<URI> modificationsOf) {
		this.modificationsOf.clear();
		this.modificationsOf.addAll(modificationsOf);
	}

	public void setPreviousRevision(Revision previousRevision) {
		this.previousRevision = previousRevision;
	}

	public void setRemovalOf(Set<URI> removalOf) {
		this.removalOf.clear();
		this.removalOf.addAll(removalOf);
	}

	public void setWasAttributedTo(Set<URI> wasAttributedTo) {
		this.wasAttributedTo.clear();
		this.wasAttributedTo.addAll(wasAttributedTo);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return accept(visitor, new HashSet<Revision>());
	}

	protected boolean accept(Visitor visitor, HashSet<Revision> visited) {
		if (!visited.add(this))
			// Ignore this Revision, visitor has already seen it
			return true;
		boolean recurse = visitor.visitEnter(this);
		if (recurse) {
			if (getPreviousRevision() != null)
				recurse = getPreviousRevision().accept(visitor, visited);
			for (Revision rev : getHadOriginalSources()) {
				if (!recurse)
					break;
				recurse = rev.accept(visitor, visited);
			}
		}
		return visitor.visitLeave(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getIdentifier();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Revision))
			return false;
		Revision other = (Revision) obj;
		if (getIdentifier() == null)
			return obj == this;
		return getIdentifier().equals(other.getIdentifier());
	}

	@Override
	public int hashCode() {
		if (getIdentifier() == null)
			return 0x01234567;
		return 0x01234567 ^ getIdentifier().hashCode();
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		Revision cloneRevision = (Revision) clone;
		cloneRevision.setAdditionOf(new LinkedHashSet<URI>(getAdditionOf()));
		cloneRevision.setChangeSpecificationType(getChangeSpecificationType());
		if (getGeneratedAtTime() != null)
			cloneRevision.setGeneratedAtTime((Calendar) getGeneratedAtTime()
					.clone());
		for (Revision source : getHadOriginalSources())
			cloneRevision.getHadOriginalSources().add(
					cloning.cloneIfNotInCache(source));
		cloneRevision.setIdentifier(getIdentifier());
		cloneRevision.setModificationsOf(new LinkedHashSet<URI>(
				getModificationsOf()));
		cloneRevision.setPreviousRevision(cloning
				.cloneIfNotInCache(getPreviousRevision()));
		cloneRevision.setRemovalOf(new LinkedHashSet<URI>(getRemovalOf()));
		cloneRevision.setWasAttributedTo(new LinkedHashSet<URI>(
				getWasAttributedTo()));
	}

	// Derived operations

	/**
	 * Get the URI of this revision.
	 * 
	 * @return The absolute URI.
	 * @see URITools#uriForBean(WorkflowBean)
	 */
	public URI getURI() {
		return getUriTools().uriForBean(this);
	}

	/**
	 * Get the URI of this revision relative to another workflow element.
	 * 
	 * @return The relative URI.
	 * @see URITools#relativeUriForBean(WorkflowBean,WorflowBean)
	 */
	public URI getRelativeURI(WorkflowBean relativeTo) {
		return getUriTools().relativeUriForBean(this, relativeTo);
	}
}
