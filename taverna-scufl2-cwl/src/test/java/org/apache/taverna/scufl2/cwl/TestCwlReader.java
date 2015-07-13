package org.apache.taverna.scufl2.cwl;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

public class TestCwlReader {
	@Test
	public void parseEx1() throws Exception {
		URL revsort = getClass().getResource("/ex1/revsort.cwl");
		CwlReader reader = new CwlReader();
		reader.readBundle(revsort);
		
	}
}
