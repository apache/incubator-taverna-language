package uk.org.taverna.databundle;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class TestDataBundles {
	@Test
	public void createDataBundle() throws Exception {
		Path dataBundle = DataBundles.createDataBundle();
		assertTrue(Files.isDirectory(dataBundle));
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
	
}
