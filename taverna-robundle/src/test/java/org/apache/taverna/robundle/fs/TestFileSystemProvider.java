package org.apache.taverna.robundle.fs;

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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.robundle.fs.BundleFileSystem;
import org.apache.taverna.robundle.fs.BundleFileSystemProvider;
import org.junit.Assume;
import org.junit.Test;

public class TestFileSystemProvider {

	@Test
	public void getInstance() throws Exception {
		assertSame(BundleFileSystemProvider.getInstance(),
				BundleFileSystemProvider.getInstance());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getInstanceEquals() throws Exception {
		assertEquals(BundleFileSystemProvider.getInstance(),
				new BundleFileSystemProvider());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getInstanceHashCode() throws Exception {
		assertEquals(BundleFileSystemProvider.getInstance().hashCode(),
				new BundleFileSystemProvider().hashCode());
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	@Test
	public void sameOpen() throws Exception {
		assertSame(BundleFileSystemProvider.getInstance().openFilesystems,
				new BundleFileSystemProvider().openFilesystems);
	}

	@Test
	public void installedProviders() throws Exception {
		for (FileSystemProvider provider : FileSystemProvider
				.installedProviders()) {
			if (provider instanceof BundleFileSystemProvider) {
				assertSame(provider, BundleFileSystemProvider.getInstance());
				return;
			}
		}
		fail("Could not find BundleFileSystemProvider as installed provider");
	}

	@Test
	public void newByURI() throws Exception {

		Path path = Files.createTempFile("test", "zip");
		path.toFile().deleteOnExit();
		BundleFileSystemProvider.createBundleAsZip(path, null);

		// HACK: Use a opaque version of app: with the file URI as scheme
		// specific part
		URI w = new URI("app", path.toUri().toASCIIString(), null);
		try (FileSystem fs = FileSystems.newFileSystem(w,
				Collections.<String, Object> emptyMap())) {
			assertTrue(fs instanceof BundleFileSystem);
		}
	}

	@Test
	public void bundleWithSpaces() throws Exception {
		Path path = Files.createTempFile("with several spaces", ".zip");
		path.toFile().deleteOnExit();
		Files.delete(path);

		URI app = new URI("app", path.toUri().toString(), null);
		assertTrue(app.toASCIIString().contains("with%2520several%2520spaces"));

		Map<String, Object> env = new HashMap<>();
		env.put("create", "true");

		try (FileSystem fs = FileSystems.newFileSystem(app, env)) {
		}
		assertTrue(Files.exists(path));
		// Reopen from now-existing Path to check that the URI is
		// escaped in the same way
		try (FileSystem fs = BundleFileSystemProvider
				.newFileSystemFromExisting(path)) {
		}
	}

	@Test
	public void bundleWithSpacesSource() throws Exception {
		Path path = Files.createTempFile("with several spaces", ".zip");
		path.toFile().deleteOnExit();
		Files.delete(path);

		try (BundleFileSystem fs = BundleFileSystemProvider
				.newFileSystemFromNew(path)) {
			assertTrue(Files.exists(fs.getSource()));
			assertEquals(path.toAbsolutePath(), fs.getSource());
		}
		assertTrue(Files.exists(path));
	}

	@Test
	public void bundleWithUnicode() throws Exception {
		Path path;
		try {
			path = Files.createTempFile("with\u2301unicode\u263bhere", ".zip");
		} catch (InvalidPathException ex) {
			Assume.assumeNoException(
					"Can't test unicode filename, as -Dfile.encoding="
							+ System.getProperty("file.encoding"), ex);
			return;
		}
		path.toFile().deleteOnExit();
		Files.delete(path);
		// System.out.println(path); // Should contain a electrical symbol and
		// smiley
		URI app = new URI("app", path.toUri().toString(), null);
		// FIXME: The below passes on Windows 8 but not in Linux!?
		// System.out.println(app);
		// assertTrue(app.toString().contains("\u2301"));
		// assertTrue(app.toString().contains("\u263b"));

		Map<String, Object> env = new HashMap<>();
		env.put("create", "true");

		try (FileSystem fs = FileSystems.newFileSystem(app, env)) {
		}
		assertTrue(Files.exists(path));
		// Reopen from now-existing Path to check that the URI is
		// escaped in the same way
		try (FileSystem fs = BundleFileSystemProvider
				.newFileSystemFromExisting(path)) {
		}
	}

	@Test
	public void newFileSystemFromExisting() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		// Make the Bundle first
		BundleFileSystemProvider.createBundleAsZip(path, "application/x-test");
		assertTrue(Files.exists(path));

		try (BundleFileSystem f = BundleFileSystemProvider
				.newFileSystemFromExisting(path)) {
			assertEquals(path, f.getSource());
			assertEquals(
					"application/x-test",
					Files.readAllLines(
							f.getRootDirectory().resolve("mimetype"),
							Charset.forName("ASCII")).get(0));
		}
	}

	@Test
	public void newFileSystemFromExistingPath() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		// Make the Bundle first as we can't pass inn create=true :/
		BundleFileSystemProvider.createBundleAsZip(path, "application/x-test");
		assertTrue(Files.exists(path));

		try (FileSystem fs = FileSystems.newFileSystem(path, getClass()
				.getClassLoader())) {
			assertEquals(
					"application/x-test",
					Files.readAllLines(fs.getPath("mimetype"),
							Charset.forName("ASCII")).get(0));
		}
	}

	@Test
	public void newFileSystemFromNewDefaultMime() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		BundleFileSystem f = BundleFileSystemProvider
				.newFileSystemFromNew(path);
		assertTrue(Files.exists(path));
		assertEquals(path, f.getSource());
		assertEquals(
				"application/vnd.wf4ever.robundle+zip",
				Files.readAllLines(f.getRootDirectory().resolve("mimetype"),
						Charset.forName("ASCII")).get(0));
	}

	@Test
	public void newFileSystemURI() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);

		URI uri = new URI("app", path.toUri().toASCIIString(), (String) null);

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		// And the optional mimetype
		env.put("mimetype", "application/x-test2");
		FileSystem f = FileSystems.newFileSystem(uri, env, getClass()
				.getClassLoader());
		assertTrue(Files.exists(path));
		assertEquals(
				"application/x-test2",
				Files.readAllLines(f.getPath("mimetype"),
						Charset.forName("ASCII")).get(0));
	}

	@Test
	public void newFileSystemFromNew() throws Exception {
		Path path = Files.createTempFile("test", null);
		path.toFile().deleteOnExit();
		Files.delete(path);
		path.toUri();
		BundleFileSystem f = BundleFileSystemProvider.newFileSystemFromNew(
				path, "application/x-test2");
		assertTrue(Files.exists(path));
		assertEquals(path, f.getSource());
		assertEquals(
				"application/x-test2",
				Files.readAllLines(f.getRootDirectory().resolve("mimetype"),
						Charset.forName("ASCII")).get(0));
	}

	@Test
	public void newFileSystemFromTemporary() throws Exception {
		Path source;
		try (BundleFileSystem f = BundleFileSystemProvider
				.newFileSystemFromTemporary()) {
			source = f.getSource();
			assertTrue(Files.exists(source));
			assertEquals(
					"application/vnd.wf4ever.robundle+zip",
					Files.readAllLines(
							f.getRootDirectory().resolve("mimetype"),
							Charset.forName("ASCII")).get(0));
		}
		Files.delete(source);
	}

}
