package org.apache.taverna.robundle.manifest;

import static org.junit.Assert.assertEquals;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	public void manifestFromSpec() throws Exception {
		try (Bundle ro = Bundles.createBundle();
			 InputStream json = getClass().getResourceAsStream("/manifest.json")) {
			// Make a new, empty manifest we can inspect
			assertNotNull("Can't find manifest.json", json);
			Manifest manifest = new Manifest(ro);

			URI baseURI = ro.getPath(".ro/manifest.json").toUri();
			new RDFToManifest().readTo(json, manifest, baseURI);

			assertEquals(Arrays.asList(ro.getPath("/.ro/manifest.json")),
						manifest.getManifest());

			assertEquals(URI.create("http://example.com/retrieved"),
					manifest.getRetrievedFrom());
			assertEquals(Instant.parse("2013-03-05T17:29:03Z"),
					manifest.getRetrievedOn().toInstant());

			Agent retrievedBy = manifest.getRetrievedBy();
			assertEquals("John P. Smith",
					retrievedBy.getName());
			assertEquals(URI.create("http://example.com/foaf#john"),
					retrievedBy.getUri());
			assertEquals(URI.create("http://orcid.org/0000-0002-1825-0100"),
					retrievedBy.getOrcid());

			assertEquals(Instant.parse("2013-03-05T17:29:03Z"),
					manifest.getCreatedOn().toInstant());
			Agent createdBy = manifest.getCreatedBy();
			assertEquals("Alice W. Land",
					createdBy.getName());
			assertEquals(URI.create("http://example.com/foaf#alice"),
					createdBy.getUri());
			assertEquals(URI.create("http://orcid.org/0000-0002-1825-0097"),
					createdBy.getOrcid());

			assertEquals(Arrays.asList(ro.getPath(".ro/evolution.ttl")),
						manifest.getHistory());

			// As manifest.getAggregation() will create non-existing entries
			// we will check the list of URIs first
			Set<URI> aggregatedUris = manifest.getAggregates().stream()
				.map(PathMetadata::getUri)
				.collect(Collectors.toSet());
			//System.out.println(aggregatedUris);
			// Compare as Set as order is not necessarily preserved
			assertTrue(aggregatedUris.contains(URI.create("/folder/soup.jpeg")));
			assertTrue(aggregatedUris.contains(URI.create("http://example.com/blog/")));
			assertTrue(aggregatedUris.contains(URI.create("/README.txt")));
			assertTrue(aggregatedUris.contains(URI.create("http://example.com/comments.txt")));
			assertEquals(4, manifest.getAggregates().size());


			PathMetadata soup = manifest.getAggregation(ro.getPath("folder/soup.jpeg"));
			assertEquals(URI.create("http://example.com/retrieved/soup.jpeg"),
					soup.getRetrievedFrom());
			assertEquals(Instant.parse("2013-03-05T17:29:04Z"),
					soup.getRetrievedOn().toInstant());
			Agent peter = soup.getRetrievedBy();
			assertEquals("Peter L. Jones",
					peter.getName());
			assertEquals(URI.create("http://example.com/foaf#peter"),
					peter.getUri());
			assertEquals(URI.create("http://orcid.org/0000-0002-1825-0120"),
					peter.getOrcid());

			PathMetadata blog = manifest.getAggregation(URI.create("http://example.com/blog/"));
			// No additional metadata
			assertNull(blog.getRetrievedFrom());
			assertNull(blog.getRetrievedBy());
			assertNull(blog.getRetrievedOn());
			assertNull(blog.getCreatedBy());
			assertNull(blog.getCreatedOn());
			// NOTE: bundledAs might be created later when saving the manifest
			assertNull(blog.getBundledAs());

			PathMetadata readme = manifest.getAggregation(ro.getPath("/README.txt"));
			assertEquals("text/plain; charset=\"utf-8\"", readme.getMediatype());
			assertEquals("Bob Builder", readme.getCreatedBy().getName());
			assertEquals(URI.create("http://example.com/foaf#bob"), readme.getCreatedBy().getUri());

			PathMetadata comments = manifest.getAggregation(URI.create("http://example.com/comments.txt"));
			Proxy bundledAs = comments.getBundledAs();
			assertEquals(URI.create("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644"),
					bundledAs.getURI());
			// TAVERNA-1043 - should work looking up "folder" even though JSON says "/folder/"
			assertEquals(ro.getPath("folder"), bundledAs.getFolder());
			assertEquals("external.txt", bundledAs.getFilename());


			// Again, these could be in any order
			assertEquals(3, manifest.getAnnotations().size());
			// annotation 1
			PathAnnotation ann1 = manifest.getAnnotation(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf")).get();
			assertEquals(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf"), ann1.getUri());
			assertEquals(URI.create("/folder/soup.jpeg"), ann1.getAbout());
			assertEquals(URI.create("annotations/soup-properties.ttl"), ann1.getContent());

			// annotation 2
			PathAnnotation ann2 = manifest.getAnnotations(URI.create("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644")).get(0);
			assertEquals(URI.create("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644"), ann2.getAbout());
			assertEquals(URI.create("http://example.com/blog/they-aggregated-our-file"),
					ann2.getContent());

			// annotation 3
			PathAnnotation ann3 = manifest.getAnnotations(ro.getRoot()).get(0);
			// Can be retrieved either by Path, relative URI or absolute URI
			assertEquals(ann3,
					manifest.getAnnotations(URI.create("/")).get(0));
			assertEquals(ann3,
					manifest.getAnnotations(ro.getRoot().toUri()).get(0));
			// Or by the second 'about' URI
			assertEquals(ann3,
					manifest.getAnnotations(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf")).get(0));
			// single content
			assertEquals(URI.create("annotations/a-meta-annotation-in-this-ro.txt"),
					ann3.getContent());
			// multiple about
			assertEquals(2, ann3.getAboutList().size());
			// ... but order not necessarily preserved
			assertTrue(ann3.getAboutList().contains(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf")));
			assertTrue(ann3.getAboutList().contains(URI.create("/")));
		}
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
			assertEquals(URI.create("https://orcid.org/0000-0001-9842-9718"),
					author.getOrcid());

			// FIXME: The BDBag got this wrong and used "uri" instead of "content", and so
			// data/README accidentally became the ID of the annotation.
			// That means that we can't find any annotations as we only pick up
			// on those that have a "content"
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
			assertNotNull(bundledAs);
			// FIXME: Why is there no bundledAs data?
			assertEquals(ro.getPath("data"),
					bundledAs.getFolder());
			assertEquals("bag1.zip", a1.getBundledAs().getFilename());

			// TODO: Enable fetching based on bundledAs URI?
			//ro.getRoot().resolve("data/bag1.zip")

		}
	}
}
