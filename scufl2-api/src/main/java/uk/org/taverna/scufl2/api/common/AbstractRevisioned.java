package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.GregorianCalendar;
import java.util.UUID;

import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.annotation.Revisioned;

public abstract class AbstractRevisioned extends AbstractNamed implements Revisioned {

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
		return getCurrentRevision().getIdentifier();
	}

	/**
	 * Set the identifier.
	 * <p>
	 * This will delete any previous revisions in {@link #getCurrentRevision()}
	 * 
	 * @see #getIdentifier()
	 * @see #getCurrentRevision()
	 * 
	 * @param identifier
	 *            the identifier
	 */
	@Override
	public void setIdentifier(URI identifier) {
		setCurrentRevision(new Revision(identifier, null));
	}

}