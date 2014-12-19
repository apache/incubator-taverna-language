package uk.org.taverna.scufl2.api.core;

import uk.org.taverna.scufl2.api.common.Child;

/**
 * A link between two workflow components where one component controls the other in some way.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
@SuppressWarnings("rawtypes")
public interface ControlLink extends Child<Workflow>, Comparable {

}
