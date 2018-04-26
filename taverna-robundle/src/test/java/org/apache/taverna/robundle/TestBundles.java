package org.apache.taverna.robundle;

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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.taverna.robundle.fs.BundleFileSystem;
import org.apache.taverna.robundle.fs.BundleFileSystemProvider;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.PathMetadata;
import org.apache.taverna.robundle.utils.TemporaryFiles;
import org.junit.Ignore;
import org.junit.Test;

public class TestBundles {

	private static final int MIME_OFFSET = 30;

	protected void checkSignature(Path zip) throws IOException {
		String MEDIATYPE = "application/vnd.wf4ever.robundle+zip";
		// Check position 30++ according to RO Bundle specification
		// http://purl.org/wf4ever/ro-bundle#ucf
		byte[] expected = ("mimetype" + MEDIATYPE + "PK").getBytes("ASCII");

		try (InputStream in = Files.newInputStream(zip)) {
			byte[] signature = new byte[expected.length];
			int MIME_OFFSET = 30;
			assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
			assertEquals(expected.length, in.read(signature));
			assertArrayEquals(expected, signature);
		}
	}

	@Test
	public void closeDeleteTemp() throws Exception {
		Bundle bundle = Bundles.createBundle();
		assertTrue(Files.exists(bundle.getSource()));
		assertTrue(bundle.getFileSystem().isOpen());
		assertTrue(bundle.isDeleteOnClose());
		bundle.close();
		assertFalse(Files.exists(bundle.getSource()));
		assertFalse(bundle.getFileSystem().isOpen());
	}

	@Test
	public void closeNotDelete() throws Exception {
		Path path = Files.createTempFile("bundle", ".zip");
		Bundle bundle = Bundles.createBundle(path);
		assertFalse(bundle.isDeleteOnClose());
		assertTrue(Files.exists(bundle.getSource()));
		assertTrue(bundle.getFileSystem().isOpen());

		bundle.close();
		assertTrue(Files.exists(bundle.getSource()));
		assertFalse(bundle.getFileSystem().isOpen());
	}

	@Test
	public void closeAndOpenBundle() throws Exception {
		Bundle bundle = Bundles.createBundle();
		Path zip = Bundles.closeBundle(bundle);
		Bundles.openBundle(zip).close();
	}

	@Test
	public void closeAndOpenBundleWithStringValue() throws Exception {
		Bundle bundle = Bundles.createBundle();
		Path hello = bundle.getRoot().resolve("hello.txt");
		Bundles.setStringValue(hello, "Hello");
		Path zip = Bundles.closeBundle(bundle);

		try (Bundle newBundle = Bundles.openBundle(zip)) {
			Path newHello = newBundle.getRoot().resolve("hello.txt");
			assertEquals("Hello", Bundles.getStringValue(newHello));
		}
	}

	@Test
	public void closeAndSaveBundleDelete() throws Exception {
		Bundle bundle = Bundles.createBundle();
		Path destination = Files.createTempFile("test", ".zip");
		destination.toFile().deleteOnExit();
		Files.delete(destination);
		assertFalse(Files.exists(destination));
		Bundles.closeAndSaveBundle(bundle, destination);
		assertTrue(Files.exists(destination));
		assertFalse(Files.exists(bundle.getSource()));
	}

	@Test
	public void closeAndSaveBundleNotDelete() throws Exception {
		Path path = Files.createTempFile("bundle", ".zip");
		Bundle bundle = Bundles.createBundle(path);
		Path destination = Files.createTempFile("test", ".zip");
		destination.toFile().deleteOnExit();
		Files.delete(destination);
		assertFalse(Files.exists(destination));
		Bundles.closeAndSaveBundle(bundle, destination);
		assertTrue(Files.exists(destination));
		assertTrue(Files.exists(bundle.getSource()));
	}

	@Test
	public void closeBundle() throws Exception {
		Bundle bundle = Bundles.createBundle();
		Path zip = Bundles.closeBundle(bundle);
		assertTrue(Files.isReadable(zip));
		assertEquals(zip, bundle.getSource());
		checkSignature(zip);
	}

