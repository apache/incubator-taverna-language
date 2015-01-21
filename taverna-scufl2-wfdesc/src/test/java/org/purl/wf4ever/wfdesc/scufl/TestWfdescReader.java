package org.purl.wf4ever.wfdesc.scufl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;


public class TestWfdescReader {

	WorkflowBundleIO io = new WorkflowBundleIO();
	
	@Test
	public void guessType() throws Exception {		
		assertEquals("application/vnd.taverna.t2flow+xml", 
				guessMediaTypeForResource("/helloworld.t2flow"));
		assertEquals("application/vnd.taverna.t2flow+xml", 
				guessMediaTypeForResource("/rdf-in-example-annotation.t2flow"));

		assertEquals("text/vnd.wf4ever.wfdesc+turtle", 
				guessMediaTypeForResource("/helloworld.wfdesc.ttl"));
		
		assertNull( 
				guessMediaTypeForResource("/allTypes.links.sparql.json"));
	}
	
	private String guessMediaTypeForResource(String resource) throws IOException {
		byte[] firstBytes = new byte[1024];
		InputStream s = getClass().getResourceAsStream(resource);
		assertNotNull("Could not find " + resource, s);
		try {
			s.read(firstBytes);
		} finally { 
			s.close();
		}
		return io.guessMediaTypeForSignature(firstBytes);
	}
	
}
