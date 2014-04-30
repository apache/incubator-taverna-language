package uk.org.taverna.scufl2.api.common;

import java.util.regex.Pattern;

/**
 * A named {@link WorkflowBean}.
 * 
 * @author Alan R Williams
 */
@SuppressWarnings("rawtypes")
public interface Named extends WorkflowBean, Comparable {

    /**
     * Name must not match this regular expression, e.g. must not include:
     * slash (/), colon (:), ASCII control characters
     * 
     */
    public static final Pattern INVALID_NAME = Pattern
            .compile("^$|[/:\\x7f\\x00-\\x1f]");
    
	/**
	 * Returns the name of the {@link WorkflowBean}.
	 * 
	 * @return  the name of the <code>WorkflowBean</code>
	 */
	public String getName();

	    /**
     * Sets the name of the {@link WorkflowBean}.
     * 
     * The name <strong>must not</strong> be <code>null</code>, not be an empty
     * String, and must not match the {@link #INVALID_NAME} regular expression.
     * 
     * @param name
     *            the name of the <code>WorkflowBean</code>
     */
	public void setName(String name);

}
