/**
 * 
 */
package net.sf.taverna.scufl2.api.reference;

import javax.xml.bind.annotation.XmlTransient;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.Named;

/**
 * @author alanrw
 *
 */
public final class Reference<T> {

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
		return id;
		
	}

}
