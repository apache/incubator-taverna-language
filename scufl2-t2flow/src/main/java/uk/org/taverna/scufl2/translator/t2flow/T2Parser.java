package uk.org.taverna.scufl2.translator.t2flow;

import java.net.URI;

public interface T2Parser {

	boolean canHandlePlugin(URI pluginURI);

	URI mapT2flowActivityToURI(URI t2flowActivity);

}
