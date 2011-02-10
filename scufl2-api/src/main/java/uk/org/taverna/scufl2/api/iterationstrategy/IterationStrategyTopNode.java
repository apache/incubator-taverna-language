package uk.org.taverna.scufl2.api.iterationstrategy;

import java.util.List;

/**
 * @author Stian Soiland-Reyes
 *
 */
public interface IterationStrategyTopNode extends IterationStrategyNode,
		List<IterationStrategyNode>, IterationStrategyParent {

}
