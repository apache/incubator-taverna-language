package uk.org.taverna.scufl2.api.common;

import uk.org.taverna.scufl2.api.configurations.Configuration;

/**
 * {@link WorkflowBean WorkflowBeans} that can have a
 * {@link uk.org.taverna.scufl2.api.configurations.Configuration Configuration}.
 * <p>
 * Configurables are {@link Typed}, but note that this type is different from
 * the type of the {@link Configuration}.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public interface Configurable extends Typed {
}
