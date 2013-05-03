package uk.org.taverna.databundle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
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

public class TestDataBundles {
	@Test
	public void asList() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		for (int i = 0; i < 5; i++) {
			Path item = DataBundles.newListItem(list);
			DataBundles.setStringValue(item, "test" + i);
		}
		List<Path> paths = DataBundles.getList(list);
		assertEquals(5, paths.size());
		assertEquals("test0", DataBundles.getStringValue(paths.get(0)));
		assertEquals("test4", DataBundles.getStringValue(paths.get(4)));
	}

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
		DataBundle dataBundle = DataBundles.createDataBundle();
		assertTrue(Files.exists(dataBundle.getSource()));
		assertTrue(dataBundle.getRoot().getFileSystem().isOpen());
		DataBundles.getInputs(dataBundle);

		dataBundle.close();
		assertFalse(Files.exists(dataBundle.getSource()));
		assertFalse(dataBundle.getRoot().getFileSystem().isOpen());

	}

	@Test
	public void closeAndOpenDataBundle() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path zip = DataBundles.closeDataBundle(dataBundle);
		DataBundles.openDataBundle(zip);
	}

	@Test
	public void closeAndOpenDataBundleWithPortValue() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path port = DataBundles.getPort(inputs, "hello");
		DataBundles.setStringValue(port, "Hello");
		Path zip = DataBundles.closeDataBundle(dataBundle);

		DataBundle newDataBundle = DataBundles.openDataBundle(zip);
		Path newInput = DataBundles.getInputs(newDataBundle);
		Path newPort = DataBundles.getPort(newInput, "hello");
		assertEquals("Hello", DataBundles.getStringValue(newPort));
	}

	@Test
	public void closeAndSaveDataBundle() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		DataBundles.getInputs(dataBundle);
		Path destination = Files.createTempFile("test", ".zip");
		Files.delete(destination);
		assertFalse(Files.exists(destination));
		DataBundles.closeAndSaveDataBundle(dataBundle, destination);
		assertTrue(Files.exists(destination));
	}

	@Test
	public void closeDataBundle() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path zip = DataBundles.closeDataBundle(dataBundle);
		assertTrue(Files.isReadable(zip));
		assertEquals(zip, dataBundle.getSource());
		checkSignature(zip);
	}

	@Test
	public void createDataBundle() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		assertTrue(Files.isDirectory(dataBundle.getRoot()));
		// TODO: Should this instead return a FileSystem so we can close() it?
	}

	@Test
	public void createFSfromJar() throws Exception {
		Path path = Files.createTempFile("test.zip", null);
		Files.delete(path);
		try (FileSystem fs = DataBundles.createFSfromJar(path)) {
			assertNotSame(fs, path.getFileSystem());
		}
		assertTrue(Files.exists(path));
	}

	@Test
	public void createFSfromZip() throws Exception {
		Path path = Files.createTempFile("test", null);
		Files.delete(path);
		try (FileSystem fs = DataBundles.createFSfromZip(path)) {
			assertNotSame(fs, path.getFileSystem());
		}
		assertTrue(Files.exists(path));
	}

	@Test
	public void createList() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(Files.isDirectory(list));
	}

	@Test
	public void getInputs() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		// Second time should not fail because it already exists
		inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		assertEquals(dataBundle.getRoot(), inputs.getParent());
	}

	@Test
	public void getOutputs() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		// Second time should not fail because it already exists
		outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		assertEquals(dataBundle.getRoot(), outputs.getParent());
	}

	@Test
	public void getPort() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		assertFalse(Files.exists(portIn1));
		assertEquals(inputs, portIn1.getParent());
	}

	@Test
	public void getPorts() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		DataBundles.createList(DataBundles.getPort(inputs, "in1"));
		DataBundles.createList(DataBundles.getPort(inputs, "in2"));
		DataBundles.setStringValue(DataBundles.getPort(inputs, "value"),
				"A value");
		Map<String, Path> ports = DataBundles.getPorts(DataBundles
				.getInputs(dataBundle));
		assertEquals(3, ports.size());
		System.out.println(ports);
		assertTrue(ports.containsKey("in1"));
		assertTrue(ports.containsKey("in2"));
		assertTrue(ports.containsKey("value"));

		assertEquals("A value", DataBundles.getStringValue(ports.get("value")));

	}

	@Test
	public void hasInputs() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		assertFalse(DataBundles.hasInputs(dataBundle));
		DataBundles.getInputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasInputs(dataBundle));
	}

	@Test
	public void getListItem() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		for (int i = 0; i < 5; i++) {
			Path item = DataBundles.newListItem(list);
			DataBundles.setStringValue(item, "item " + i);
		}
		// set at next available position
		Path item5 = DataBundles.getListItem(list, 5);
		assertTrue(item5.getFileName().toString().contains("5"));
		DataBundles.setStringValue(item5, "item 5");
	
		
		// set somewhere later
		Path item8 = DataBundles.getListItem(list, 8);
		assertTrue(item8.getFileName().toString().contains("8"));
		DataBundles.setStringValue(item8, "item 8");
		
		Path item7 = DataBundles.getListItem(list, 7);
		assertFalse(Files.exists(item7));
		assertFalse(DataBundles.isList(item7));
		assertFalse(DataBundles.isValue(item7));
		// TODO: Is it really missing? item1337 is also missing..
		assertTrue(DataBundles.isMissing(item7));
		
		
		// overwrite #2
		Path item2 = DataBundles.getListItem(list, 2);		
		DataBundles.setStringValue(item2, "replaced");
		
		
		List<Path> listItems = DataBundles.getList(list);
		assertEquals(9, listItems.size());
		assertEquals("item 0", DataBundles.getStringValue(listItems.get(0)));
		assertEquals("item 1", DataBundles.getStringValue(listItems.get(1)));
		assertEquals("replaced", DataBundles.getStringValue(listItems.get(2)));
		assertEquals("item 3", DataBundles.getStringValue(listItems.get(3)));
		assertEquals("item 4", DataBundles.getStringValue(listItems.get(4)));
		assertEquals("item 5", DataBundles.getStringValue(listItems.get(5)));
		assertNull(listItems.get(6));
		assertNull(listItems.get(7));
		assertEquals("item 8", DataBundles.getStringValue(listItems.get(8)));
		
	}

	@Test
	public void hasOutputs() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getInputs(dataBundle); // independent
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getOutputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasOutputs(dataBundle));
	}

	protected boolean isEmpty(Path path) throws IOException {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			return !ds.iterator().hasNext();
		}
	}

	@Test
	public void isList() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(DataBundles.isList(list));
		assertFalse(DataBundles.isValue(list));
	}

	@Test
	public void isValue() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		DataBundles.setStringValue(portIn1, "Hello");
		assertTrue(DataBundles.isValue(portIn1));
		assertFalse(DataBundles.isList(portIn1));
	}

	@Test
	public void listOfLists() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		Path sublist0 = DataBundles.newListItem(list);
		DataBundles.createList(sublist0);
		
		Path sublist1 = DataBundles.newListItem(list);
		DataBundles.createList(sublist1);
		
		assertEquals(Arrays.asList("0/", "1/"), ls(list));
		
		DataBundles.setStringValue(DataBundles.newListItem(sublist1), 
				"Hello");
		
		assertEquals(Arrays.asList("0"), ls(sublist1));
		
		assertEquals("Hello",DataBundles.getStringValue( 
				DataBundles.getListItem(DataBundles.getListItem(list, 1), 0)));
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
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		Path item0 = DataBundles.newListItem(list);
		assertEquals(list, item0.getParent());
		assertTrue(item0.getFileName().toString().contains("0"));
		assertFalse(Files.exists(item0));
		DataBundles.setStringValue(item0, "test");

		Path item1 = DataBundles.newListItem(list);
		assertTrue(item1.getFileName().toString().contains("1"));
		// Because we've not actually created item1 yet
		assertEquals(item1, DataBundles.newListItem(list));
		DataBundles.setStringValue(item1, "test");

		// Check that DataBundles.newListItem can deal with gaps
		Files.delete(item0);
		Path item2 = DataBundles.newListItem(list);
		assertTrue(item2.getFileName().toString().contains("2"));

		// Check that non-numbers don't interfere
		Path nonumber = list.resolve("nonumber");
		Files.createFile(nonumber);
		item2 = DataBundles.newListItem(list);
		assertTrue(item2.getFileName().toString().contains("2"));

		// Check that extension is stripped
		Path five = list.resolve("5.txt");
		Files.createFile(five);
		Path item6 = DataBundles.newListItem(list);
		assertTrue(item6.getFileName().toString().contains("6"));
	}

	@Test
	public void safeMove() throws Exception {
		Path tmp = Files.createTempDirectory("test");
		Path f1 = tmp.resolve("f1");
		Files.createFile(f1);
		assertFalse(isEmpty(tmp));

		DataBundle db = DataBundles.createDataBundle();
		Path f2 = db.getRoot().resolve("f2");
		DataBundles.safeMove(f1, f2);
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
			DataBundles.safeMove(f1, d1);
		} finally {
			assertTrue(Files.exists(f1));
			assertEquals(Arrays.asList("d1", "f1"), ls(tmp));
		}
	}

	@Test
	public void setStringValue() throws Exception {
		DataBundle dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		String string = "A string";
		DataBundles.setStringValue(portIn1, string);
		assertTrue(Files.exists(portIn1));
		assertEquals(string, DataBundles.getStringValue(portIn1));
	}

}