  @Ignore("Broken in OpenJDK8 zipfs")
	@Test
	public void mimeTypePosition() throws Exception {
		Bundle bundle = Bundles.createBundle();
		String mimetype = "application/x-test";
		Bundles.setMimeType(bundle, mimetype);
		assertEquals(mimetype, Bundles.getMimeType(bundle));
		Path zip = Bundles.closeBundle(bundle);

		assertTrue(Files.exists(zip));
		try (ZipFile zipFile = new ZipFile(zip.toFile())) {
			// Must be first entry
			ZipEntry mimeEntry = zipFile.entries().nextElement();
			assertEquals("First zip entry is not 'mimetype'", "mimetype",
					mimeEntry.getName());
			assertEquals(
					"mimetype should be uncompressed, but compressed size mismatch",
					mimeEntry.getCompressedSize(), mimeEntry.getSize());
			assertEquals("mimetype should have STORED method", ZipEntry.STORED,
					mimeEntry.getMethod());
			assertEquals("Wrong mimetype", mimetype, IOUtils.toString(
					zipFile.getInputStream(mimeEntry), "ASCII"));
		}

		// Check position 30++ according to
		// http://livedocs.adobe.com/navigator/9/Navigator_SDK9_HTMLHelp/wwhelp/wwhimpl/common/html/wwhelp.htm?context=Navigator_SDK9_HTMLHelp&file=Appx_Packaging.6.1.html#1522568
		byte[] expected = ("mimetype" + mimetype + "PK").getBytes("ASCII");
		FileInputStream in = new FileInputStream(zip.toFile());
		byte[] actual = new byte[expected.length];

		try {
      
			assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
			assertEquals(expected.length, in.read(actual));
		} finally {
			in.close();
		}
		assertArrayEquals(expected, actual);
	}

	@Test
	public void createBundle() throws Exception {
		Path source = null;
		try (Bundle bundle = Bundles.createBundle()) {
			assertTrue(Files.isDirectory(bundle.getRoot()));
			source = bundle.getSource();
			assertTrue(Files.exists(source));
		}
		// As it was temporary file it should be deleted on close
		assertFalse(Files.exists(source));
	}

	@Test
	public void createBundlePath() throws Exception {
		Path source = Files.createTempFile("test", ".zip");
		source.toFile().deleteOnExit();
		Files.delete(source);
		try (Bundle bundle = Bundles.createBundle(source)) {
			assertTrue(Files.isDirectory(bundle.getRoot()));
			assertEquals(source, bundle.getSource());
			assertTrue(Files.exists(source));
		}
		// As it was a specific path, it should NOT be deleted on close
		assertTrue(Files.exists(source));
	}

	@Test
	public void createBundlePathExists() throws Exception {
		Path source = Files.createTempFile("test", ".zip");
		source.toFile().deleteOnExit();

		assertTrue(Files.exists(source)); // will be overwritten
		try (Bundle bundle = Bundles.createBundle(source)) {
		}
		// As it was a specific path, it should NOT be deleted on close
		assertTrue(Files.exists(source));
	}

	@Test(expected = IOException.class)
	public void createBundleExistsAsDirFails() throws Exception {
		Path source = Files.createTempDirectory("test");
		source.toFile().deleteOnExit();
		try (Bundle bundle = Bundles.createBundle(source)) {
		}
	}

	@Test
	public void getMimeType() throws Exception {
		Path bundlePath = TemporaryFiles.temporaryBundle();
		try (BundleFileSystem bundleFs = BundleFileSystemProvider
				.newFileSystemFromNew(bundlePath, "application/x-test")) {
			Bundle bundle = new Bundle(bundleFs.getPath("/"), false);
			assertEquals("application/x-test", Bundles.getMimeType(bundle));
		}
	}

	@Test
	public void setMimeType() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path mimetypePath = bundle.getRoot().resolve("mimetype");
			assertEquals("application/vnd.wf4ever.robundle+zip",
					Bundles.getStringValue(mimetypePath));

