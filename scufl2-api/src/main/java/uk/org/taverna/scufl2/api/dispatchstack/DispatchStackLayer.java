package uk.org.taverna.scufl2.api.dispatchstack;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.Visitor;

public class DispatchStackLayer implements Typed, Child<DispatchStack>,Configurable {

	private DispatchStack parent;
	private URI configurableType;

	public DispatchStackLayer(DispatchStack parent, URI configurableType) {
		setParent(parent);
		setConfigurableType(configurableType);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public URI getConfigurableType() {
		return configurableType;
	}


	@Override
	public DispatchStack getParent() {
		return parent;
	}

	@Override
	public void setConfigurableType(URI type) {
		configurableType = type;
	}

	@Override
	public void setParent(DispatchStack parent) {
		if (this.parent == parent) {
			return; // No more to do!
		}
		if (this.parent != null) {
			this.parent.remove(this);
		}
		this.parent = parent;
		if (parent != null && !parent.contains(this)) {
			parent.add(this); // Just add to the end
		}
	}

}
