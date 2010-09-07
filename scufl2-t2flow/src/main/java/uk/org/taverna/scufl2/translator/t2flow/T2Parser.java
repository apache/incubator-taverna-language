package uk.org.taverna.scufl2.translator.t2flow;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public interface T2Parser {

	boolean canHandlePlugin(URI pluginURI);

	URI mapT2flowActivityToURI(URI t2flowActivity);

	Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ParseException;

}
