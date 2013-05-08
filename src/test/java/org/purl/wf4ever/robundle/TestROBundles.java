package org.purl.wf4ever.robundle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.purl.wf4ever.robundle.ROBundle;
import org.purl.wf4ever.robundle.ROBundles;

public class TestROBundles {
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
	public void close() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		assertTrue(Files.exists(dataBundle.getSource()));
		assertTrue(dataBundle.getRoot().getFileSystem().isOpen());

		dataBundle.close();
		assertFalse(Files.exists(dataBundle.getSource()));
		assertFalse(dataBundle.getRoot().getFileSystem().isOpen());

	}

	@Test
	public void closeAndOpenDataBundle() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path zip = ROBundles.closeDataBundle(dataBundle);
		ROBundles.openDataBundle(zip);
	}

	@Test
	public void closeAndOpenDataBundleWithPortValue() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path hello = dataBundle.getRoot().resolve("hello.txt");
		ROBundles.setStringValue(hello, "Hello");
		Path zip = ROBundles.closeDataBundle(dataBundle);

		ROBundle newDataBundle = ROBundles.openDataBundle(zip);
		Path newHello = newDataBundle.getRoot().resolve("hello.txt");		
		assertEquals("Hello", ROBundles.getStringValue(newHello));
	}

	@Test
	public void closeAndSaveDataBundle() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path destination = Files.createTempFile("test", ".zip");
		Files.delete(destination);
		assertFalse(Files.exists(destination));
		ROBundles.closeAndSaveDataBundle(dataBundle, destination);
		assertTrue(Files.exists(destination));
	}

	@Test
	public void closeDataBundle() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path zip = ROBundles.closeDataBundle(dataBundle);
		assertTrue(Files.isReadable(zip));
		assertEquals(zip, dataBundle.getSource());
		checkSignature(zip);
	}

	@Test
	public void createDataBundle() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		assertTrue(Files.isDirectory(dataBundle.getRoot()));
		// TODO: Should this instead return a FileSystem so we can close() it?
	}

	@Test
	public void createFSfromJar() throws Exception {
		Path path = Files.createTempFile("test.zip", null);
		Files.delete(path);
		try (FileSystem fs = ROBundles.createFSfromJar(path)) {
			assertNotSame(fs, path.getFileSystem());
		}
		assertTrue(Files.exists(path));
	}

	@Test
	public void createFSfromZip() throws Exception {
		Path path = Files.createTempFile("test", null);
		Files.delete(path);
		try (FileSystem fs = ROBundles.createFSfromZip(path)) {
			assertNotSame(fs, path.getFileSystem());
		}
		assertTrue(Files.exists(path));
	}

	@Test
	public void getReference() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path hello = dataBundle.getRoot().resolve("hello");
		ROBundles.setReference(hello, URI.create("http://example.org/test"));
		URI uri = ROBundles.getReference(hello);
		assertEquals("http://example.org/test", uri.toASCIIString());
	}

	@Test
	public void getReferenceFromWin8() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path win8 = dataBundle.getRoot().resolve("win8");
		Path win8Url = dataBundle.getRoot().resolve("win8.url");
		Files.copy(getClass().getResourceAsStream("/win8.url"), win8Url);
				
		URI uri = ROBundles.getReference(win8);
		assertEquals("http://example.com/made-in-windows-8", uri.toASCIIString());
	}

	@Test
	public void getStringValue() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path hello = dataBundle.getRoot().resolve("hello");
		String string = "A string";
		ROBundles.setStringValue(hello, string);
		assertEquals(string, ROBundles.getStringValue(hello));	
		assertEquals(null, ROBundles.getStringValue(null));
	}

	protected boolean isEmpty(Path path) throws IOException {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			return !ds.iterator().hasNext();
		}
	}


	@Test
	public void isMissing() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path missing = dataBundle.getRoot().resolve("missing");		
		assertFalse(ROBundles.isValue(missing));
		assertTrue(ROBundles.isMissing(missing));
		assertFalse(ROBundles.isReference(missing));
	}
	
	@Test
	public void isReference() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path ref = dataBundle.getRoot().resolve("ref");		
		ROBundles.setReference(ref, URI.create("http://example.org/test"));
		assertTrue(ROBundles.isReference(ref));
		assertFalse(ROBundles.isMissing(ref));
		assertFalse(ROBundles.isValue(ref));
	}
	
	@Test
	public void isValue() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path hello = dataBundle.getRoot().resolve("hello");		
		ROBundles.setStringValue(hello, "Hello");
		assertTrue(ROBundles.isValue(hello));
		assertFalse(ROBundles.isReference(hello));
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
		Path f1 = tmp.resolve("f1");
		Files.createFile(f1);
		assertFalse(isEmpty(tmp));

		ROBundle db = ROBundles.createDataBundle();
		Path f2 = db.getRoot().resolve("f2");
		ROBundles.safeMove(f1, f2);
		assertTrue(isEmpty(tmp));
		assertEquals(Arrays.asList("f2", "mimetype"), ls(db.getRoot()));

	}
	

	@Test(expected = IOException.class)
	public void safeMoveFails() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		Path f1 = tmp.resolve("f1");
		Path d1 = tmp.resolve("d1");
		Files.createFile(f1);
		Files.createDirectory(d1);
		try {
			ROBundles.safeMove(f1, d1);
		} finally {
			assertTrue(Files.exists(f1));
			assertEquals(Arrays.asList("d1", "f1"), ls(tmp));
		}
	}
	
	@Test
	public void setReference() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		
		Path ref = dataBundle.getRoot().resolve("ref");		
		ROBundles.setReference(ref, URI.create("http://example.org/test"));
		
		URI uri = URI.create("http://example.org/test");		
		Path f = ROBundles.setReference(ref, uri);
		assertEquals("ref.url", f.getFileName().toString());
		assertEquals(dataBundle.getRoot(), f.getParent());
		assertFalse(Files.exists(ref));		
		
		List<String> uriLines = Files.readAllLines(f, Charset.forName("ASCII"));
		assertEquals(3, uriLines.size());
		assertEquals("[InternetShortcut]", uriLines.get(0));
		assertEquals("URL=http://example.org/test", uriLines.get(1));
		assertEquals("", uriLines.get(2));				
	}
	
	@Test
	public void setReferenceIri() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path ref = dataBundle.getRoot().resolve("ref");		
		URI uri = new URI("http", "xn--bcher-kva.example.com", "/s\u00F8iland/\u2603snowman", "\u2605star");
		Path f = ROBundles.setReference(ref, uri);
		List<String> uriLines = Files.readAllLines(f, Charset.forName("ASCII"));
		// TODO: Double-check that this is actually correct escaping :)
		assertEquals("URL=http://xn--bcher-kva.example.com/s%C3%B8iland/%E2%98%83snowman#%E2%98%85star", 
				uriLines.get(1));
	}

	@Test
	public void setStringValue() throws Exception {
		ROBundle dataBundle = ROBundles.createDataBundle();
		Path ref = dataBundle.getRoot().resolve("ref");		
		String string = "A string";
		ROBundles.setStringValue(ref, string);
		assertEquals(string, Files.readAllLines(ref, Charset.forName("UTF-8")).get(0));
	}
	
	@Test
	public void withExtension() throws Exception {
		Path testDir = Files.createTempDirectory("test");
		Path fileTxt = testDir.resolve("file.txt");
		assertEquals("file.txt", fileTxt.getFileName().toString()); // better be!
		
		Path fileHtml = ROBundles.withExtension(fileTxt, ".html");
		assertEquals(fileTxt.getParent(), fileHtml.getParent());
		assertEquals("file.html", fileHtml.getFileName().toString()); 
		
		Path fileDot = ROBundles.withExtension(fileTxt, ".");
		assertEquals("file.", fileDot.getFileName().toString()); 
		
		Path fileEmpty = ROBundles.withExtension(fileTxt, "");
		assertEquals("file", fileEmpty.getFileName().toString()); 
		
		
		Path fileDoc = ROBundles.withExtension(fileEmpty, ".doc");
		assertEquals("file.doc", fileDoc.getFileName().toString());
		
		Path fileManyPdf = ROBundles.withExtension(fileTxt, ".test.many.pdf");
		assertEquals("file.test.many.pdf", fileManyPdf.getFileName().toString()); 
		
		Path fileManyTxt = ROBundles.withExtension(fileManyPdf, ".txt");
		assertEquals("file.test.many.txt", fileManyTxt.getFileName().toString());
	}

}

