package uk.org.taverna.databundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

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
	}
	
	@Test
	public void getPort() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		assertFalse(Files.exists(portIn1));
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
	
}
