package org.apache.taverna.robundle.manifest;

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


import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.github.jsonldjava.core.DocumentLoader;

//import com.github.jsonldjava.core.DocumentLoader;

public class TestRDFToManifest {
	private static final String CONTEXT = "https://w3id.org/bundle/context";

	@Test
	public void contextLoadedFromJarCache() throws Exception {
		// RDFToManifest.makeBaseURI(); // trigger static{} block
		@SuppressWarnings("unchecked")
		Map<String, Object> context = (Map<String, Object>) new DocumentLoader()
				.loadDocument(CONTEXT).getDocument();
		// FIXME: jsonld-java 0.3 and later uses DocumentLoader instead of
		// JSONUtils
		// Map<String, Object> context = (Map<String, Object>)
		// JSONUtils.fromURL(new URL(CONTEXT));
		Object retrievedFrom = context.get("http://purl.org/pav/retrievedFrom");
		assertNotNull("Did not load context from cache: " + CONTEXT,
				retrievedFrom);

	}
}