			Bundles.setMimeType(bundle, "application/x-test");
			assertEquals("application/x-test",
					Bundles.getStringValue(mimetypePath));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void setMimeTypeNoNewlines() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Bundles.setMimeType(bundle,
					"application/x-test\nNo newlines allowed");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void setMimeTypeNoSlash() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Bundles.setMimeType(bundle, "test");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void setMimeTypeEmpty() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Bundles.setMimeType(bundle, "");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void setMimeTypeNonAscii() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Bundles.setMimeType(bundle, "application/x-test-\u00E9");
			// Include the e-acute-accent from latin1
		}
	}

	@Test
	public void getMimeTypeMissing() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path mimetypePath = bundle.getRoot().resolve("mimetype");
			Files.delete(mimetypePath);
			// Fall back according to our spec
			assertEquals("application/vnd.wf4ever.robundle+zip",
					Bundles.getMimeType(bundle));
		}
	}

	@Test(expected = IOException.class)
	public void setMimeTypeMissing() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path mimetypePath = bundle.getRoot().resolve("mimetype");
			Files.delete(mimetypePath);
			// sadly now we can't set it (the mimetype file must be uncompressed
			// and at beginning of file,
			// which we don't have the possibility to do now that file system is
			// open)
			Bundles.setMimeType(bundle, "application/x-test");
		}
	}

	@Test
	public void getReference() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path hello = bundle.getRoot().resolve("hello");
			Bundles.setReference(hello, URI.create("http://example.org/test"));
			URI uri = Bundles.getReference(hello);
			assertEquals("http://example.org/test", uri.toASCIIString());
		}
	}

	@Test
	public void getReferenceFromWin8() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path win8 = bundle.getRoot().resolve("win8");
			Path win8Url = bundle.getRoot().resolve("win8.url");
			Files.copy(getClass().getResourceAsStream("/win8.url"), win8Url);

			URI uri = Bundles.getReference(win8);
			assertEquals("http://example.com/made-in-windows-8",
					uri.toASCIIString());
		}
	}

	@Test
	public void getStringValue() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path hello = bundle.getRoot().resolve("hello");
			String string = "A string";
			Bundles.setStringValue(hello, string);
			assertEquals(string, Bundles.getStringValue(hello));
			assertEquals(null, Bundles.getStringValue(null));
		}
	}

	protected boolean isEmpty(Path path) throws IOException {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			return !ds.iterator().hasNext();
		}
	}

	@Test
	public void isMissing() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path missing = bundle.getRoot().resolve("missing");
			assertFalse(Bundles.isValue(missing));
			assertTrue(Bundles.isMissing(missing));
			assertFalse(Bundles.isReference(missing));
		}
	}

	@Test
	public void isReference() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path ref = bundle.getRoot().resolve("ref");
			Bundles.setReference(ref, URI.create("http://example.org/test"));
			assertTrue(Bundles.isReference(ref));
			assertFalse(Bundles.isMissing(ref));
			assertFalse(Bundles.isValue(ref));
		}
	}

	@Test
	public void isValue() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {

			Path hello = bundle.getRoot().resolve("hello");
			Bundles.setStringValue(hello, "Hello");
			assertTrue(Bundles.isValue(hello));
			assertFalse(Bundles.isReference(hello));
		}
	}

	protected List<String> ls(Path path) throws IOException {
		List<String> paths = new ArrayList<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			for (Path p : ds) {
				paths.add(p.getFileName() + "");
			}
		}
		Collections.sort(paths);
		return paths;
	}

	@Test
	public void safeMove() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		tmp.toFile().deleteOnExit();
		Path f1 = tmp.resolve("f1");
		f1.toFile().deleteOnExit();
		Files.createFile(f1);
		assertFalse(isEmpty(tmp));

		try (Bundle db = Bundles.createBundle()) {
			Path f2 = db.getRoot().resolve("f2");
			Bundles.safeMove(f1, f2);
			assertFalse(Files.exists(f1));
			assertTrue(isEmpty(tmp));
			assertEquals(Arrays.asList("f2", "mimetype"), ls(db.getRoot()));
		}

	}

	private void checkWorkflowrunBundle(Bundle b) throws IOException {
		Path path = b.getRoot().resolve("/workflowrun.prov.ttl");
		assertTrue(Files.exists(path));
		// Ensure manifest was read
		Manifest manifest = b.getManifest();
		PathMetadata aggregation = manifest.getAggregation(path);
		assertNotNull(aggregation);
		@SuppressWarnings("deprecation")
		URI proxy = aggregation.getProxy();
		if (proxy != null)// FIXME proxy is normally null here? WHY?
			assertEquals(
					URI.create("urn:uuid:ac1c89cc-3ba2-462d-bd82-ab5b8297f98e"),
					proxy);
	}

	@Test
	public void openBundleInputStream() throws Exception {
		try (InputStream stream = getClass().getResourceAsStream(
				"/workflowrun.bundle.zip")) {
			assertNotNull(stream);
			try (Bundle b = Bundles.openBundle(stream)) {
				checkWorkflowrunBundle(b);
			}
		}
	}

	@Test
	public void openBundleURL() throws Exception {
		Path temp = Files.createTempFile("workflowrun", ".bundle.zip");
		// Don't use URL from getResource(), copy to
		// temporary file to make sure our classpath file is not
		// overwritten on b.close()!
		try (InputStream stream = getClass().getResourceAsStream(
				"/workflowrun.bundle.zip")) {
			Files.copy(stream, temp, StandardCopyOption.REPLACE_EXISTING);
		}
		URL url = temp.toUri().toURL();
		try (Bundle b = Bundles.openBundle(url)) {
			checkWorkflowrunBundle(b);
		}
	}

	@Test
	public void openBundleURLNonFile() throws Exception {
		final URL url = getClass().getResource("/workflowrun.bundle.zip");
		assertNotNull(url);
		URLStreamHandler handler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return url.openConnection();
			}
		};
		URL testUrl = new URL("test", "test", 0, "test", handler);
		try (Bundle b = Bundles.openBundle(testUrl)) {
			checkWorkflowrunBundle(b);
		}
	}

	@Test
	public void openBundleReadOnly() throws Exception {
		Path untouched = Files
				.createTempFile("test-openBundleReadOnly", ".zip");
		try (Bundle bundle = Bundles.createBundle(untouched)) {
			Bundles.setStringValue(bundle.getRoot().resolve("file.txt"),
					"Untouched");
		}
		try (Bundle readOnly = Bundles.openBundleReadOnly(untouched)) {
			Path file = readOnly.getRoot().resolve("file.txt");
			// You can change the open file system
			Bundles.setStringValue(file, "Modified");
			assertEquals("Modified", Bundles.getStringValue(file));
			// and even make new resources
			Path newFile = readOnly.getRoot().resolve("newfile.txt");
			Files.createFile(newFile);
			assertTrue(Files.exists(newFile));

		}
		try (Bundle readOnly = Bundles.openBundleReadOnly(untouched)) {
			// But that is not persisted in the zip
			Path file = readOnly.getRoot().resolve("file.txt");
			assertEquals("Untouched", Bundles.getStringValue(file));
			Path newfile = readOnly.getRoot().resolve("newfile.txt");
			assertFalse(Files.exists(newfile));
		}
	}

	@Test
	public void safeCopy() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		tmp.toFile().deleteOnExit();
		Path f1 = tmp.resolve("f1");
		f1.toFile().deleteOnExit();
		Files.createFile(f1);
		assertFalse(isEmpty(tmp));

		try (Bundle db = Bundles.createBundle()) {
			Path f2 = db.getRoot().resolve("f2");
			Bundles.safeCopy(f1, f2);
			assertTrue(Files.exists(f1));
			assertTrue(Files.exists(f2));
			assertEquals(Arrays.asList("f2", "mimetype"), ls(db.getRoot()));
		}

	}

	@Test(expected = DirectoryNotEmptyException.class)
	public void safeCopyFails() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		tmp.toFile().deleteOnExit();
		Path f1 = tmp.resolve("f1");
		f1.toFile().deleteOnExit();
		Path d1 = tmp.resolve("d1");
		d1.toFile().deleteOnExit();
		Files.createFile(f1);

		// Make d1 difficult to overwrite
		Files.createDirectory(d1);
		Files.createFile(d1.resolve("child"));

		try {
			// Files.copy(f1, d1, StandardCopyOption.REPLACE_EXISTING);
			Bundles.safeCopy(f1, d1);
		} finally {
			assertEquals(Arrays.asList("d1", "f1"), ls(tmp));
			assertTrue(Files.exists(f1));
			assertTrue(Files.isDirectory(d1));
		}
	}

	@Test(expected = IOException.class)
	public void safeMoveFails() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		tmp.toFile().deleteOnExit();
		Path f1 = tmp.resolve("f1");
		f1.toFile().deleteOnExit();
		Path d1 = tmp.resolve("d1");
		d1.toFile().deleteOnExit();
		Files.createFile(f1);

		// Make d1 difficult to overwrite
		Files.createDirectory(d1);
		Files.createFile(d1.resolve("child"));

		try {
			Bundles.safeMove(f1, d1);
		} finally {
			assertTrue(Files.exists(f1));
			assertEquals(Arrays.asList("d1", "f1"), ls(tmp));
		}
	}

	@Test
	public void setReference() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {

			Path ref = bundle.getRoot().resolve("ref");
			Bundles.setReference(ref, URI.create("http://example.org/test"));

			URI uri = URI.create("http://example.org/test");
			Path f = Bundles.setReference(ref, uri);
			assertEquals("ref.url", f.getFileName().toString());
			assertEquals(bundle.getRoot(), f.getParent());
			assertFalse(Files.exists(ref));

			List<String> uriLines = Files.readAllLines(f,
					Charset.forName("ASCII"));
			assertEquals(3, uriLines.size());
			assertEquals("[InternetShortcut]", uriLines.get(0));
			assertEquals("URL=http://example.org/test", uriLines.get(1));
			assertEquals("", uriLines.get(2));
		}
	}

	@Test
	public void setReferenceIri() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path ref = bundle.getRoot().resolve("ref");
			URI uri = new URI("http", "xn--bcher-kva.example.com",
					"/s\u00F8iland/\u2603snowman", "\u2605star");
			Path f = Bundles.setReference(ref, uri);
			List<String> uriLines = Files.readAllLines(f,
					Charset.forName("ASCII"));
			// TODO: Double-check that this is actually correct escaping :)
			assertEquals(
					"URL=http://xn--bcher-kva.example.com/s%C3%B8iland/%E2%98%83snowman#%E2%98%85star",
					uriLines.get(1));
		}
	}

	@Test
	public void setStringValue() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			Path file = bundle.getRoot().resolve("file");
			String string = "A string";
			Bundles.setStringValue(file, string);
			assertEquals(string,
					Files.readAllLines(file, Charset.forName("UTF-8")).get(0));
		}
	}

	@Test
	public void withExtension() throws Exception {
		Path testDir = Files.createTempDirectory("test");
		testDir.toFile().deleteOnExit();
		Path fileTxt = testDir.resolve("file.txt");
		fileTxt.toFile().deleteOnExit();
		assertEquals("file.txt", fileTxt.getFileName().toString()); // better
																	// be!

		Path fileHtml = Bundles.withExtension(fileTxt, ".html");
		assertEquals(fileTxt.getParent(), fileHtml.getParent());
		assertEquals("file.html", fileHtml.getFileName().toString());

		Path fileDot = Bundles.withExtension(fileTxt, ".");
		assertEquals("file.", fileDot.getFileName().toString());

		Path fileEmpty = Bundles.withExtension(fileTxt, "");
		assertEquals("file", fileEmpty.getFileName().toString());

		Path fileDoc = Bundles.withExtension(fileEmpty, ".doc");
		assertEquals("file.doc", fileDoc.getFileName().toString());

		Path fileManyPdf = Bundles.withExtension(fileTxt, ".test.many.pdf");
		assertEquals("file.test.many.pdf", fileManyPdf.getFileName().toString());

		Path fileManyTxt = Bundles.withExtension(fileManyPdf, ".txt");
		assertEquals("file.test.many.txt", fileManyTxt.getFileName().toString());
	}

}
