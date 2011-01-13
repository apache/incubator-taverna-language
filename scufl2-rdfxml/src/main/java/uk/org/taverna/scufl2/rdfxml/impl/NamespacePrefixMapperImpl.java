package uk.org.taverna.scufl2.rdfxml.impl;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

@SuppressWarnings("restriction")
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		if (namespaceUri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
			return "xsi";
		}
		if (namespaceUri.equals("http://ns.taverna.org.uk/2010/scufl2#")) {
			return ""; // default
		}
		if (namespaceUri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#")) {
			return "rdf";
		}
		if (namespaceUri.equals("http://www.w3.org/2000/01/rdf-schema#")) {
			return "rdfs";
		}
		if (namespaceUri.equals("http://purl.org/dc/elements/1.1/")) {
			return "dc";
		}
		if (namespaceUri.equals("http://purl.org/dc/terms/")) {
			return "dcterms";
		}
		if (namespaceUri.equals("http://www.w3.org/2002/07/owl#")) {
			return "owl";
		}
		return suggestion;
	}

	public String[] getPreDeclaredNamespaceUris() {
		return new String[] {  };
	}

}
