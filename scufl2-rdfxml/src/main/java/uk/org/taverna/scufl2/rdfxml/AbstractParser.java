package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;

public class AbstractParser {
	protected JAXBContext jaxbContext;
	protected final ThreadLocal<ParserState> parserState;
	protected Scufl2Tools scufl2Tools = new Scufl2Tools();
	protected Unmarshaller unmarshaller;
	protected URITools uriTools = new URITools();

	/**
	 * A static class for the thread-local parser state.
	 */
	private static class ThreadLocalParserState extends ThreadLocal<ParserState> {
		@Override
		protected ParserState initialValue() {
			return new ParserState();
		};
	}

	public AbstractParser() {
		this(new ThreadLocalParserState());
	}

	public AbstractParser(ThreadLocal<ParserState> parserState) {
		this.parserState = parserState;
		createMarshaller();
	}

	protected void clearParserState() {
		parserState.remove();
	}

	private void createMarshaller() {
		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException(
					"Can't create JAXBContext/unmarshaller", e);
		}
	}

	protected ParserState getParserState() {
		return parserState.get();
	}

	protected void mapBean(String about, WorkflowBean bean) {
		if (about == null)
			return;
		URI aboutUri = getParserState().getCurrentBase().resolve(about);
		mapBean(aboutUri, bean);
	}

	protected void mapBean(URI uri, WorkflowBean bean) {
		getParserState().getUriToBean().put(uri, bean);
		getParserState().getBeanToUri().put(bean, uri);
	}

	protected URI resolve(String uri) {
		return getParserState().getCurrentBase().resolve(uri);
	}

	protected <T extends WorkflowBean> T resolveBeanUri(String resource,
			Class<T> beanType) throws ReaderException {
		URI uri = resolve(resource);
		WorkflowBean bean = resolveBeanUri(uri);
		if (bean == null)
			throw new ReaderException("Can't find workflow bean for resource "
					+ resource);
		if (!beanType.isInstance(bean))
			throw new ReaderException("Wrong type for workflow bean "
					+ resource + ", expected " + beanType.getSimpleName()
					+ " but was " + bean.getClass().getSimpleName());
		return beanType.cast(bean);
	}

	protected WorkflowBean resolveBeanUri(URI uri) {
		WorkflowBean workflowBean = getParserState().getUriToBean().get(uri);
		if (workflowBean != null)
			return workflowBean;
		uri = getParserState().getCurrentBase().resolve(uri);
		return uriTools.resolveUri(uri,
				getParserState().getCurrent(WorkflowBundle.class));
	}
}
