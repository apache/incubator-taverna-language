package uk.org.taverna.scufl2.api.annotation;

import java.net.URI;
import java.util.Calendar;
import java.util.Set;

import uk.org.taverna.scufl2.api.property.MultiplePropertiesException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyNotFoundException;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.UnexpectedPropertyException;

public class Revision extends PropertyResource {

	/**
	 * Based on working draft http://www.w3.org/TR/2012/WD-prov-o-20120503/
	 * 
	 * TODO: Update for PROV-O when released as spec
	 **/
	protected static final URI PROV = URI.create("http://www.w3.org/ns/prov#");

	protected static final URI AT_TIME = PROV.resolve("#atTime");
	protected static final URI ENTITY = PROV.resolve("#Entity");
	protected static final URI GENERATION = PROV.resolve("#Generation");

	protected static final URI QUALIFIED_GENERATION = PROV
			.resolve("#qualifiedGeneration");
	protected static final URI WAS_ATTRIBUTED_TO = PROV
			.resolve("#wasAttributedTo");
	protected static final URI WAS_REVISION_OF = PROV.resolve("#wasRevisionOf");

	public Revision() {
		this(null, null);
	}

	/**
	 * Cloning constructor
	 * 
	 * @param propertyAsResource
	 */
	protected Revision(PropertyResource propertyAsResource) {
		setTypeURI(propertyAsResource.getTypeURI());
		setResourceURI(propertyAsResource.getResourceURI());
		/*
		 * Note: setProperties() makes an implicit clone of the properties map
		 */
		setProperties(propertyAsResource.getProperties());
	}

	public Revision(URI uri) {
		this(uri, null);
	}

	public Revision(URI uri, Revision previous) {
		setResourceURI(uri);
		setTypeURI(ENTITY);
		setPreviousRevision(previous);
	}

	public void addCreator(URI creator) {
		addPropertyReference(WAS_ATTRIBUTED_TO, creator);
	}

	public Calendar getCreated() {
		PropertyResource generation;
		try {
			generation = getPropertyAsResource(QUALIFIED_GENERATION);
			return generation.getPropertyAsLiteral(AT_TIME)
					.getLiteralValueAsCalendar();
		} catch (PropertyNotFoundException e) {
			return null;
		} catch (UnexpectedPropertyException e) {
			throw new IllegalStateException(String.format("Invalid %s or %s",
					QUALIFIED_GENERATION, AT_TIME), e);
		} catch (MultiplePropertiesException e) {
			throw new IllegalStateException(String.format("Multiple %s or %s",
					QUALIFIED_GENERATION, AT_TIME), e);
		}
	}

	public Set<URI> getCreators() {
		try {
			return getPropertiesAsResourceURIs(WAS_ATTRIBUTED_TO);
		} catch (UnexpectedPropertyException e) {
			throw new IllegalStateException(String.format("Invalid %s",
					WAS_ATTRIBUTED_TO), e);
		}
	}

	public Revision getPreviousRevision() {
		try {
			PropertyObject property = getProperty(WAS_REVISION_OF);
			Revision revision;
			if (property instanceof Revision) {
				revision = (Revision) property;
			} else if (property instanceof PropertyResource) {
				// Clone it as Revision subclass
				revision = new Revision((PropertyResource) property);
				// Replace the plain PropertyResource so changes shine through
				setPreviousRevision(revision);
			} else if (property instanceof PropertyReference) {
				URI previous = ((PropertyReference) property).getResourceURI();
				revision = new Revision(previous);
				setPreviousRevision(revision);
			} else {
				throw new IllegalStateException(String.format("Invalid %s",
						WAS_REVISION_OF));
			}
			return revision;
		} catch (PropertyNotFoundException e) {
			return null;
		} catch (MultiplePropertiesException e) {
			throw new IllegalStateException(String.format("Multiple %s",
					WAS_REVISION_OF), e);
		}
	}

	public void setCreated(Calendar created) {
		PropertyResource generation;
		try {
			generation = getPropertyAsResource(QUALIFIED_GENERATION);
		} catch (PropertyNotFoundException e) {
			generation = addPropertyAsNewResource(QUALIFIED_GENERATION,
					GENERATION);
		} catch (UnexpectedPropertyException e) {
			throw new IllegalStateException(String.format("Invalid %s",
					QUALIFIED_GENERATION), e);
		} catch (MultiplePropertiesException e) {
			throw new IllegalStateException(String.format("Multiple %s",
					QUALIFIED_GENERATION), e);
		}
		generation.clearProperties(AT_TIME);
		generation.addProperty(AT_TIME, new PropertyLiteral(created));
	}

	public void setCreators(Set<URI> creators) {
		clearProperties(WAS_ATTRIBUTED_TO);
		for (URI creator : creators) {
			addCreator(creator);
		}
	}

	public void setPreviousRevision(Revision previous) {
		clearProperties(WAS_REVISION_OF);
		if (previous != null) {
			addProperty(WAS_REVISION_OF, previous);
		}
	}

	public void setPreviousRevision(URI previous) {
		clearProperties(WAS_REVISION_OF);
		if (previous != null) {
			addPropertyReference(WAS_REVISION_OF, previous);
		}		
	}

}
