package net.sf.taverna.scufl2.api.common;

public class ConfigurableProperty extends AbstractNamed {

	public ConfigurableProperty(String name) {
		super(name);
	}

	private String complianceLevel;

	private Object defaultValue;

	private String description;

	private Object mandatoryStatus;

	public String getComplianceLevel() {
		return complianceLevel;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
	public String getDescription() {
		return description;
	}
	public Object getMandatoryStatus() {
		return mandatoryStatus;
	}

	public void setComplianceLevel(String complianceLevel) {
		this.complianceLevel = complianceLevel;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMandatoryStatus(Object mandatoryStatus) {
		this.mandatoryStatus = mandatoryStatus;
	}

}
