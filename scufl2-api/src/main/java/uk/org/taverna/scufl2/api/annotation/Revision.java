package uk.org.taverna.scufl2.api.annotation;

import java.net.URI;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;

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
 * </p>
 * <p>
 * Revisions might be given a custom {@link #getChangeSpecificationType()} to
 * indicate a particular kind of edit, for instance insertion of a nested
 * workflow.
 * </p>
 * 
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class Revision implements WorkflowBean {

	private Set<URI> additionOf;
	private URI changeSpecificationType;
	private Calendar generatedAtTime;
	private Set<Revision> hadOriginalSources;
	private URI identifier;

	private Set<URI> modificationsOf;

	private Revision previousRevision;

	private Set<URI> removalOf;

	private Set<URI> wasAttributedTo;

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
		this.additionOf = additionOf;
	}

	public void setChangeSpecificationType(URI changeSpecificationType) {
		this.changeSpecificationType = changeSpecificationType;
	}

	public void setGeneratedAtTime(Calendar generatedAtTime) {
		this.generatedAtTime = generatedAtTime;
	}

	public void setHadOriginalSources(Set<Revision> hadOriginalSources) {
		this.hadOriginalSources = hadOriginalSources;
	}

	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}

	public void setModificationsOf(Set<URI> modificationsOf) {
		this.modificationsOf = modificationsOf;
	}

	public void setPreviousRevision(Revision previousRevision) {
		this.previousRevision = previousRevision;
	}

	public void setRemovalOf(Set<URI> removalOf) {
		this.removalOf = removalOf;
	}

	public void setWasAttributedTo(Set<URI> wasAttributedTo) {
		this.wasAttributedTo = wasAttributedTo;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return accept(visitor, new HashSet<Revision>());
	}

	protected boolean accept(Visitor visitor, HashSet<Revision> visited) {
		if (!visited.add(this)) {
			// Ignore this Revision, visitor has already seen it
			return true;
		}
		boolean recurse = visitor.visitEnter(this);
		if (recurse) {
			if (getPreviousRevision() != null) {
				recurse = getPreviousRevision().accept(visitor, visited);
			}
			for (Revision rev : getHadOriginalSources()) {
				if (! recurse) {
					break;
				}
				recurse = rev.accept(visitor, visited);						
			}
		}
		return visitor.visitLeave(this);	
	}

}
