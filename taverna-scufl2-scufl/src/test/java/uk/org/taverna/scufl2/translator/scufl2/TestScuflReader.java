package uk.org.taverna.scufl2.translator.scufl2;

import static org.junit.Assert.assertEquals;
import static uk.org.taverna.scufl2.translator.scufl.ScuflReader.APPLICATION_VND_TAVERNA_SCUFL_XML;

import org.junit.Test;

import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

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
