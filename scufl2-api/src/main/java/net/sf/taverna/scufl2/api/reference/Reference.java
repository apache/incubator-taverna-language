/**
 * 
 */
package net.sf.taverna.scufl2.api.reference;

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
		Reference<X> result = null;
		return result;
	}

}
