package uk.org.taverna.scufl2.api.property;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.impl.LazyMap;

public class PropertyResource extends PropertyReference implements PropertyObject {
	/**
	 * A special {@link Child} used by {@link PropertyResource#accept(Visitor)}
	 * when visiting the map of {@link PropertyResource#getProperties()}
	 *
	 * @author Stian Soiland-Reyes
	 *
	 */
	public class PropertyVisit implements Child<PropertyResource> {

		private final URI predicateUri;

		PropertyVisit(URI uri) {
			predicateUri = uri;
		}

		@Override
		public boolean accept(Visitor visitor) {
			if (visitor.visitEnter(this)) {
				for (PropertyObject po : getPropertiesForPredicate()) {
					if (!po.accept(visitor)) {
						break;
					}
				}
			}
			return visitor.visitLeave(this);
		}

		@Override
		public PropertyResource getParent() {
			return PropertyResource.this;
		}

		public URI getPredicateUri() {
			return predicateUri;
		}

		public Set<PropertyObject> getPropertiesForPredicate() {
			return getProperties().get(predicateUri);
		}

		@Override
		public void setParent(PropertyResource parent) {
			throw new UnsupportedOperationException();
		}

	}

	private URI typeURI;

	private final Map<URI, Set<PropertyObject>> properties = new LazyMap<URI, Set<PropertyObject>>() {
		private static final long serialVersionUID = 1L;

		@Override
		public Set<PropertyObject> getDefault(URI key) {
			return new LinkedHashSet<PropertyObject>();
		}
	};

	public PropertyResource() {
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (URI uri : getProperties().keySet()) {
				if (!new PropertyVisit(uri).accept(visitor)) {
					break;
				}
			}
		}
		return visitor.visitLeave(this);
	}

	public void addProperty(URI predicate, PropertyObject object) {
		getProperties().get(predicate).add(object);
	}

	public PropertyResource addPropertyAsNewResource(URI predicate, URI typeURI) {
		PropertyResource resource = new PropertyResource();
		resource.setTypeURI(typeURI);
		addProperty(predicate, resource);
		return resource;
	}

	public void addPropertyAsString(URI predicate, String value) {
		addProperty(predicate, new PropertyLiteral(value));
	}

	public void addPropertyReference(URI predicate, URI resourceURI) {
		addProperty(predicate, new PropertyReference(resourceURI));
	}

	public final Map<URI, Set<PropertyObject>> getProperties() {
		return properties;
	}

	public Set<PropertyLiteral> getPropertiesAsLiterals(URI predicate)
			throws UnexpectedPropertyException {
		return getPropertiesOfType(predicate, PropertyLiteral.class);
	}

	public Set<PropertyReference> getPropertiesAsReferences(URI predicate)
	throws UnexpectedPropertyException {
		return getPropertiesOfType(predicate, PropertyReference.class);
}

	public Set<PropertyResource> getPropertiesAsResources(URI predicate)
			throws UnexpectedPropertyException {
		return getPropertiesOfType(predicate, PropertyResource.class);
	}

	public Set<URI> getPropertiesAsResourceURIs(URI predicate)
			throws UnexpectedPropertyException {
		Set<URI> uris = new HashSet<URI>();
		for (PropertyReference resource : getPropertiesAsReferences(predicate)) {
			URI uri = resource.getResourceURI();
			if (uri == null) {
				throw new UnexpectedPropertyException(
						"Resource property without URI for " + predicate
								+ " in " + this + ": " + resource, predicate,
						this);
			}
			uris.add(uri);
		}
		return uris;
	}

	public Set<String> getPropertiesAsStrings(URI predicate)
			throws UnexpectedPropertyException {
		Set<String> strings = new HashSet<String>();
		for (PropertyLiteral literal : getPropertiesAsLiterals(predicate)) {
			strings.add(literal.getLiteralValue());
		}
		return strings;
	}

	public <PropertyType extends PropertyObject> Set<PropertyType> getPropertiesOfType(
			URI predicate, Class<PropertyType> propertyType)
			throws UnexpectedPropertyException {
		Set<PropertyType> properties = new HashSet<PropertyType>();
		for (PropertyObject obj : getProperties().get(predicate)) {
			if (!propertyType.isInstance(obj)) {
				throw new UnexpectedPropertyException("Not a " + propertyType
						+ ": " + predicate + " in " + this, predicate, this);
			}
			properties.add(propertyType.cast(obj));
		}
		return properties;
	}

	public PropertyObject getProperty(URI predicate)
			throws PropertyNotFoundException, MultiplePropertiesException {
		PropertyObject foundProperty = null;
		// Could have checked set's size() - but it's
		for (PropertyObject obj : getProperties().get(predicate)) {
			if (foundProperty != null) {
				throw new MultiplePropertiesException(predicate, this);
			}
			foundProperty = obj;
		}
		if (foundProperty == null) {
			throw new PropertyNotFoundException(predicate, this);
		}
		return foundProperty;
	}

	public PropertyReference getPropertyAsReference(URI predicate)
			throws UnexpectedPropertyException, PropertyNotFoundException,
			MultiplePropertiesException {
		PropertyReference propertyResource = getPropertyOfType(predicate,
				PropertyReference.class);
		return propertyResource;
	}

	public URI getPropertyAsResourceURI(URI predicate)
			throws UnexpectedPropertyException, PropertyNotFoundException,
			MultiplePropertiesException {
		PropertyReference propertyResource = getPropertyOfType(predicate,
				PropertyReference.class);
		URI uri = propertyResource.getResourceURI();
		if (uri == null) {
			throw new UnexpectedPropertyException(
					"Resource property without URI for " + predicate + " in "
							+ this + ": " + propertyResource, predicate, this);
		}
		return uri;
	}

	public String getPropertyAsString(URI predicate) throws PropertyException {
		PropertyLiteral propertyLiteral = getPropertyOfType(predicate,
				PropertyLiteral.class);
		return propertyLiteral.getLiteralValue();
	}

	public <PropertyType extends PropertyObject> PropertyType getPropertyOfType(
			URI predicate, Class<PropertyType> propertyType)
			throws UnexpectedPropertyException, PropertyNotFoundException,
			MultiplePropertiesException {
		PropertyObject propObj = getProperty(predicate);
		if (!propertyType.isInstance(propObj)) {
			throw new UnexpectedPropertyException("Not a " + propertyType
					+ ": " + predicate + " in " + this, predicate, this);
		}
		return propertyType.cast(propObj);
	}

	public final URI getTypeURI() {
		return typeURI;
	}

	public final void setProperties(Map<URI, Set<PropertyObject>> properties) {
		this.properties.clear();
		this.properties.putAll(properties);
	}

	public final void setTypeURI(URI typeURI) {
		this.typeURI = typeURI;
	}

}
