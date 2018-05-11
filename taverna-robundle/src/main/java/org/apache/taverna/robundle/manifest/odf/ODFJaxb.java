package org.apache.taverna.robundle.manifest.odf;

import static java.util.logging.Level.FINE;

import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/*
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
 */

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import org.apache.taverna.robundle.xml.odf.manifest.ObjectFactory;

/**
 * JAXB bindings for ODF manifest.xml / container.xml
 *
 */
public class ODFJaxb {

	private static Logger logger = Logger.getLogger(ODFJaxb.class.getCanonicalName());

	protected final ObjectFactory manifestFactory = new ObjectFactory();

	private static JAXBContext jaxbContext;
	private static boolean warnedPrefixMapper;

	public static class ManifestNamespacePrefixMapperJAXB_RI extends NamespacePrefixMapper {
		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return super.getPreDeclaredNamespaceUris();
		}

		@Override
		public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
			switch (namespaceUri) {
			case "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0":
				return "manifest";
			case "urn:oasis:names:tc:opendocument:xmlns:container":
				return "";
			case "http://www.w3.org/2000/09/xmldsig#":
				return "ds";
			case "http://www.w3.org/2001/04/xmlenc#":
				return "enc";
			}
			return suggestion;
		}
	}

	protected static synchronized Marshaller createMarshaller() throws JAXBException {
		Marshaller marshaller = getJaxbContext().createMarshaller();
		setPrefixMapper(marshaller);
		return marshaller;
	}

	protected static synchronized Unmarshaller createUnMarshaller() throws JAXBException {
		return getJaxbContext().createUnmarshaller();
	}

	protected static synchronized JAXBContext getJaxbContext() throws JAXBException {
		if (jaxbContext == null) {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class,
					org.apache.taverna.robundle.xml.odf.container.ObjectFactory.class,					
					org.apache.taverna.robundle.xml.dsig.ObjectFactory.class,
					org.apache.taverna.robundle.xml.xenc.ObjectFactory.class					
			);
		}
		return jaxbContext;
	}

	protected static void setPrefixMapper(Marshaller marshaller) {
		boolean setPrefixMapper = false;

		try {
			/*
			 * This only works with JAXB RI, in which case we can set the
			 * namespace prefix mapper
			 */
			Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new ManifestNamespacePrefixMapperJAXB_RI());
			/*
			 * Note: A similar mapper for the built-in java
			 * (com.sun.xml.bind.internal.namespacePrefixMapper) is no longer
			 * included here, as it will not (easily) compile with Maven.
			 */
			setPrefixMapper = true;
		} catch (Exception e) {
			logger.log(FINE, "Can't find NamespacePrefixMapper", e);
		}

		if (!setPrefixMapper && !warnedPrefixMapper) {
			logger.info(
					"Could not set prefix mapper (missing or incompatible JAXB) " + "- will use prefixes ns0, ns1, ..");
			warnedPrefixMapper = true;
		}
	}

}
