package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;

public class PropertyLiteral implements PropertyObject {

	public static URI XSD = URI.create("http://www.w3.org/2001/XMLSchema#");
	public static URI XSD_STRING = XSD.resolve("#string");

	private String literalValue = "";
	private URI literalType = XSD_STRING;

	public URI getLiteralType() {
		return literalType;
	}

	public String getLiteralValue() {
		return literalValue;
	}

	public void setLiteralType(URI literalType) {
		if (literalType == null) {
			throw new NullPointerException(
			"Data value type can't be null, try PropertyLiteral");
		}
		this.literalType = literalType;
	}

	public void setLiteralValue(String literalValue) {
		if (literalValue == null) {
			throw new NullPointerException("Value can't be null");
		}
		this.literalValue = literalValue;
	}

}
