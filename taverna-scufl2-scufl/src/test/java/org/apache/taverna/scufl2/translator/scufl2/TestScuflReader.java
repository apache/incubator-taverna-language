package org.apache.taverna.scufl2.translator.scufl2;

import static org.apache.taverna.scufl2.translator.scufl.ScuflReader.APPLICATION_VND_TAVERNA_SCUFL_XML;
import static org.junit.Assert.assertEquals;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;


public class TestScuflReader {

	private static final String WORKFLOW10_XML = "/workflow10.xml";
	WorkflowBundleIO io = new WorkflowBundleIO();

	@Test
	public void guessMediaType() throws Exception {
		byte[] firstBytes = new byte[1024];
		getClass().getResourceAsStream(WORKFLOW10_XML).read(firstBytes);
		assertEquals(APPLICATION_VND_TAVERNA_SCUFL_XML,
				io.guessMediaTypeForSignature(firstBytes));
		// Mess up the namespace
		firstBytes[70] = 32;
		assertEquals(null, io.guessMediaTypeForSignature(firstBytes));
	}

}
