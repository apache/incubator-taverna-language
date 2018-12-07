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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestManifestJSON {
	
	@Test
	public void testHistory () throws IOException
	{
		Path tmpBundle = Files.createTempFile ("testbundle", "history");

		// create history
		try (Bundle bundle = Bundles.createBundle())
		{
			Bundles.closeAndSaveBundle (bundle, tmpBundle);
		}
		catch (IOException e)
		{
			fail ("failed to create bundle for history test: " + e.getMessage ());
		}
		
		// make sure it doesn't fail if there is no history
		try (Bundle bundle = Bundles.openBundle (tmpBundle))
		{
			Manifest manifest = bundle.getManifest();
			Path evolutionPath = bundle.getPath(".ro/evolution.ttl");
			assertFalse ("did not expect a history file", Files.exists (evolutionPath));
			assertEquals ("did not expect a history", 0, manifest.getHistory ().size ());
			
			Files.createDirectories(evolutionPath.getParent());
			Bundles.setStringValue(
					evolutionPath,
					"<manifest.json> < http://purl.org/pav/retrievedFrom> "
							+ "<http://wf4ever.github.io/ro/bundle/2013-05-21/example/.ro/manifest.json> .");
			manifest.getHistory().add(evolutionPath);
			assertTrue ("expected a history file", Files.exists (evolutionPath));
			assertTrue ("expected a history", manifest.getHistory ().size () > 0);
			
			Bundles.closeBundle (bundle);
		}
		catch (IOException e)
		{
			fail ("failed to read bundle for history test: " + e.getMessage ());
		}
		
		// check if history is still there
		try (Bundle bundle = Bundles.openBundleReadOnly (tmpBundle))
		{
			Manifest manifest = bundle.getManifest();
			Path evolutionPath = bundle.getPath(".ro/evolution.ttl");
			assertTrue ("expected a history file", Files.exists (evolutionPath));
			assertEquals ("expected exactly one history", 1, manifest.getHistory ().size ());
			Bundles.closeBundle (bundle);
		}
		catch (IOException e)
		{
			fail ("failed to read bundle for history test: " + e.getMessage ());
		}
		
		Files.delete (tmpBundle);
	}
	
	@Test
	public void createBundle() throws Exception {
		// Create bundle as in Example 3 of the specification
		// http://wf4ever.github.io/ro/bundle/2013-05-21/
		try (Bundle bundle = Bundles.createBundle()) {
			Calendar createdOnCal = Calendar.getInstance(
					TimeZone.getTimeZone("Z"), Locale.ENGLISH);
			// "2013-03-05T17:29:03Z"
			// Remember months are 0-based in java.util.Calendar!
			createdOnCal.set(2013, 3 - 1, 5, 17, 29, 03);
			createdOnCal.set(Calendar.MILLISECOND, 0);
			FileTime createdOn = FileTime.fromMillis(createdOnCal
					.getTimeInMillis());
			Manifest manifest = bundle.getManifest();
			manifest.setCreatedOn(createdOn);
			Agent createdBy = new Agent("Alice W. Land");
			createdBy.setUri(URI.create("http://example.com/foaf#alice"));
			createdBy.setOrcid(URI
					.create("http://orcid.org/0000-0002-1825-0097"));

			manifest.setCreatedBy(createdBy);

			Path evolutionPath = bundle.getPath(".ro/evolution.ttl");
			Files.createDirectories(evolutionPath.getParent());
			Bundles.setStringValue(
					evolutionPath,
					"<manifest.json> < http://purl.org/pav/retrievedFrom> "
							+ "<http://wf4ever.github.io/ro/bundle/2013-05-21/example/.ro/manifest.json> .");
			manifest.getHistory().add(evolutionPath);

			Path jpeg = bundle.getPath("folder/soup.jpeg");
			Files.createDirectory(jpeg.getParent());
			Files.createFile(jpeg);
			// register in manifest first
			bundle.getManifest().getAggregation(jpeg);

			URI blog = URI.create("http://example.com/blog/");
			bundle.getManifest().getAggregation(blog);

			Path readme = bundle.getPath("README.txt");
			Files.createFile(readme);
			PathMetadata readmeMeta = bundle.getManifest().getAggregation(
					readme);
			readmeMeta.setMediatype("text/plain; charset=\"utf-8\"");
			Agent readmeCreatedby = new Agent("Bob Builder");
			readmeCreatedby.setUri(URI.create("http://example.com/foaf#bob"));
			readmeMeta.setCreatedBy(readmeCreatedby);

			// 2013-02-12T19:37:32.939Z
			createdOnCal.set(2013, 2 - 1, 12, 19, 37, 32);
			createdOnCal.set(Calendar.MILLISECOND, 939);
			createdOn = FileTime.fromMillis(createdOnCal.getTimeInMillis());
			Files.setLastModifiedTime(readme, createdOn);
			readmeMeta.setCreatedOn(createdOn);

			PathMetadata comments = bundle.getManifest().getAggregation(
					URI.create("http://example.com/comments.txt"));
			comments.getOrCreateBundledAs()
					.setURI(URI
							.create("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644"));
			comments.getOrCreateBundledAs().setFolder(
					bundle.getPath("/folder/"));
			comments.getOrCreateBundledAs().setFilename("external.txt");

			PathAnnotation jpegAnn = new PathAnnotation();
			jpegAnn.setAbout(jpeg);
			Path soupProps = Bundles.getAnnotations(bundle).resolve(
					"soup-properties.ttl");
			Bundles.setStringValue(soupProps,
					"</folder/soup.jpeg> <http://xmlns.com/foaf/0.1/depicts> "
							+ "<http://example.com/menu/tomato-soup> .");
			jpegAnn.setContent(soupProps);
			// jpegAnn.setContent(URI.create("annotations/soup-properties.ttl"));
			jpegAnn.setUri(URI
					.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf"));
			manifest.getAnnotations().add(jpegAnn);

			PathAnnotation proxyAnn = new PathAnnotation();
			proxyAnn.setAbout(comments.getBundledAs().getURI());
			proxyAnn.setContent(URI
					.create("http://example.com/blog/they-aggregated-our-file"));
			manifest.getAnnotations().add(proxyAnn);

			Path metaAnn = Bundles.getAnnotations(bundle).resolve(
					"a-meta-annotation-in-this-ro.txt");
			Bundles.setStringValue(metaAnn,
					"This bundle contains an annotation about /folder/soup.jpeg");

			PathAnnotation metaAnnotation = new PathAnnotation();
			metaAnnotation.setAbout(bundle.getRoot());
			metaAnnotation
					.getAboutList()
					.add(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf"));

			metaAnnotation.setContent(metaAnn);
			manifest.getAnnotations().add(metaAnnotation);

			Path jsonPath = bundle.getManifest().writeAsJsonLD();
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonStr = Bundles.getStringValue(jsonPath);
			//System.out.println(jsonStr);
			JsonNode json = objectMapper.readTree(jsonStr);
			checkManifestJson(json);
		}
	}

	public void checkManifestJson(JsonNode json) {
		JsonNode context = json.get("@context");
		assertNotNull("Could not find @context", context);
		assertTrue("@context SHOULD be an array", context.isArray());
		assertTrue("@context SHOULD include a context", context.size() > 0);
		JsonNode lastContext = context.get(context.size() - 1);
		assertEquals(
				"@context SHOULD include https://w3id.org/bundle/context as last item",
				"https://w3id.org/bundle/context", lastContext.asText());

		assertEquals("/", json.get("id").asText());

		JsonNode manifest = json.get("manifest");
		if (manifest.isValueNode()) {
			assertEquals(
					"manifest SHOULD be literal value \"manifest.json\" or list",
					"manifest.json", manifest.asText());
		} else {
			assertTrue("manifest is neither literal or list",
					manifest.isArray());
			boolean found = false;
			for (JsonNode n : manifest) {
				found = n.asText().equals("manifest.json");
				if (found) {
					break;
				}
			}
			;
			assertTrue("Could not find 'manifest.json' in 'manifest' list: "
					+ manifest, found);
		}

		assertEquals("2013-03-05T17:29:03Z", json.get("createdOn").asText());
		JsonNode createdBy = json.get("createdBy");
		assertNotNull("Could not find createdBy", createdBy);
		assertEquals("http://example.com/foaf#alice", createdBy.get("uri")
				.asText());
		assertEquals("http://orcid.org/0000-0002-1825-0097",
				createdBy.get("orcid").asText());
		assertEquals("Alice W. Land", createdBy.get("name").asText());

		JsonNode history = json.get("history");
		if (history.isValueNode()) {
			assertEquals("evolution.ttl", history.asText());
		} else {
			assertEquals("evolution.ttl", history.get(0).asText());
		}

		JsonNode aggregates = json.get("aggregates");
		assertTrue("aggregates not a list", aggregates.isArray());
		JsonNode soup = aggregates.get(0);
		if (soup.isValueNode()) {
			assertEquals("/folder/soup.jpeg", soup.asText());
		} else {
			assertEquals("/folder/soup.jpeg", soup.get("uri").asText());
		}

		JsonNode blog = aggregates.get(1);
		if (blog.isValueNode()) {
			assertEquals("http://example.com/blog/", blog.asText());
		} else {
			assertEquals("http://example.com/blog/", blog.get("uri").asText());
		}

		JsonNode readme = aggregates.get(2);
		assertEquals("/README.txt", readme.get("uri").asText());
		assertEquals("text/plain; charset=\"utf-8\"", readme.get("mediatype").asText());
		assertEquals("2013-02-12T19:37:32.939Z", readme.get("createdOn")
				.asText());
		JsonNode readmeCreatedBy = readme.get("createdBy");
		assertEquals("http://example.com/foaf#bob", readmeCreatedBy.get("uri")
				.asText());
		assertEquals("Bob Builder", readmeCreatedBy.get("name").asText());

		JsonNode comments = aggregates.get(3);
		assertEquals("http://example.com/comments.txt", comments.get("uri")
				.asText());
		JsonNode bundledAs = comments.get("bundledAs");
		assertEquals("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644", bundledAs
				.get("uri").asText());
		assertEquals("/folder/", bundledAs.get("folder").asText());
		assertEquals("external.txt", bundledAs.get("filename").asText());

		JsonNode annotations = json.get("annotations");
		assertTrue("annotations MUST be a list", annotations.isArray());

		JsonNode ann0 = annotations.get(0);
		assertEquals("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf",
				ann0.get("uri").asText());
		assertEquals("/folder/soup.jpeg", ann0.get("about").asText());
		assertEquals("annotations/soup-properties.ttl", ann0.get("content")
				.asText());

		JsonNode ann1 = annotations.get(1);
		assertNull(ann1.get("annotation"));
		assertEquals("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644",
				ann1.get("about").asText());
		assertEquals("http://example.com/blog/they-aggregated-our-file", ann1
				.get("content").asText());

		JsonNode ann2 = annotations.get(2);
		assertNull(ann2.get("annotation"));
		JsonNode about = ann2.get("about");
		assertTrue("about was not a list", about.isArray());
		assertEquals("/", about.get(0).asText());
		assertEquals("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf", about
				.get(1).asText());
		assertEquals("annotations/a-meta-annotation-in-this-ro.txt",
				ann2.get("content").asText());

	}

	@Test
	public void checkJsonFromSpec() throws Exception {
		// Verify that our test confirms the existing spec example
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode json = objectMapper.readTree(getClass().getResource(
				"/manifest.json"));
		checkManifestJson(json);

	}
}
