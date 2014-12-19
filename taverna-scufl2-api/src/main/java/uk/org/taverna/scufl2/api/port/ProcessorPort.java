package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * A <code>Port</code> that specifies the data consumed or produced by an
 * {@link Processor}.
 *
 * @author Alan R Williams
 */
public interface ProcessorPort extends Port, Child<Processor> {
}
