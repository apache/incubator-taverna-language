package org.apache.taverna.robundle.utils;

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


import static com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDdateTime;
import static java.nio.file.attribute.FileTime.fromMillis;

import java.nio.file.attribute.FileTime;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFUtils {
	private static final Logger logger = Logger.getLogger(RDFUtils.class
			.getCanonicalName());

	public static FileTime literalAsFileTime(RDFNode rdfNode) {
		if (rdfNode == null)
			return null;
		if (!rdfNode.isLiteral()) {
			logger.warning("Expected literal. not " + rdfNode);
			return null;
		}
		Literal literal = rdfNode.asLiteral();
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
