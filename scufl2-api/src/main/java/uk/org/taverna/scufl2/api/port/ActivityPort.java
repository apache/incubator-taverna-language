package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Child;

/**
 * A <code>Port</code> that specifies the data consumed or produced by an
 * {@link Activity}.
 */
public interface ActivityPort extends Port, Child<Activity> {

}
