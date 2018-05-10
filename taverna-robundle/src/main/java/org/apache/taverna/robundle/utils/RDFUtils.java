package org.apache.taverna.robundle.utils;

import static java.nio.file.attribute.FileTime.fromMillis;

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


import static org.apache.jena.datatypes.xsd.XSDDatatype.XSDdateTime;

import java.nio.file.attribute.FileTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class RDFUtils {
	private static final Logger logger = Logger.getLogger(RDFUtils.class
			.getCanonicalName());

	public static FileTime literalAsFileTime(RDFNode rdfNode) {
		if (rdfNode == null) {
			return null;
		}
		Literal literal = null;
		if (rdfNode.isLiteral()) {
			/* Example:
			   <dcterms:created
			     rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2014-06-26T10:29:00Z</dcterms:created>
			 */
			literal = rdfNode.asLiteral();
		} else {
			// TAVERNA-1044: not a literal, so assume a resource
			// with the literal nested somehow
			Resource resource = rdfNode.asResource();

			// Potential type of bnode
			Resource dctW3CDTF = rdfNode.getModel().getResource("http://purl.org/dc/terms/W3CDTF");
			// TAVERNA-1044 - COMBIE sometimes mis-use DCT:W3CDTF as if it was a property
			Property dctW3CDTFProp = rdfNode.getModel().getProperty("http://purl.org/dc/terms/W3CDTF");

			if (resource.hasProperty(RDF.type, dctW3CDTF)) {
				// Semantically correct pattern, pick up rdf:value directly.
				/* Example:
			     <dcterms:created>
			      <dcterms:W3CDTF>
			        <rdf:value>2018-05-10T02:38:51Z</rdf:value>
			      </dcterms:W3CDTF>
			    </dcterms:created>
				 */
				Statement valueStmt = rdfNode.asResource().getProperty(RDF.value);
				if (valueStmt != null && valueStmt.getObject().isLiteral()) {
					literal = valueStmt.getLiteral();
				}
			} else if (resource.hasProperty(dctW3CDTFProp)) {
				// TAVERNA-1044: Weird, dct:W3CDTF is a type, not a property,
				// but we'll pretend it is to be compatible with example in
				// http://identifiers.org/combine.specifications/omex.version-1

				/* Example:
				<dcterms:created rdf:parseType="Resource">
				  <dcterms:W3CDTF>2014-06-26T10:29:00Z</dcterms:W3CDTF>
				</dcterms:created>
				*/
				Statement w3cDtfStmt = resource.getProperty(dctW3CDTFProp);
				if (w3cDtfStmt != null && w3cDtfStmt.getObject().isLiteral()) {
					literal = w3cDtfStmt.getLiteral();
				}
			}
		}
		if (literal == null) {
			Exception ex = new Exception("Invalid timestamp literal");
			logger.log(Level.WARNING,
			           "Expected literal value or dcterms:W3CDTF instance, not: " + rdfNode,
			           ex);
			return null;
		}

		Object value = literal.getValue();
		XSDDateTime dateTime;
		if (value instanceof XSDDateTime) {
			dateTime = (XSDDateTime) value;
		} else {
			logger.info("Literal not an XSDDateTime, but: " + value.getClass() + " " + value);
			// Try to parse it anyway
			try {
				dateTime = (XSDDateTime) XSDdateTime.parse(literal.getLexicalForm());
			} catch (DatatypeFormatException e) {
				logger.warning("Invalid datetime: " + literal);
				return null;
			}
		}
		return fromMillis(dateTime.asCalendar().getTimeInMillis());
	}
}
