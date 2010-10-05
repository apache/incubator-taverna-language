/**
 * 
 */
package uk.org.taverna.scufl2.api.core;



/**
 * @author Alan R Williams
 *
 */
public abstract class ProcessorControlledStartCondition extends StartCondition {
	
	private Processor controllingProcessor;

	public Processor getControllingProcessor() {
		return controllingProcessor;
	}

	public void setControllingProcessor(Processor controllingProcessor) {
		this.controllingProcessor = controllingProcessor;
	}
}
