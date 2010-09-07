/**
 * 
 */
package uk.org.taverna.scufl2.api.reference;

import java.util.logging.Level;
import java.util.logging.Logger;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Named;


/**
 * @author Alan R Williams
 *
 */
public final class Reference<T> {

	private static Logger logger = Logger.getLogger(Reference.class.getCanonicalName());
	
	private String identification;

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}
	
	public T resolve() {
		// to be done
		return null;
	}
	
	public static <X> Reference<X> createReference(X object) {
		Reference<X> result = new Reference();
		result.setIdentification(getRoute(object));
		return result;
	}
	
	private static String getRoute(Object object) {
		String id = "unknown";
		if (object instanceof Named) {
			Named named = (Named) object;
			id = object.getClass().getSimpleName().toLowerCase() +
			"/" + named.getName();			
		}
		if (object instanceof Child) {
			Child child = (Child) object;
			id = getRoute(child.getParent()) + "/" + id;
		}
		if (id.contains("unknown")) {
			logger.log(Level.WARNING, "Unknown route for " + object + ": " + id, new Exception());
		}
		return id;
		
	}

}
