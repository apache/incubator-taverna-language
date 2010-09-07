package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public class WSDLActivityParser implements T2Parser {
	private static URI wsdlActivityRavenURI =
			T2FlowParser.ravenURI.resolve("net.sf.taverna.t2.activities/wsdl-activity/");

	private static String wsdlActivityClassName = "net.sf.taverna.t2.activities.wsdl.WSDLActivity";

	private static String inputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLInputSplitterActivity";
	private static String outputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLOutputSplitterActivity";

	public static URI wsdlScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static URI inputScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static URI outputScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(wsdlActivityRavenURI.toASCIIString())) {
			return false;
		}
		if (activityUriStr.endsWith(wsdlActivityClassName)
				|| activityUriStr.endsWith(inputSplitterClassName)
				|| activityUriStr.endsWith(outputSplitterClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		String activityUriStr = t2flowActivity.toASCIIString();
		if (activityUriStr.endsWith(inputSplitterClassName)) {
			return inputScufl2Uri;
		} else if (activityUriStr.endsWith(outputSplitterClassName)) {
			return outputScufl2Uri;
		} else {
			return wsdlScufl2Uri;
		}
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) {
		// TODO Auto-generated method stub
		return null;
	}

}
