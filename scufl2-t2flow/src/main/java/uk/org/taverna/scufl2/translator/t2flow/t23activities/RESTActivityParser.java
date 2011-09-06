package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import static uk.org.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.ACTIVITY_URI;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.HTTP_URI;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityInputs.Entry;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.HTTPHeaders;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RESTConfig;

public class RESTActivityParser extends AbstractActivityParser {

	private static final String ACTIVITY_XSD = "../xsd/restactivity.xsd";

	private static URI ravenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/rest-activity/");

	private static URI ravenUIURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.ui-activities/rest-activity/");

	
	private static String className = "net.sf.taverna.t2.activities.rest.RESTActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/rest");
	
	public static URI HTTP_URI = URI.create("http://www.w3.org/2011/http#");
	public static URI HTTP_HEADERS_URI = URI.create("http://www.w3.org/2011/http-headers#");
	public static URI HTTP_METHODS_URI = URI.create("http://www.w3.org/2011/http-methods#");


	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return ( activityUriStr.startsWith(ravenURI.toASCIIString()) || 
				 activityUriStr.startsWith(ravenUIURI.toASCIIString()) ) 
				&& activityUriStr.endsWith(className);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL externalToolXsd = getClass().getResource(ACTIVITY_XSD);		
		try {
			return Arrays.asList(externalToolXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find external tool schema "
					+ externalToolXsd);
		}
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	private static URITools uriTools = new URITools();

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {


		RESTConfig restConfig = unmarshallConfig(t2FlowParser, configBean,
					"xstream", RESTConfig.class);
	
		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());
		parserState.setCurrentConfiguration(configuration);
		try {
			PropertyResource configResource = configuration
					.getPropertyResource();
			configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

			PropertyResource request = configResource.addPropertyAsNewResource(ACTIVITY_URI.resolve("#request"), 
					ACTIVITY_URI.resolve("#Request"));
						
			URI method = HTTP_METHODS_URI.resolve("#" + restConfig.getHttpMethod().toUpperCase());
			request.addPropertyReference(HTTP_URI.resolve("#mthd"), method);
			
			request.addPropertyAsString(ACTIVITY_URI.resolve("#absoluteURITemplate"), restConfig.getUrlSignature());

			PropertyList headers = new PropertyList();
			request.addProperty(HTTP_URI.resolve("#headers"), headers);
			
			if (restConfig.getAcceptsHeaderValue() != null && ! restConfig.getAcceptsHeaderValue().isEmpty()) {
				PropertyResource accept = new PropertyResource();
				accept.setTypeURI(HTTP_URI.resolve("#RequestHeader"));
				accept.addPropertyAsString(HTTP_URI.resolve("#fieldName"), "Accept");
				accept.addPropertyAsString(HTTP_URI.resolve("#fieldValue"), restConfig.getAcceptsHeaderValue());
				//accept.addPropertyReference(HTTP_URI.resolve("#hdrName"), HTTP_METHODS_URI.resolve("#accept"));
				headers.add(accept);
			}
			if (hasContent(method)) {				
				if (restConfig.getContentTypeForUpdates() != null && ! restConfig.getContentTypeForUpdates().isEmpty()) {
					PropertyResource contentType = new PropertyResource();
					contentType.setTypeURI(HTTP_URI.resolve("#RequestHeader"));
					contentType.addPropertyAsString(HTTP_URI.resolve("#fieldName"), "Content-Type");
					contentType.addPropertyAsString(HTTP_URI.resolve("#fieldValue"), restConfig.getContentTypeForUpdates());
					//accept.addPropertyReference(HTTP_URI.resolve("#hdrName"), HTTP_METHODS_URI.resolve("#content-type"));
					headers.add(contentType);
				} 
				if (restConfig.isSendHTTPExpectRequestHeader()) {
					PropertyResource expect = new PropertyResource();
					expect.setTypeURI(HTTP_URI.resolve("#RequestHeader"));
					expect.addPropertyAsString(HTTP_URI.resolve("#fieldName"), "Expect");
					expect.addProperty(ACTIVITY_URI.resolve("#use100Continue"), new PropertyLiteral(true));
					//accept.addPropertyReference(HTTP_URI.resolve("#hdrName"), HTTP_METHODS_URI.resolve("#expect"));
					headers.add(expect);
				}
			}
			if (restConfig.getOtherHTTPHeaders() != null && restConfig.getOtherHTTPHeaders().getList() != null) {
				for (HTTPHeaders.List list : restConfig.getOtherHTTPHeaders().getList()) {
					String fieldName = list.getContent().get(0).getValue();
					String fieldValue = list.getContent().get(1).getValue();
					
					PropertyResource header = new PropertyResource();
					header.setTypeURI(HTTP_URI.resolve("#RequestHeader"));
					header.addPropertyAsString(HTTP_URI.resolve("#fieldName"), fieldName);
					header.addPropertyAsString(HTTP_URI.resolve("#fieldValue"), fieldValue);
					headers.add(header);
				}
			}
			if (restConfig.isShowRedirectionOutputPort()) {
				configResource.addProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort"), new PropertyLiteral(true));
			}
			if (restConfig.getEscapeParameters() != null && ! restConfig.getEscapeParameters()) {
				// Default: true
				configResource.addProperty(ACTIVITY_URI.resolve("#escapeParameters"), new PropertyLiteral(false));
			}
			
			// Ports
			
			Activity currentActivity = parserState.getCurrentActivity();
			if (restConfig.getActivityInputs() != null && restConfig.getActivityInputs().getEntry() != null) {
				for (Entry entry : restConfig.getActivityInputs().getEntry()) {
					String portName = entry.getString();
					// Ignored, URL parameters have to be strings
					//String className = entry.getJavaClass();
					
					InputActivityPort inputPort = new InputActivityPort(currentActivity, portName);
					inputPort.setDepth(0);
					
					
					PropertyResource portConfig = configResource.addPropertyAsNewResource(
							Scufl2Tools.PORT_DEFINITION.resolve("#inputPortDefinition"),
							Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"));

					URI portUri = new URITools().relativeUriForBean(inputPort, configuration);
					portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#definesInputPort"), portUri);
					portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#dataType"),
							PropertyLiteral.XSD_STRING);										
				}
			}
			if (hasContent(method)) {
				InputActivityPort inputPort = new InputActivityPort(currentActivity, IN_BODY);
				inputPort.setDepth(0);
				
				// FIXME: Is this really an #inputPortDefinition? It's not specified
				// by the user - it's a consequence of the method choice. But if we don't do 
				// this, then we'll have to specify the binary/string option elsewhere.
				PropertyResource portConfig = configResource.addPropertyAsNewResource(
						Scufl2Tools.PORT_DEFINITION.resolve("#inputPortDefinition"),
						Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"));

				URI portUri = new URITools().relativeUriForBean(inputPort, configuration);
				portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#definesInputPort"), portUri);
				
				URI dataType = PropertyLiteral.XSD_STRING;
				if (restConfig.getOutgoingDataFormat().equals("Binary")) {
					dataType = Scufl2Tools.PORT_DEFINITION.resolve("#Binary");
				}						
				portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#dataType"),
						dataType);
			}
			
			OutputActivityPort responseBody = new OutputActivityPort(currentActivity, OUT_RESPONSE_BODY);
			responseBody.setDepth(0);
			OutputActivityPort status = new OutputActivityPort(currentActivity, OUT_STATUS);
			status.setDepth(0);
			
			if (restConfig.isShowRedirectionOutputPort()) {
				OutputActivityPort redirection = new OutputActivityPort(currentActivity, OUT_REDIRECTION);
				redirection.setDepth(0);									
			}
			
			
			return configuration;
		} finally {
			parserState.setCurrentConfiguration(null);
		}
	}
	
	private static final String IN_BODY = "inputBody";
	private static final String OUT_RESPONSE_BODY = "responseBody";
	private static final String OUT_STATUS = "status";
	private static final String OUT_REDIRECTION = "redirection";

	private boolean hasContent(URI method) {
		if (! (method.resolve("#").equals(HTTP_METHODS_URI))) { 
			throw new IllegalArgumentException("Only standard HTTP methods from " +
					HTTP_METHODS_URI + " are supported");
		}
		String methodName = method.getFragment();		
		if (Arrays.asList("GET", "HEAD", "DELETE", "CONNECT").contains(methodName)) {
			return false;
		}
		// Most probably does have or could have content
		return true;
	}


}
