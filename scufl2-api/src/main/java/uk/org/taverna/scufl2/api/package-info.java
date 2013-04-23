/**
 * Scufl2 Workflow language API
 * <p>
 * A {@link uk.org.taverna.scufl2.api.container.WorkflowBundle} is
 * the main entry point for description of a workflow. It contains
 * several {@link uk.org.taverna.scufl2.api.core.Workflow Workflows}, one
 * of which is the
 * {@link uk.org.taverna.scufl2.api.container.WorkflowBundle#getMainWorkflow()}.
 * A workflow is configured for execution in a
 * {@link uk.org.taverna.scufl2.api.profiles.Profile Profile}, one of which is
 * the {@link uk.org.taverna.scufl2.api.container.WorkflowBundle#getMainProfile()}.
 * <p>
 * You can load and save WorkflowBundle instances using 
 * {@link uk.org.taverna.scufl2.api.io.WorkflowBundleIO} as long as the 
 * implementations from the modules scufl2-t2flow and scufl2-rdfxml are
 * discoverable the classpath or available as OSGi services, alternatively
 * implementations of {@link uk.org.taverna.scufl2.api.io.WorkflowBundleReader}
 * and {@link uk.org.taverna.scufl2.api.io.WorkflowBundleWriter} can be used independently.
 * <p>
 * Also see <a href="http://dev.mygrid.org.uk/wiki/display/developer/SCUFL2+API">SCUFL 2 API in myGrid wiki</a> and 
 * the <a href="https://github.com/myGrid/scufl2">scufl2 github projecT</a>.
 *  
 *  @author Stian Soiland-Reyes
 *  @author Alan Williams
 *  @author David Withers
 *  @author Paolo Missier
 */
package uk.org.taverna.scufl2.api;

