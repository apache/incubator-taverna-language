/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.validation.ValidationProblem;

public class MismatchConfigurableTypeProblem extends ValidationProblem {
	private final Configurable configurable;

	public MismatchConfigurableTypeProblem(Configuration configuration,
			Configurable configurable) {
		super(configuration);
		this.configurable = configurable;
	}

	/**
	 * @return the configurable
	 */
	public Configurable getConfigurable() {
		return configurable;
	}

	@Override
	public String toString() {
		return "The types of " + getBean() + " and " + configurable
				+ " are mismatched";
	}
}