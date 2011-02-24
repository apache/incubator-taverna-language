package uk.org.taverna.scufl2.api.property;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.impl.LazyMap;

public class PropertyResource extends PropertyReference implements
		PropertyObject {

	public static class PropertyComparator implements
			Comparator<PropertyObject> {
		private static final List<Class<?>> CLASSORDERING = Arrays
				.<Class<?>> asList(PropertyLiteral.class, PropertyList.class,
						PropertyReference.class, PropertyResource.class);

		@Override
		public int compare(PropertyObject o1, PropertyObject o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1.getClass() != o2.getClass()) {
				return CLASSORDERING.indexOf(o1.getClass())
						- CLASSORDERING.indexOf(o2.getClass());
			}
			if (o1 instanceof PropertyLiteral) {
				return compareLiteral((PropertyLiteral) o1,
						(PropertyLiteral) o2);
			}
			if (o1 instanceof PropertyResource) {
				return compareResource((PropertyResource) o1,
						(PropertyResource) o2);
			}
			if (o1 instanceof PropertyReference) {
				// Remember to check PropertyReference *after* PropertyResource
				// which is a subclass of PropertyReference
				return compareReference((PropertyReference) o1,
						(PropertyReference) o2);
			}
			if (o1 instanceof PropertyList) {
				return compareList((PropertyList) o1, (PropertyList) o2);
			}
			throw new IllegalArgumentException(
					"Comparison of unsupported subclass " + o1.getClass());
		}

		private int compareIterators(Iterator<PropertyObject> it1,
				Iterator<PropertyObject> it2) {
			while (it1.hasNext() && it2.hasNext()) {
				int v = compare(it1.next(), it2.next());
				if (v != 0) {
					return v;
				}
			}
			if (it1.hasNext() && !it2.hasNext()) {
				return 1; // it2 is 'smaller' than us
			} else if (!it1.hasNext() && it2.hasNext()) {
				return -1;
			}
			return 0;

		}

		private int compareList(PropertyList o1, PropertyList o2) {
			return compareIterators(o1.iterator(), o2.iterator());
		}

		private int compareLiteral(PropertyLiteral o1, PropertyLiteral o2) {
			int v = o1.getLiteralValue().compareTo(o2.getLiteralValue());
			if (v != 0) {
				return v;
			}
			return compareWithNullCheck(o1.getLiteralType(),
					o2.getLiteralType());
		}

		private int compareMaps(Map<URI, SortedSet<PropertyObject>> map,
				Map<URI, SortedSet<PropertyObject>> map2) {
			TreeSet<URI> keys = new TreeSet<URI>();
			keys.addAll(map.keySet());
			keys.addAll(map2.keySet());

			for (URI uri : keys) {
				Set<PropertyObject> set1 = map.get(uri);
				Set<PropertyObject> set2 = map2.get(uri);
				if (set1 == null && set2 == null) {
					continue;
				}
				if (set1 == null && set2 != null) {
					return 1; // set2 is there before us
				}
				if (set2 == null && set1 != null) {
					return -1;
				}

				Iterator<PropertyObject> it1 = set1.iterator();
				Iterator<PropertyObject> it2 = set2.iterator();
				int v = compareIterators(it1, it2);
				if (v != 0) {
					return v;
				}
			}
			// all (or none) equal
			return 0;
		}

		private int compareReference(PropertyReference o1, PropertyReference o2) {
			return compareWithNullCheck(o1.getResourceURI(),
					o2.getResourceURI());
		}

		private int compareResource(PropertyResource o1, PropertyResource o2) {
			int diff = compareWithNullCheck(o1.getResourceURI(),
					o2.getResourceURI());
			if (diff != 0) {
				return diff;
			}

			diff = compareWithNullCheck(o1.getTypeURI(), o2.getTypeURI());
			if (diff != 0) {
				return diff;
			}

			return compareMaps(o1.getProperties(), o2.getProperties());
		}

		private <T extends Comparable<T>> int compareWithNullCheck(T o1, T o2) {
			if (o1 == o2 || o1 == null && o2 == null) {
				return 0;
			}
			if (o1 != null && o2 == null) {
				// Null is 'more than' having a value
				return -1;
			}
			if (o1 == null && o2 != null) {
				return 1;
			}
			return o1.compareTo(o2);
		}

	}

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

		@Override
		public String toString() {
			return "PropertyVisit " + getPredicateUri();
		}

	}

	private URI typeURI;

	private final Map<URI, SortedSet<PropertyObject>> properties = new LazyMap<URI, SortedSet<PropertyObject>>() {
		private static final long serialVersionUID = 1L;

		@Override
		public SortedSet<PropertyObject> getDefault(URI key) {
			return new TreeSet<PropertyObject>(new PropertyComparator());
		}
	};

	public PropertyResource() {
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			Set<URI> uris = getProperties().keySet();
			for (URI uri : uris) {
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

	public final Map<URI, SortedSet<PropertyObject>> getProperties() {
		return properties;
	}

	public SortedSet<PropertyLiteral> getPropertiesAsLiterals(URI predicate)
			throws UnexpectedPropertyException {
		return getPropertiesOfType(predicate, PropertyLiteral.class);
	}

	public SortedSet<PropertyReference> getPropertiesAsReferences(URI predicate)
			throws UnexpectedPropertyException {
		return getPropertiesOfType(predicate, PropertyReference.class);
	}

	public SortedSet<PropertyResource> getPropertiesAsResources(URI predicate)
			throws UnexpectedPropertyException {
		return getPropertiesOfType(predicate, PropertyResource.class);
	}

	public SortedSet<URI> getPropertiesAsResourceURIs(URI predicate)
			throws UnexpectedPropertyException {
		SortedSet<URI> uris = new TreeSet<URI>();
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

	public SortedSet<String> getPropertiesAsStrings(URI predicate)
			throws UnexpectedPropertyException {
		SortedSet<String> strings = new TreeSet<String>();
		for (PropertyLiteral literal : getPropertiesAsLiterals(predicate)) {
			strings.add(literal.getLiteralValue());
		}
		return strings;
	}

	public <PropertyType extends PropertyObject> SortedSet<PropertyType> getPropertiesOfType(
			URI predicate, Class<PropertyType> propertyType)
			throws UnexpectedPropertyException {
		SortedSet<PropertyType> properties = new TreeSet<PropertyType>(
				new PropertyComparator());
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

	public PropertyLiteral getPropertyAsLiteral(URI predicate) throws UnexpectedPropertyException, PropertyNotFoundException, MultiplePropertiesException {
		PropertyLiteral propertyResource = getPropertyOfType(predicate,
				PropertyLiteral.class);
		return propertyResource;
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

	public final void setProperties(
			Map<URI, SortedSet<PropertyObject>> properties) {
		this.properties.clear();
		this.properties.putAll(properties);
	}

	public final void setTypeURI(URI typeURI) {
		this.typeURI = typeURI;
	}

	@Override
	public String toString() {
		return "PropertyResource [getTypeURI()=" + getTypeURI()
				+ ", getResourceURI()=" + getResourceURI() + "]";
	}


}
