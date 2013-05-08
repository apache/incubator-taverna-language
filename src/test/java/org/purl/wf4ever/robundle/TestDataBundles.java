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
import org.purl.wf4ever.robundle.DataBundle;
import org.purl.wf4ever.robundle.ROBundles;
import org.purl.wf4ever.robundle.ErrorDocument;

public class TestDataBundles {
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
		DataBundle dataBundle = ROBundles.createDataBundle();
		assertTrue(Files.exists(dataBundle.getSource()));
		assertTrue(dataBundle.getRoot().getFileSystem().isOpen());
		ROBundles.getInputs(dataBundle);

		dataBundle.close();
		assertFalse(Files.exists(dataBundle.getSource()));
		assertFalse(dataBundle.getRoot().getFileSystem().isOpen());

	}

	@Test
	public void closeAndOpenDataBundle() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path zip = ROBundles.closeDataBundle(dataBundle);
		ROBundles.openDataBundle(zip);
	}

	@Test
	public void closeAndOpenDataBundleWithPortValue() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path port = ROBundles.getPort(inputs, "hello");
		ROBundles.setStringValue(port, "Hello");
		Path zip = ROBundles.closeDataBundle(dataBundle);

		DataBundle newDataBundle = ROBundles.openDataBundle(zip);
		Path newInput = ROBundles.getInputs(newDataBundle);
		Path newPort = ROBundles.getPort(newInput, "hello");
		assertEquals("Hello", ROBundles.getStringValue(newPort));
	}

	@Test
	public void closeAndSaveDataBundle() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		ROBundles.getInputs(dataBundle);
		Path destination = Files.createTempFile("test", ".zip");
		Files.delete(destination);
		assertFalse(Files.exists(destination));
		ROBundles.closeAndSaveDataBundle(dataBundle, destination);
		assertTrue(Files.exists(destination));
	}

	@Test
	public void closeDataBundle() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path zip = ROBundles.closeDataBundle(dataBundle);
		assertTrue(Files.isReadable(zip));
		assertEquals(zip, dataBundle.getSource());
		checkSignature(zip);
	}

	@Test
	public void createDataBundle() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
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
	public void createList() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path list = ROBundles.getPort(inputs, "in1");
		ROBundles.createList(list);
		assertTrue(Files.isDirectory(list));
	}

	@Test
	public void getError() throws Exception {
		
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		ROBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");		
		
		ErrorDocument error = ROBundles.getError(portIn1);
		assertTrue(error.getCausedBy().isEmpty());
		
		assertEquals("Something did not work", error.getMessage());
		// Notice that the lack of trailing \n is preserved 
		assertEquals("A very\n long\n error\n trace", error.getTrace());	
		
		assertEquals(null, ROBundles.getError(null));
	}

	@Test
	public void getErrorCause() throws Exception {		
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		Path cause1 = ROBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");
		Path portIn2 = ROBundles.getPort(inputs, "in2");
		Path cause2 = ROBundles.setError(portIn2, "Something else did not work", "Shorter trace");
		
		
		Path outputs = ROBundles.getOutputs(dataBundle);
		Path portOut1 = ROBundles.getPort(outputs, "out1");
		ROBundles.setError(portOut1, "Errors in input", "", cause1, cause2);

		ErrorDocument error = ROBundles.getError(portOut1);
		assertEquals("Errors in input", error.getMessage());
		assertEquals("", error.getTrace());
		assertEquals(2, error.getCausedBy().size());
		
		assertTrue(Files.isSameFile(cause1, error.getCausedBy().get(0)));
		assertTrue(Files.isSameFile(cause2, error.getCausedBy().get(1)));
	}

	@Test
	public void getInputs() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		// Second time should not fail because it already exists
		inputs = ROBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		assertEquals(dataBundle.getRoot(), inputs.getParent());
	}

	@Test
	public void getList() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path list = ROBundles.getPort(inputs, "in1");
		ROBundles.createList(list);
		for (int i = 0; i < 5; i++) {
			Path item = ROBundles.newListItem(list);
			ROBundles.setStringValue(item, "test" + i);
		}
		List<Path> paths = ROBundles.getList(list);
		assertEquals(5, paths.size());
		assertEquals("test0", ROBundles.getStringValue(paths.get(0)));
		assertEquals("test4", ROBundles.getStringValue(paths.get(4)));
		
		assertEquals(null, ROBundles.getList(null));
	}

	@Test
	public void getListItem() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path list = ROBundles.getPort(inputs, "in1");
		ROBundles.createList(list);
		for (int i = 0; i < 5; i++) {
			Path item = ROBundles.newListItem(list);
			ROBundles.setStringValue(item, "item " + i);
		}
		// set at next available position
		Path item5 = ROBundles.getListItem(list, 5);
		assertTrue(item5.getFileName().toString().contains("5"));
		ROBundles.setStringValue(item5, "item 5");
	
		
		// set somewhere later
		Path item8 = ROBundles.getListItem(list, 8);
		assertTrue(item8.getFileName().toString().contains("8"));
		ROBundles.setStringValue(item8, "item 8");
		
		Path item7 = ROBundles.getListItem(list, 7);
		assertFalse(Files.exists(item7));
		assertFalse(ROBundles.isList(item7));
		assertFalse(ROBundles.isError(item7));
		assertFalse(ROBundles.isValue(item7));
		// TODO: Is it really missing? item1337 is also missing..
		assertTrue(ROBundles.isMissing(item7));
		
		
		// overwrite #2
		Path item2 = ROBundles.getListItem(list, 2);		
		ROBundles.setStringValue(item2, "replaced");
		
		
		List<Path> listItems = ROBundles.getList(list);
		assertEquals(9, listItems.size());
		assertEquals("item 0", ROBundles.getStringValue(listItems.get(0)));
		assertEquals("item 1", ROBundles.getStringValue(listItems.get(1)));
		assertEquals("replaced", ROBundles.getStringValue(listItems.get(2)));
		assertEquals("item 3", ROBundles.getStringValue(listItems.get(3)));
		assertEquals("item 4", ROBundles.getStringValue(listItems.get(4)));
		assertEquals("item 5", ROBundles.getStringValue(listItems.get(5)));
		assertNull(listItems.get(6));
		assertNull(listItems.get(7));
		assertEquals("item 8", ROBundles.getStringValue(listItems.get(8)));
		
	}

	@Test
	public void getOutputs() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path outputs = ROBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		// Second time should not fail because it already exists
		outputs = ROBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		assertEquals(dataBundle.getRoot(), outputs.getParent());
	}

	@Test
	public void getPort() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		assertFalse(Files.exists(portIn1));
		assertEquals(inputs, portIn1.getParent());
	}

	@Test
	public void getPorts() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		ROBundles.createList(ROBundles.getPort(inputs, "in1"));
		ROBundles.createList(ROBundles.getPort(inputs, "in2"));
		ROBundles.setStringValue(ROBundles.getPort(inputs, "value"),
				"A value");
		Map<String, Path> ports = ROBundles.getPorts(ROBundles
				.getInputs(dataBundle));
		assertEquals(3, ports.size());
