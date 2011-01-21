package uk.org.taverna.scufl2.api.core;

/**
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 */
public class RunAfterCondition extends ProcessorControlledStartCondition {

	public RunAfterCondition(Processor parent, Processor runAfter) {
		setParent(parent);
		setControllingProcessor(runAfter);
	}

}
