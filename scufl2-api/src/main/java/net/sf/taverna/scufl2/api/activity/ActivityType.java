package net.sf.taverna.scufl2.api.activity;

import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.common.AbstractNamed;

public class ActivityType extends AbstractNamed {

	/**
	 * ActivityType specifies the type of one or more Activities. It has not yet
	 * been decided how specific an ActivityType is. For example, it could
	 * specify that the Activity calls a WSDL operation or it could specify a
	 * specific WSDL operation.
	 * 
	 * Note that depending on the decision with regard to the specificity of the
	 * ActivityType it may specify ConfigurableProperties that are inherited by
	 * the Activitys.
	 * 
	 * @param name
	 */
	public ActivityType(String name) {
		super(name);
	}
	
	public ActivityType() {
		super();
	}

}