//		System.out.println(ports);
		assertTrue(ports.containsKey("in1"));
		assertTrue(ports.containsKey("in2"));
		assertTrue(ports.containsKey("value"));

		assertEquals("A value", ROBundles.getStringValue(ports.get("value")));

	}

	@Test
	public void getReference() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		ROBundles.setReference(portIn1, URI.create("http://example.org/test"));
		URI uri = ROBundles.getReference(portIn1);
		assertEquals("http://example.org/test", uri.toASCIIString());
	}

	@Test
	public void getReferenceFromWin8() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path win8 = inputs.resolve("win8.url");
		Files.copy(getClass().getResourceAsStream("/win8.url"), win8);
				
		URI uri = ROBundles.getReference(ROBundles.getPort(inputs, "win8"));
		assertEquals("http://example.com/made-in-windows-8", uri.toASCIIString());
	}

	@Test
	public void getStringValue() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		String string = "A string";
		ROBundles.setStringValue(portIn1, string);
		assertEquals(string, ROBundles.getStringValue(portIn1));	
		assertEquals(null, ROBundles.getStringValue(null));
	}

	@Test
	public void hasInputs() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		assertFalse(ROBundles.hasInputs(dataBundle));
		ROBundles.getInputs(dataBundle); // create on demand
		assertTrue(ROBundles.hasInputs(dataBundle));
	}

	@Test
	public void hasOutputs() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		assertFalse(ROBundles.hasOutputs(dataBundle));
		ROBundles.getInputs(dataBundle); // independent
		assertFalse(ROBundles.hasOutputs(dataBundle));
		ROBundles.getOutputs(dataBundle); // create on demand
		assertTrue(ROBundles.hasOutputs(dataBundle));
	}
	
	protected boolean isEmpty(Path path) throws IOException {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			return !ds.iterator().hasNext();
		}
	}

	@Test
	public void isError() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		ROBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");		
		
		assertFalse(ROBundles.isList(portIn1));		
		assertFalse(ROBundles.isValue(portIn1));
		assertFalse(ROBundles.isMissing(portIn1));
		assertFalse(ROBundles.isReference(portIn1));
		assertTrue(ROBundles.isError(portIn1));		
	}

	@Test
	public void isList() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path list = ROBundles.getPort(inputs, "in1");
		ROBundles.createList(list);
		assertTrue(ROBundles.isList(list));
		assertFalse(ROBundles.isValue(list));
		assertFalse(ROBundles.isError(list));
		assertFalse(ROBundles.isReference(list));
		assertFalse(ROBundles.isMissing(list));
	}
	
	@Test
	public void isMissing() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		
		assertFalse(ROBundles.isList(portIn1));		
		assertFalse(ROBundles.isValue(portIn1));
		assertFalse(ROBundles.isError(portIn1));
		assertTrue(ROBundles.isMissing(portIn1));
		assertFalse(ROBundles.isReference(portIn1));
	}
	
	@Test
	public void isReference() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		ROBundles.setReference(portIn1, URI.create("http://example.org/test"));
		assertTrue(ROBundles.isReference(portIn1));
		assertFalse(ROBundles.isError(portIn1));		
		assertFalse(ROBundles.isList(portIn1));
		assertFalse(ROBundles.isMissing(portIn1));
		assertFalse(ROBundles.isValue(portIn1));
	}
	
	@Test
	public void isValue() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		ROBundles.setStringValue(portIn1, "Hello");
		assertTrue(ROBundles.isValue(portIn1));
		assertFalse(ROBundles.isList(portIn1));
		assertFalse(ROBundles.isError(portIn1));
		assertFalse(ROBundles.isReference(portIn1));
	}

	@Test
	public void listOfLists() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path list = ROBundles.getPort(inputs, "in1");
		ROBundles.createList(list);
		Path sublist0 = ROBundles.newListItem(list);
		ROBundles.createList(sublist0);
		
		Path sublist1 = ROBundles.newListItem(list);
		ROBundles.createList(sublist1);
		
		assertEquals(Arrays.asList("0/", "1/"), ls(list));
		
		ROBundles.setStringValue(ROBundles.newListItem(sublist1), 
				"Hello");
		
		assertEquals(Arrays.asList("0"), ls(sublist1));
		
		assertEquals("Hello",ROBundles.getStringValue( 
				ROBundles.getListItem(ROBundles.getListItem(list, 1), 0)));
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
	public void newListItem() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path list = ROBundles.getPort(inputs, "in1");
		ROBundles.createList(list);
		Path item0 = ROBundles.newListItem(list);
		assertEquals(list, item0.getParent());
		assertTrue(item0.getFileName().toString().contains("0"));
		assertFalse(Files.exists(item0));
		ROBundles.setStringValue(item0, "test");

		Path item1 = ROBundles.newListItem(list);
		assertTrue(item1.getFileName().toString().contains("1"));
		// Because we've not actually created item1 yet
		assertEquals(item1, ROBundles.newListItem(list));
		ROBundles.setStringValue(item1, "test");

		// Check that ROBundles.newListItem can deal with gaps
		Files.delete(item0);
		Path item2 = ROBundles.newListItem(list);
		assertTrue(item2.getFileName().toString().contains("2"));

		// Check that non-numbers don't interfere
		Path nonumber = list.resolve("nonumber");
		Files.createFile(nonumber);
		item2 = ROBundles.newListItem(list);
		assertTrue(item2.getFileName().toString().contains("2"));

		// Check that extension is stripped
		Path five = list.resolve("5.txt");
		Files.createFile(five);
		Path item6 = ROBundles.newListItem(list);
		assertTrue(item6.getFileName().toString().contains("6"));
	}
	
	
	@Test
	public void safeMove() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		Path f1 = tmp.resolve("f1");
		Files.createFile(f1);
		assertFalse(isEmpty(tmp));

		DataBundle db = ROBundles.createDataBundle();
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
	public void setErrorArgs() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		Path errorPath = ROBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");		
		assertEquals("in1.err", errorPath.getFileName().toString());

		List<String> errLines = Files.readAllLines(errorPath, Charset.forName("UTF-8"));
		assertEquals(6, errLines.size());
		assertEquals("", errLines.get(0));
		assertEquals("Something did not work", errLines.get(1));
		assertEquals("A very", errLines.get(2));
		assertEquals(" long", errLines.get(3));
		assertEquals(" error", errLines.get(4));
		assertEquals(" trace", errLines.get(5));
	}
	
	@Test
	public void setErrorCause() throws Exception {		
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		Path cause1 = ROBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");
		Path portIn2 = ROBundles.getPort(inputs, "in2");
		Path cause2 = ROBundles.setError(portIn2, "Something else did not work", "Shorter trace");
		
		
		Path outputs = ROBundles.getOutputs(dataBundle);
		Path portOut1 = ROBundles.getPort(outputs, "out1");
		Path errorPath = ROBundles.setError(portOut1, "Errors in input", "", cause1, cause2);
		
		List<String> errLines = Files.readAllLines(errorPath, Charset.forName("UTF-8"));
		assertEquals("../inputs/in1.err", errLines.get(0));
		assertEquals("../inputs/in2.err", errLines.get(1));
		assertEquals("", errLines.get(2));
	}
	
	@Test
	public void setErrorObj() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);

		Path portIn1 = ROBundles.getPort(inputs, "in1");
		Path cause1 = ROBundles.setError(portIn1, "a", "b");
		Path portIn2 = ROBundles.getPort(inputs, "in2");
		Path cause2 = ROBundles.setError(portIn2, "c", "d");
		
		
		Path outputs = ROBundles.getOutputs(dataBundle);
		Path portOut1 = ROBundles.getPort(outputs, "out1");

		ErrorDocument error = new ErrorDocument();
		error.getCausedBy().add(cause1);
		error.getCausedBy().add(cause2);
		
		error.setMessage("Something did not work");
		error.setTrace("Here\nis\nwhy\n");
		
		Path errorPath = ROBundles.setError(portOut1, error);		
		assertEquals("out1.err", errorPath.getFileName().toString());

		List<String> errLines = Files.readAllLines(errorPath, Charset.forName("UTF-8"));
		assertEquals(8, errLines.size());
		assertEquals("../inputs/in1.err", errLines.get(0));
		assertEquals("../inputs/in2.err", errLines.get(1));
		assertEquals("", errLines.get(2));
		assertEquals("Something did not work", errLines.get(3));
		assertEquals("Here", errLines.get(4));
		assertEquals("is", errLines.get(5));
		assertEquals("why", errLines.get(6));
		assertEquals("", errLines.get(7));
	}
	
	
	@Test
	public void setReference() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		URI uri = URI.create("http://example.org/test");		
		Path f = ROBundles.setReference(portIn1, uri);
		assertEquals("in1.url", f.getFileName().toString());
		assertEquals(inputs, f.getParent());
		assertFalse(Files.exists(portIn1));		
		
		List<String> uriLines = Files.readAllLines(f, Charset.forName("ASCII"));
		assertEquals(3, uriLines.size());
		assertEquals("[InternetShortcut]", uriLines.get(0));
		assertEquals("URL=http://example.org/test", uriLines.get(1));
		assertEquals("", uriLines.get(2));				
	}
	
	@Test
	public void setReferenceIri() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");		
		URI uri = new URI("http", "xn--bcher-kva.example.com", "/s\u00F8iland/\u2603snowman", "\u2605star");
		Path f = ROBundles.setReference(portIn1, uri);
		List<String> uriLines = Files.readAllLines(f, Charset.forName("ASCII"));
		// TODO: Double-check that this is actually correct escaping :)
		assertEquals("URL=http://xn--bcher-kva.example.com/s%C3%B8iland/%E2%98%83snowman#%E2%98%85star", 
				uriLines.get(1));
	}

	@Test
	public void setStringValue() throws Exception {
		DataBundle dataBundle = ROBundles.createDataBundle();
		Path inputs = ROBundles.getInputs(dataBundle);
		Path portIn1 = ROBundles.getPort(inputs, "in1");
		String string = "A string";
		ROBundles.setStringValue(portIn1, string);
		assertEquals(string, Files.readAllLines(portIn1, Charset.forName("UTF-8")).get(0));
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

