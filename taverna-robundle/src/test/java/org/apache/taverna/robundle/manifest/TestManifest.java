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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.PathMetadata;
import org.apache.taverna.robundle.manifest.RDFToManifest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

@SuppressWarnings({ "deprecation", "unused" })
public class TestManifest {
	private Bundle bundle;

	@Test
	public void populateFromBundle() throws Exception {
		Path r = bundle.getRoot();
		URI base = r.toUri();

		Manifest manifest = new Manifest(bundle);
		manifest.populateFromBundle();

		List<String> uris = new ArrayList<>();
		for (PathMetadata s : manifest.getAggregates()) {
			uris.add(s.getFile().toString());
			Path path = s.getFile();
			assertNotNull(path.getParent());
			assertEquals(path.getParent(), s.getBundledAs().getFolder());
			if (s.getFile().equals(URI.create("/f/nested/empty/"))) {
				continue;
				// Folder's don't need proxy and createdOn
			}
			assertEquals("urn", s.getProxy().getScheme());
			UUID.fromString(s.getProxy().getSchemeSpecificPart()
					.replace("uuid:", ""));
			assertEquals(s.getCreatedOn(), Files.getLastModifiedTime(path));
		}
		//System.out.println(uris);
		assertFalse(uris.contains("/mimetype"));
		assertFalse(uris.contains("/META-INF"));
		assertTrue(uris.remove("/hello.txt"));
		assertTrue(uris.remove("/f/file1.txt"));
		assertTrue(uris.remove("/f/file2.txt"));
		assertTrue(uris.remove("/f/file3.txt"));
		assertTrue(uris.remove("/f/nested/file1.txt"));
		assertTrue(uris.remove("/f/nested/empty"));
		assertTrue(uris.isEmpty());
	}

	@Test
	public void repopulateFromBundle() throws Exception {
		Path r = bundle.getRoot();
		URI base = r.toUri();

		Manifest manifest = new Manifest(bundle);
		manifest.populateFromBundle();
		// Second populate should not add additional entries
		manifest.populateFromBundle();

		List<String> paths = new ArrayList<>();
		for (PathMetadata s : manifest.getAggregates()) {
			Path path = s.getFile();
			paths.add(s.toString());
			assertNotNull(path.getParent());
			assertEquals(path.getParent(), s.getBundledAs().getFolder());
			if (s.getFile().equals(URI.create("/f/nested/empty/"))) {
				continue;
				// Folder's don't need proxy and createdOn
			}
			assertEquals("urn", s.getProxy().getScheme());
			UUID.fromString(s.getProxy().getSchemeSpecificPart()
					.replace("uuid:", ""));
			assertEquals(s.getCreatedOn(), Files.getLastModifiedTime(path));
		}
		//System.out.println(uris);
		assertFalse(paths.contains("/mimetype"));
		assertFalse(paths.contains("/META-INF"));
		assertTrue(paths.remove("/hello.txt"));
		assertTrue(paths.remove("/f/file1.txt"));
		assertTrue(paths.remove("/f/file2.txt"));
		assertTrue(paths.remove("/f/file3.txt"));
		assertTrue(paths.remove("/f/nested/file1.txt"));
		assertTrue(paths.remove("/f/nested/empty/"));
		assertTrue("Unexpected path: " + paths, paths.isEmpty());
	}

	private Path uri2path(URI base, URI uri) {
		URI fileUri = base.resolve(uri);
		return Paths.get(fileUri);
	}

