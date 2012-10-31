package uk.org.taverna.scufl2.api.property;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;

/**
 * A {@link PropertyObject} representing a reference to a value.
 */
public class PropertyReference extends AbstractCloneable implements PropertyObject {

	private URI resourceURI;

	public PropertyReference() {
		super();
	}

	public PropertyReference(URI resourceURI) {
		this.resourceURI = resourceURI;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public final URI getResourceURI() {
		return resourceURI;
	}

	public final void setResourceURI(URI resourceURI) {
		this.resourceURI = resourceURI;
	}

	@Override
	public String toString() {
		return "PropertyReference [getResourceURI()=" + getResourceURI() + "]";
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		PropertyReference clonePropRef = (PropertyReference) clone;
		clonePropRef.setResourceURI(getResourceURI());		
	}

}