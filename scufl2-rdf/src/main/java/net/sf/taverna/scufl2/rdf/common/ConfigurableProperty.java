package net.sf.taverna.scufl2.rdf.common;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "ConfigurableProperty")
public interface ConfigurableProperty extends Named {

	@rdf(Ontology.CORE + "description")
	public String getDescription();

	@rdf(Ontology.CORE + "complianceLevel")
	public String getComplianceLevel();

	@rdf(Ontology.CORE + "defaultValue")
	public Object getDefaultValue();

	@rdf(Ontology.CORE + "mandatoryStatus")
	public Object getMandatoryStatus();

	public void setDescription(String description);

	public void setComplianceLevel(String complianceLevel);

	public void setDefaultValue(Object defaultValue);

	public void setMandatoryStatus(Object mandatoryStatus);
	
}
