package org.apache.taverna.scufl2.rdfxml.impl;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


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
