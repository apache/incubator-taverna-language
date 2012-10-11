package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.GregorianCalendar;
import java.util.UUID;

import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.annotation.Revisioned;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public abstract class AbstractRevisioned extends AbstractNamedChild implements Revisioned {

	

	private Revision currentRevision;

	public URI generateIdentifier() {
		return getIdentifierRoot().resolve(UUID.randomUUID().toString() + "/");
	}

	protected abstract URI getIdentifierRoot();

	public AbstractRevisioned() {
		newRevision();
	}

	public AbstractRevisioned(String name) {
		super(name);
		newRevision();
	}

	public void setCurrentRevision(Revision currentRevision) {
		this.currentRevision = currentRevision;
		if (currentRevision == null) {
			newRevision();		
		}
	}

	@Override
	public Revision newRevision() {
		return newRevision(null);
	}

	@Override
	public Revision newRevision(URI revisionIdentifier) {
		GregorianCalendar created = null;
		if (revisionIdentifier == null) {
			revisionIdentifier = generateIdentifier();
			created = new GregorianCalendar();
		}
		Revision newRevision = new Revision(revisionIdentifier,
				getCurrentRevision());
		newRevision.setCreated(created);
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
	 * The the default identifier is based on #getIdentifierRoot() plus a random UUID.
	 * 
	 * @see {@link #setIdentifier(URI)}
	 * 
	 * @return the identifier
	 */
	@Override
	public URI getIdentifier() {
		if (getCurrentRevision() == null) {
			return null;
		}
		return getCurrentRevision().getResourceURI();
	}

	/**
	 * Set the identifier.
	 * <p>
	 * This will delete any previous revisions in {@link #getCurrentRevision()}
	 * 
	 * @see #getIdentifier()
	 * @see #getCurrentRevision()
	 * 
	 * @param workflowIdentifier
	 *            the identifier
	 */
	@Override
	public void setIdentifier(URI workflowIdentifier) {
		setCurrentRevision(new Revision(workflowIdentifier));
	}

}