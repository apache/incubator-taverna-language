package uk.org.taverna.scufl2.api.dispatchstack;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Typed;

public class DispatchStackLayer implements Typed, Child<DispatchStack>,
		Configurable {

	private DispatchStack parent;
	private URI type;

	@Override
	public DispatchStack getParent() {
		return parent;
	}

	@Override
	public URI getType() {
		return type;
	}

	@Override
	public void setParent(DispatchStack parent) {
		this.parent = parent;
	}

	@Override
	public void setType(URI type) {
		this.type = type;

	}

}
