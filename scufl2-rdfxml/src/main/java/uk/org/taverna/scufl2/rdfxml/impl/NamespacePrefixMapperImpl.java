package uk.org.taverna.scufl2.rdfxml.impl;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

@SuppressWarnings("restriction")
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {
	
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri)) {
			return "xsi";
		}
		if ("http://ns.taverna.org.uk/2010/scufl2#".equals(namespaceUri)) {
			return "scufl2";
		}
		return suggestion;
	}

	public String[] getPreDeclaredNamespaceUris() {
		return new String[] { "urn:xsi", "urn:scufl2" };
	}

}
