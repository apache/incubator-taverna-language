package uk.org.taverna.databundle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class TestDataBundles {
	@Test
	public void createDataBundle() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		assertTrue(Files.isDirectory(dataBundle));
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
	public void getInputs() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		// Second time should not fail because it already exists
		inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		assertEquals(dataBundle, inputs.getParent());
	}

	@Test
	public void closeDataBundle() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path zip = DataBundles.closeDataBundle(dataBundle);
		assertTrue(Files.isReadable(zip));
		
		checkSignature(zip);
	}
	
	
	protected void checkSignature(Path zip) throws IOException {
		String MEDIATYPE = "application/vnd.wf4ever.robundle+zip";
		// Check position 30++ according to RO Bundle specification
		// http://purl.org/wf4ever/ro-bundle#ucf
		byte[] expected = ("mimetype" + MEDIATYPE + "PK")
				.getBytes("ASCII");
		
		try (InputStream in = Files.newInputStream(zip)) {
			byte[] signature = new byte[expected.length];
			int MIME_OFFSET = 30;
			assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
			assertEquals(expected.length, in.read(signature));
			assertArrayEquals(expected, signature);
		}
	}



	@Test
	public void saveDataBundle() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		DataBundles.getInputs(dataBundle);
		Path destination = Files.createTempFile("test", ".zip");
		Files.delete(destination);
		assertFalse(Files.exists(destination));
		DataBundles.closeAndSaveDataBundle(dataBundle, destination);
		assertTrue(Files.exists(destination));
	}
	
	
	@Test
	public void hasInputs() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		assertFalse(DataBundles.hasInputs(dataBundle));
		DataBundles.getInputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasInputs(dataBundle));		
	}
	
	@Test
	public void hasOutputs() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getInputs(dataBundle); // independent
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getOutputs(dataBundle);	// create on demand	
		assertTrue(DataBundles.hasOutputs(dataBundle));		
	}
	
	@Test
	public void getOutputs() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		// Second time should not fail because it already exists
		outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		assertEquals(dataBundle, outputs.getParent());
	}
	
	@Test
	public void getPort() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		assertFalse(Files.exists(portIn1));
		assertEquals(inputs, portIn1.getParent());
	}
	
	@Test
	public void setStringValue() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		String string = "A string";
		DataBundles.setStringValue(portIn1, string);
		assertTrue(Files.exists(portIn1));
		assertEquals(string, DataBundles.getStringValue(portIn1));
	}
	
	@Test
	public void createList() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(Files.isDirectory(list));
	}
	
	@Test
	public void isList() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(DataBundles.isList(list));
	}
	
	@Test
	public void newListItem() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
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
	public void asList() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		for (int i=0; i<5; i++) {
			Path item = DataBundles.newListItem(list);
			DataBundles.setStringValue(item, "test" + i);	
		}
		List<Path> paths = DataBundles.getList(list);
		assertEquals(5, paths.size());
		assertEquals("test0", DataBundles.getStringValue(paths.get(0)));
		assertEquals("test4", DataBundles.getStringValue(paths.get(4)));
	}
	
}
