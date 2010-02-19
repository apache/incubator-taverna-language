package net.sf.taverna.scufl2.api.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

/**
 * @author alanrw
 *
 */
@XmlType (propOrder = {"complianceLevel", "defaultValue", "description", "mandatoryStatus"})
public class ConfigurableProperty extends AbstractNamed {
	
	/**
	 * @param name
	 */
	public ConfigurableProperty(String name) {
		super(name);
	}
	
	public ConfigurableProperty() {
		super();
	}

	private String complianceLevel;

	private Object defaultValue;

	private String description;

	private Object mandatoryStatus;

	/**
	 * @return
	 */
	@XmlElement(required=true,nillable=false)
	public String getComplianceLevel() {
		return complianceLevel;
	}

	/**
	 * @return
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return
	 */
	@XmlElement(required=true,nillable=false)
	public Object getMandatoryStatus() {
		return mandatoryStatus;
	}

	/**
	 * @param complianceLevel
	 */
	public void setComplianceLevel(String complianceLevel) {
		this.complianceLevel = complianceLevel;
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param mandatoryStatus
	 */
	public void setMandatoryStatus(Object mandatoryStatus) {
		this.mandatoryStatus = mandatoryStatus;
	}

}
