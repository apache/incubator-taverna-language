/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;

/**
 * @author alanrw
 *
 */
public class ScuflReader implements WorkflowBundleReader {
	
	private Scufl2Tools scufl2Tools = new Scufl2Tools();

	private ScuflParser parser;
	
	public static final String APPLICATION_XML = "application/xml";
	public static final String TEXT_XML = "text/xml";
	private static final Set<String> SCUFL_TYPES = new HashSet(Arrays.asList(new String[] {APPLICATION_XML, TEXT_XML}));

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.io.WorkflowBundleReader#getMediaTypes()
	 */
	@Override
	public Set<String> getMediaTypes() {
		return SCUFL_TYPES;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.io.WorkflowBundleReader#readBundle(java.io.File, java.lang.String)
	 */
	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		try {
			WorkflowBundle bundle = getParser().parseScufl(bundleFile);
			scufl2Tools.setParents(bundle);
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse SCUFL " + bundleFile, e);
		}	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.io.WorkflowBundleReader#readBundle(java.io.InputStream, java.lang.String)
	 */
	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		try {
			WorkflowBundle bundle = getParser().parseScufl(inputStream);
			scufl2Tools.setParents(bundle);
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse SCUFL", e);
		}
	}
	
	public void setParser(ScuflParser parser) {
		this.parser = parser;
	}
	
	public ScuflParser getParser() throws JAXBException {
		if (parser == null) {
			parser = new ScuflParser();
		}
		return parser;
	}

}
