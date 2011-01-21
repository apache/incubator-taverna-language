package uk.org.taverna.scufl2.translator.t2flow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;

public class T2FlowReader implements WorkflowBundleReader {
	public static final String APPLICATION_VND_TAVERNA_T2FLOW_XML = "application/vnd.taverna.t2flow+xml";
	private T2FlowParser parser;
	
	
	@Override
	public Set<String> getMediaTypes() {
		return Collections.singleton(APPLICATION_VND_TAVERNA_T2FLOW_XML);
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		try {
			return getParser().parseT2Flow(bundleFile);
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
			parser = new T2FlowParser();
			return parser.parseT2Flow(inputStream);
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

}
