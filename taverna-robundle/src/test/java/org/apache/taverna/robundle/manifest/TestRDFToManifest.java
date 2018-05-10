package org.apache.taverna.robundle.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
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
	
	@Test
	public void multipleConformsTo() throws Exception {
		try (Bundle ro = Bundles.createBundle();
			 InputStream json = getClass().getResourceAsStream("/bag-of-bags-manifest.json")) {
			// Make a new, empty manifest we can inspect
			assertNotNull("Can't find bag-of-bags-manifest.json", json);
			Manifest manifest = new Manifest(ro);
			
			// NOTE: Uses RO-BagIt folder metadata/ instead of .ro/
			URI baseURI = ro.getPath("metadata/manifest.json").toUri();			
			new RDFToManifest().readTo(json, manifest, baseURI);

			// Check basic metadata
			assertEquals(URI.create("https://github.com/ini-bdds/bdbag"),
					manifest.getCreatedBy().getUri());
			assertEquals(1, manifest.getAuthoredBy().size());
			Agent author = manifest.getAuthoredBy().get(0);
			assertEquals("Stian Soiland-Reyes", 
					author.getName());
			assertEquals(URI.create("mbox:stain@apache.org"), 
					author.getUri());
			// FIXME: Why is ORCID not picked up?
//			assertEquals(URI.create("https://orcid.org/0000-0001-9842-9718"),
//					author.getOrcid());

			// FIXME: Why are annotations not picked up?
//			assertEquals(1, manifest.getAnnotations().size());
//			PathAnnotation ann = manifest.getAnnotations().get(0);
//			assertEquals(ro.getRoot().toUri(),
//					ann.getAbout());
//			assertEquals(ro.getPath("data/README").toUri(), ann.getContent());
			
			// Now let's check aggregations
			assertEquals(1, manifest.getAggregates().size());
			// As aggregates order is not preserved, for simplicity this test
			// has only one aggregation			
			PathMetadata a1 = manifest.getAggregates().get(0);
			URI uri = URI.create("http://n2t.net/ark:/57799/b90h3c");
			assertEquals(uri, a1.getUri());
			assertEquals(a1, manifest.getAggregation(uri));
			
			// FIXME: More than one?
			assertEquals(URI.create("https://w3id.org/ro/bagit/profile"),
					a1.getConformsTo());
//			assertEquals(URI.create("https://tools.ietf.org/html/draft-kunze-bagit-14"),
//					a1.getConformsTo());

			
			Proxy bundledAs = a1.getBundledAs();
			// FIXME: Why is there no bundledAs?
//			assertNotNull(bundledAs);
//			assertEquals(ro.getPath("data"), 
//					bundledAs.getFolder());			
//			assertEquals("bag1.zip", a1.getBundledAs().getFilename());

			// TODO: Enable fetching based on bundledAs URI?
			//ro.getRoot().resolve("data/bag1.zip")
			
		}				
	}
}
