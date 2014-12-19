package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * A <code>Port</code> that specifies the data consumed or produced by an
 * {@link Workflow}.
 *
 * @author Alan R Williams
 */
public interface WorkflowPort extends Port, Child<Workflow> {

}
