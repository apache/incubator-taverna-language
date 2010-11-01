package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;

public class DataProperty extends Property {

	public static URI XSD = URI.create("http://www.w3.org/2001/XMLSchema#");
	public static URI XSD_STRING = XSD.resolve("#string");

	private String dataValue = "";
	private URI dataValueType = XSD_STRING;

	public DataProperty() {
	}

	public DataProperty(URI predicate, String dataValue) {
		setPredicate(predicate);
		setDataValue(dataValue);
	}

	public DataProperty(URI predicate, String dataValue, URI dataValueType) {
		setPredicate(predicate);
		setDataValue(dataValue);
		setDataValueType(dataValueType);
	}

	public String getDataValue() {
		return dataValue;
	}

	public URI getDataValueType() {
		return dataValueType;
	}

	public void setDataValue(String dataValue) {
		if (dataValue == null) {
			throw new NullPointerException("Value can't be null");
		}
		this.dataValue = dataValue;
	}

	public void setDataValueType(URI dataValueType) {
		if (dataValueType == null) {
			throw new NullPointerException(
			"Data value type can't be null, try DataProperty.XSD_STRING");
		}
		this.dataValueType = dataValueType;
	}

	@Override
	protected void toStringObject(StringBuilder sb, String indent) {
		appendString(sb, dataValue);
		if (!getDataValueType().equals(XSD_STRING)) {
			sb.append("^^");
			appendUri(sb, getDataValueType());
		}
	}

}