	@Test
	public void writeAsJsonLD() throws Exception {
		Manifest manifest = new Manifest(bundle);
		manifest.populateFromBundle();
		PathMetadata helloMeta = null;
		for (PathMetadata meta : manifest.getAggregates()) {
			if (meta.getFile().endsWith("hello.txt")) {
				helloMeta = meta;
			}
		}
		assertNotNull("No metadata for </hello.txt>", helloMeta);

		Path jsonld = manifest.writeAsJsonLD();
		assertEquals(bundle.getFileSystem().getPath("/.ro", "manifest.json"),
				jsonld);
		assertTrue(Files.exists(jsonld));
		String manifestStr = new String(Files.readAllBytes(jsonld), "UTF8");
		//System.out.println(manifestStr);

		// Rough and ready that somethings are there
		// TODO: Read back and check as JSON structure
		// TODO: Check as JSON-LD graph
		assertTrue(manifestStr.contains("@context"));
		assertTrue(manifestStr.contains("https://w3id.org/bundle/context"));
		assertTrue(manifestStr.contains("/f/file2.txt"));
		assertTrue(manifestStr.contains("/hello.txt"));
		assertTrue(manifestStr.contains(helloMeta.getProxy().toASCIIString()));

		// Parse back as JSON-LD
		try (InputStream jsonIn = Files.newInputStream(jsonld)) {
			URI baseURI = jsonld.toUri();
			Model model = RDFToManifest.jsonLdAsJenaModel(jsonIn, baseURI);
			model.write(new ByteArrayOutputStream(), "TURTLE", baseURI.toString());
			model.write(new ByteArrayOutputStream(), "RDF/XML", baseURI.toString());

			String queryStr = "PREFIX ore: <http://www.openarchives.org/ore/terms/>"
					+ "PREFIX bundle: <http://purl.org/wf4ever/bundle#>"
					+ "SELECT ?file ?proxy "
					+ "WHERE {"
					+ "    ?ro ore:aggregates ?file ."
					+ "    OPTIONAL { ?file bundle:bundledAs ?proxy . } " + "}";
			Query query = QueryFactory.create(queryStr);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);

			try {
				ResultSet results = qexec.execSelect();
				int aggregationCount = 0;
				for (; results.hasNext(); aggregationCount++) {
					QuerySolution soln = results.nextSolution();
					Resource fileRes = soln.getResource("file");
					Resource proxy = soln.getResource("proxy");
					//System.out.println("File: " + fileRes);
					//System.out.println(asURI(fileRes));

					Path file = Paths.get(asURI(fileRes));
					assertTrue(Files.exists(file));
					PathMetadata meta = manifest.getAggregation(file);
					assertEquals(meta.getProxy(), asURI(proxy));
				}
				assertEquals("Could not find all aggregations from manifest: "
						+ manifest.getAggregates(), manifest.getAggregates()
						.size(), aggregationCount);
			} finally {
				// WHY is not QueryExecution an instance of Closable?
				qexec.close();
			}
		}
	}

	@Test
	public void readManifest() throws Exception {
		Manifest manifest = new Manifest(bundle);

		new RDFToManifest().readTo(
				getClass().getResourceAsStream("/manifest.json"), manifest,
				manifest.getBaseURI().resolve("does/not/exist"));

		Path r = bundle.getRoot();

		assertEquals("http://example.com/retrieved", manifest.getRetrievedFrom().toString());
		assertEquals("2013-03-05T17:29:03Z", manifest.getRetrievedOn().toString());
		assertEquals("http://example.com/foaf#john", manifest.getRetrievedBy().getUri().toString());

		PathMetadata soupAggregation = manifest.getAggregation(r.resolve("/folder/soup.jpeg"));
		assertEquals("http://example.com/retrieved/soup.jpeg", soupAggregation.getRetrievedFrom().toString());
		assertEquals("2013-03-05T17:29:04Z", soupAggregation.getRetrievedOn().toString());
		assertEquals("http://example.com/foaf#peter", soupAggregation.getRetrievedBy().getUri().toString());

		assertNotNull(manifest.getAggregation(r.resolve("/README.txt")));
		PathMetadata readme = manifest.getAggregation(r.resolve("/README.txt"));
		assertEquals("http://example.com/foaf#bob", readme.getCreatedBy()
				.getUri().toString());
		assertEquals("Bob Builder",
				manifest.getAggregation(r.resolve("/README.txt"))
						.getCreatedBy().getName());
		assertEquals("text/plain; charset=\"utf-8\"",
				manifest.getAggregation(r.resolve("/README.txt"))
						.getMediatype());

		assertNull(manifest.getAggregation(r.resolve("/README.txt"))
				.getBundledAs());

		// Disabled: RO Bundle in flux on how to put external URIs in folders
		// assertNotNull(manifest.getAggregation(
		// URI.create("http://example.com/comments.txt")).getProxy());

		//System.out.println(manifest.getAnnotations());

		assertEquals(3, manifest.getAnnotations().size());

	}

	private URI asURI(Resource proxy) {
		if (proxy == null) {
			return null;
		}
		String uri = proxy.getURI();
		if (uri == null) {
			return null;
		}
		return bundle.getRoot().toUri().resolve(uri);
	}

	@Before
	public void exampleBundle() throws IOException {
		Path source;
		try (Bundle bundle = Bundles.createBundle()) {
			source = bundle.getSource();
			Path r = bundle.getRoot();
			Files.createFile(r.resolve("hello.txt"));
			Path f = r.resolve("f");
			Files.createDirectory(f);
			Files.createFile(f.resolve("file3.txt"));
			Files.createFile(f.resolve("file2.txt"));
			Files.createFile(f.resolve("file1.txt"));

			Path nested = f.resolve("nested");
			Files.createDirectory(nested);
			Files.createFile(nested.resolve("file1.txt"));

			Files.createDirectory(nested.resolve("empty"));
			bundle.setDeleteOnClose(false);
		}
		bundle = Bundles.openBundle(source);
	}

	@After
	public void closeBundle() throws IOException {
		bundle.close();

	}
}
