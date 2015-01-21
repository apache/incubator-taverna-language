package org.apache.taverna.scufl2.validation.correctness.report;

import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.validation.ValidationProblem;


/**
 * @author alanrw
 * 
 */
public class PortMissingFromIterationStrategyStackProblem extends
		ValidationProblem {
	private final Port port;

	public PortMissingFromIterationStrategyStackProblem(Port port,
			IterationStrategyStack iterationStrategyStack) {
		super(iterationStrategyStack);
		this.port = port;
	}

	/**
	 * @return the port
	 */
	public Port getPort() {
		return port;
	}

	@Override
	public String toString() {
		return getBean() + " does not include " + port;
	}
}