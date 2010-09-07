package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.translator.t2flow.T2Parser;

public class BeanshellActivityParser implements T2Parser {

	private static URI activityRavenURI = URI
			.create("http://ns.taverna.org.uk/2010/activity/raven/net.sf.taverna.t2.activities/beanshell-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.beanshell.BeanshellActivity";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		return scufl2Uri;
	}

}
