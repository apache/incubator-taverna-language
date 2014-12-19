package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.port.AbstractGranularDepthPort;
import uk.org.taverna.scufl2.validation.ValidationProblem;

/**
 * @author alanrw
 */
public class IncompatibleGranularDepthProblem extends ValidationProblem {
	private final Integer depth;
	private final Integer granularDepth;

	public IncompatibleGranularDepthProblem(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth) {
		super(bean);
		this.depth = depth;
		this.granularDepth = granularDepth;
	}

	/**
	 * @return the depth
	 */
	public Integer getDepth() {
		return depth;
	}

	/**
	 * @return the granularDepth
	 */
	public Integer getGranularDepth() {
		return granularDepth;
	}

	@Override
	public String toString() {
		return getBean() + " has depth " + depth + " and granular depth "
				+ granularDepth;
	}
}