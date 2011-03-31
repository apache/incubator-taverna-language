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
 * You can load a WorkflowBundle from a legacy .t2flow file
 * using {@link uk.org.taverna.scufl2.translator.t2flow.T2FlowParser T2FlowParser}.
 * 
 *  @author Stian Soiland-Reyes
 *  @author Alan Williams
 *  @author David Withers
 *  @author Paolo Missier
 */
package uk.org.taverna.scufl2.api;

