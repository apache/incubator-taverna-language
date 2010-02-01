package net.sf.taverna.scufl2.api.common;

/**
 * @author alanrw
 *
 */
public class ConfigurableProperty extends AbstractNamed {

	/**
	 * @param name
	 */
	public ConfigurableProperty(String name) {
		super(name);
	}

	private String complianceLevel;

	private Object defaultValue;

	private String description;

	private Object mandatoryStatus;

	/**
	 * @return
	 */
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
