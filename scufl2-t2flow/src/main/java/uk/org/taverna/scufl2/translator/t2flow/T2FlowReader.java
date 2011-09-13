package uk.org.taverna.scufl2.translator.t2flow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;

public class T2FlowReader implements WorkflowBundleReader {
	public static final String APPLICATION_VND_TAVERNA_T2FLOW_XML = "application/vnd.taverna.t2flow+xml";
	private T2FlowParser parser;
	
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	
	@Override
	public Set<String> getMediaTypes() {
		return Collections.singleton(APPLICATION_VND_TAVERNA_T2FLOW_XML);
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		try {
			WorkflowBundle bundle = getParser().parseT2Flow(bundleFile);
			scufl2Tools.setParents(bundle);
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse t2flow " + bundleFile, e);
		}
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		T2FlowParser parser;
		try {
			WorkflowBundle bundle = getParser().parseT2Flow(inputStream);
			scufl2Tools.setParents(bundle);
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse t2flow", e);
		}
	}

	public void setParser(T2FlowParser parser) {
		this.parser = parser;
	}

	public T2FlowParser getParser() throws JAXBException {
		if (parser == null) {
			parser = new T2FlowParser();
		}
		return parser;
	}
	
	@Override
	public String guessMediaTypeForSignature(byte[] firstBytes) {

		if (firstBytes.length < 100) { 
			return null;
		}
		// FIXME: Does not deal with potential UTF-16 encoding
		
		// Latin 1 can deal with nasty bytes in binaries
		Charset latin1 = Charset.forName("ISO-8859-1");
		String asLatin1 = new String(firstBytes, latin1);
		if (! asLatin1.contains("workflow")) { 
			return null;
		}
		if (! asLatin1.contains("http://taverna.sf.net/2008/xml/t2flow")) { 
			return null;
		}
		if (! asLatin1.contains("dataflow")) { 
			return null;
		}
		// Good enough - XML is hard to check on so few bytes		
		return APPLICATION_VND_TAVERNA_T2FLOW_XML;
	}
	

}
