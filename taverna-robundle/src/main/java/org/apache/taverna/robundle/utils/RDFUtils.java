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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class RDFUtils {
	private static final Logger logger = Logger.getLogger(RDFUtils.class
			.getCanonicalName());

	public static FileTime literalAsFileTime(RDFNode rdfNode) {
		if (rdfNode == null) {
			return null;
		}
		final Literal literal;
		if (rdfNode.isLiteral()) {
			literal = rdfNode.asLiteral();
		} else { 
			// TAVERNA-1044: not a literal, so assume a resource.
			// Let's climb into rdf:value if it exists, in case we're in a
			// <dct:W3CDTF> typed bnode.
			Statement valueStmt = rdfNode.asResource().getProperty(RDF.value);
			if (valueStmt == null) {
				// Make our own exception so logger gets a stacktrace
				Exception ex = new Exception("Can't find timestamp as literal");
				logger.log(Level.WARNING, 
				           "Expected literal or resource with rdf:value. not " + rdfNode, 
				           ex);
				return null;
			}
			if (valueStmt.getObject().isLiteral()) {
				literal = valueStmt.getObject().asLiteral();
			} else {	
				Exception ex = new Exception("Invalid timestamp literal");
				logger.log(Level.WARNING, 
				           "Expected rdf:value statement with literal object, not" + valueStmt,
				           ex);
				return null;				
			}
		}

		Object value = literal.getValue();
		XSDDateTime dateTime;
		if (value instanceof XSDDateTime) {
			dateTime = (XSDDateTime) value;
		} else {
			logger.info("Literal not an XSDDateTime, but: " + value.getClass()
					+ " " + value);

			// Try to parse it anyway
			try {
				dateTime = (XSDDateTime) XSDdateTime.parse(literal
						.getLexicalForm());
			} catch (DatatypeFormatException e) {
				logger.warning("Invalid datetime: " + literal);
				return null;
			}
		}
		return fromMillis(dateTime.asCalendar().getTimeInMillis());
	}
}
