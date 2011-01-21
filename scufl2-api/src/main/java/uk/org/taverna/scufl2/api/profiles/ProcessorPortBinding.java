package uk.org.taverna.scufl2.api.profiles;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.ProcessorPort;

public interface ProcessorPortBinding<A extends ActivityPort, P extends ProcessorPort>
		extends Child<ProcessorBinding> {

	A getBoundActivityPort();

	P getBoundProcessorPort();

	void setBoundActivityPort(A boundActivityPort);

	void setBoundProcessorPort(P boundProcessorPort);

}
