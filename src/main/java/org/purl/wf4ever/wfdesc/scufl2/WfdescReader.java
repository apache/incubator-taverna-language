package org.purl.wf4ever.wfdesc.scufl2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;

public class WfdescReader implements WorkflowBundleReader {

	public static final String APPLICATION_RDF_XML = "application/rdf+xml";
	public static final String TEXT_TURTLE = "text/turtle";
	public static final String TEXT_VND_WF4EVER_WFDESC_TURTLE = "text/vnd.wf4ever.wfdesc+turtle";

	@Override
	public Set<String> getMediaTypes() {
		return Collections.emptySet();
	    //return new HashSet<String>(Arrays.asList(TEXT_VND_WF4EVER_WFDESC_TURTLE, TEXT_TURTLE, APPLICATION_RDF_XML));
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		return null;
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String guessMediaTypeForSignature(byte[] firstBytes) {
		if (firstBytes.length < 100) { 
			return null;
		}
		Charset latin1 = Charset.forName("ISO-8859-1");
		String firstChars = new String(firstBytes, latin1);
		
		String mimeType = null;
		
		if (firstChars.contains("@prefix") || firstChars.contains("@base")) {
			mimeType = TEXT_TURTLE;
			if (firstChars.contains("http://purl.org/wf4ever/wfdesc")) {
				mimeType = TEXT_VND_WF4EVER_WFDESC_TURTLE;
			}
		}
		if (firstChars.contains("<") && firstChars.contains(">") && firstChars.contains("xmlns") && firstChars.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#")) {
			mimeType = APPLICATION_RDF_XML;
		}
		return mimeType;
	}

}
