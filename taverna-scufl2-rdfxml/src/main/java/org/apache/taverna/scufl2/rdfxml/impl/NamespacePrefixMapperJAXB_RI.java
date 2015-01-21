package org.apache.taverna.scufl2.rdfxml.impl;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class NamespacePrefixMapperJAXB_RI extends NamespacePrefixMapper {
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		switch (namespaceUri) {
		case "http://www.w3.org/2001/XMLSchema-instance":
			return "xsi";
		case "http://ns.taverna.org.uk/2010/scufl2#":
			return ""; // default
		case "http://www.w3.org/1999/02/22-rdf-syntax-ns#":
			return "rdf";
		case "http://www.w3.org/2000/01/rdf-schema#":
			return "rdfs";
		case "http://purl.org/dc/elements/1.1/":
			return "dc";
		case "http://purl.org/dc/terms/":
			return "dcterms";
		case "http://www.w3.org/2002/07/owl#":
			return "owl";
		default:
			return suggestion;
		}
	}

	@Override
	public String[] getPreDeclaredNamespaceUris() {
		return new String[] {};
	}
}
