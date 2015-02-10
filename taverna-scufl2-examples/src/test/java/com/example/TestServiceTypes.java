package com.example;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestServiceTypes {
	@Test
	public void wsdlServices() throws Exception {
		File tmp = File.createTempFile("scufl2-ebi-interproscan", ".t2flow");
		tmp.deleteOnExit();
		InputStream ebi = getClass()
				.getResourceAsStream(
						"/workflows/t2flow/ebi_interproscan_for_taverna_2_317472.t2flow");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();

		Set<String> expectedTypes = new HashSet<String>();
		expectedTypes.addAll(Arrays.asList(
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				"http://ns.taverna.org.uk/2010/activity/beanshell",
				"http://ns.taverna.org.uk/2010/activity/wsdl",
				"http://ns.taverna.org.uk/2010/activity/constant"));

		Set<String> types = new ServiceTypes().serviceTypes(new String[] { tmp
				.getAbsolutePath() });
		assertEquals(expectedTypes, types);
	}
	
	@Test
	public void wsdlServicesWfBundle() throws Exception {
		File tmp = File.createTempFile("scufl2-ebi-interproscan", ".wfbundle");
		tmp.deleteOnExit();
		InputStream ebi = getClass()
				.getResourceAsStream(
						"/workflows/wfbundle/ebi_interproscan_for_taverna_2_317472.wfbundle");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();

		Set<String> expectedTypes = new HashSet<String>();
		expectedTypes.addAll(Arrays.asList(
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				"http://ns.taverna.org.uk/2010/activity/beanshell",
				"http://ns.taverna.org.uk/2010/activity/wsdl",
				"http://ns.taverna.org.uk/2010/activity/constant"));

		Set<String> types = new ServiceTypes().serviceTypes(new String[] { tmp
				.getAbsolutePath() });
		assertEquals(expectedTypes, types);
	}
	
}
