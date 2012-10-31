package uk.org.taverna.scufl2.api.property;

import java.net.URI;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;

/**
 * A special {@link Child} used by {@link PropertyResource#accept(Visitor)}
 * when visiting the map of {@link PropertyResource#getProperties()}
 *
 * @author Stian Soiland-Reyes
 *
 */
@SuppressWarnings("deprecation")
public class PropertyVisit extends AbstractCloneable 
	implements Child<PropertyResource>, uk.org.taverna.scufl2.api.property.PropertyResource.PropertyVisit {
	
	private PropertyResource parent;
	private URI predicateUri;

	public PropertyVisit() {	
	}
	
	
	public PropertyVisit(PropertyResource propertyResource, URI uri) {
		setParent(propertyResource);
		setPredicateUri(uri);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getParent() == null) ? 0 : getParent().hashCode());
		result = prime * result
				+ ((getPredicateUri() == null) ? 0 : getPredicateUri().hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PropertyVisit)) {
			return false;
		}
		PropertyVisit other = (PropertyVisit) obj;
		if (getParent() == null) {
			// We can't know
			return false;
		} else if (!getParent().equals(other.getParent())) {
			return false;
		}
		if (getPredicateUri() == null) {
			if (other.getPredicateUri() != null) {
				return false;
			}
		} else if (!getPredicateUri().equals(other.getPredicateUri())) {
			return false;
		}
		return true;
	}


	@Override
	public PropertyVisit clone() {			
		return AbstractCloneable.cloneWorkflowBean(this);	
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		PropertyVisit cloneVisit = new PropertyVisit();
		cloneVisit.setPredicateUri(getPredicateUri());		
	}

	@Override
	public PropertyResource getParent() {
		return parent;
	}

	public URI getPredicateUri() {
		return predicateUri;
	}

	public Set<PropertyObject> getPropertiesForPredicate() {
		return getPropertyResource().getProperties().get(getPredicateUri());
	}

	public PropertyResource getPropertyResource() {
		return parent;
	}

	@Override
	public void setParent(PropertyResource parent) {
		this.parent = parent;
	}

	public void setPredicateUri(URI predicateUri) {
		this.predicateUri = predicateUri;
	}

	@Override
	public String toString() {
		return "PropertyVisit " + getPredicateUri();
	}

}