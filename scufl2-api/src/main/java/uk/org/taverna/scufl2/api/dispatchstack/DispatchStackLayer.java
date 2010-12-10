package uk.org.taverna.scufl2.api.dispatchstack;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Typed;

public class DispatchStackLayer implements Typed, Child<DispatchStack>,
Configurable {

	private DispatchStack parent;
	private URI configurableType;

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
		this.parent = parent;
	}

}
