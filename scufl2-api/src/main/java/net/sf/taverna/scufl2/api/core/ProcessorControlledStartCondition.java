/**
 * 
 */
package net.sf.taverna.scufl2.api.core;

/**
 * @author alanrw
 *
 */
public class ProcessorControlledStartCondition extends StartCondition {
	private Processor controllingProcessor;

	/**
	 * @return
	 */
	public Processor getControllingProcessor() {
		return controllingProcessor;
	}

	/**
	 * @param controllingProcessor
	 */
	public void setControllingProcessor(Processor controllingProcessor) {
		this.controllingProcessor = controllingProcessor;
	}

}
