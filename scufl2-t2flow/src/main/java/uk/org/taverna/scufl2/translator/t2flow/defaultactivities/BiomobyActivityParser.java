package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.translator.t2flow.T2Parser;

public class BiomobyActivityParser implements T2Parser {

	private static URI activityRavenURI = URI
			.create("http://ns.taverna.org.uk/2010/activity/raven/net.sf.taverna.t2.activities/biomoby-activity/");

	private static String mobyObjectClassName = "net.sf.taverna.t2.activities.biomoby.BiomobyObjectActivity";

	private static String mobyServiceClassName = "net.sf.taverna.t2.activities.biomoby.BiomobyActivity";

	public static URI mobyObjectScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/biomoby/object");

	public static URI mobyServiceScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/biomoby/service");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(activityRavenURI.toASCIIString())) {
			return false;
		}
		if (activityUriStr.endsWith(mobyObjectClassName)
				|| activityUriStr.endsWith(mobyServiceClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		String activityUriStr = t2flowActivity.toASCIIString();
		if (activityUriStr.endsWith(mobyObjectClassName)) {
			return mobyObjectScufl2Uri;
		} else {
			return mobyServiceScufl2Uri;
		}
	}

}
