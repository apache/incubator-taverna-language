package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import static org.junit.Assert.assertEquals;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.ExternalToolActivityParser.ACTIVITY_URI;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.ExternalToolActivityParser.CHARSET;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Entry;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ExternalToolConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Group;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ScriptInputStatic;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ScriptInputUser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ScriptOutput;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.UsecaseConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.UsecaseDescription;

public class ExternalToolActivityParser extends AbstractActivityParser {

	private static final String STDOUT = "STDOUT";

	private static final String STDIN = "STDIN";

	private static final String STDERR = "STDERR";

	private static final String EXTERNALTOOLACTIVITY_XSD = "../xsd/externaltoolactivity.xsd";

	public static URI CNT = URI.create("http://www.w3.org/2011/content#");

	
	public static URI CHARSET = URI.create("http://www.iana.org/assignments/character-sets#");
	// or http://www.iana.org/assignments/charset-reg/ ? 

	private static URI usecaseActivityRavenUri = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/usecase-activity/");

	private static String usecaseActivityClass = "net.sf.taverna.t2.activities.usecase.UseCaseActivity";

	private static URI externalToolRavenUri = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/external-tool-activity/");

	private static String externalToolClass = "net.sf.taverna.t2.activities.externaltool.ExternalToolActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/tool");
	
	public static URI DC = URI
			.create("http://purl.org/dc/elements/1.1/");

	private Map<URI,URI> mappedMechanismTypes = makeMappedMechanismTypes();

	private Map<URI, URI> makeMappedMechanismTypes() {
		Map<URI, URI> map = new HashMap<URI, URI>();
		map.put(ACTIVITY_URI.resolve("#789663B8-DA91-428A-9F7D-B3F3DA185FD4"), 
				ACTIVITY_URI.resolve("#local"));
		
		map.put(ACTIVITY_URI.resolve("#D0A4CDEB-DD10-4A8E-A49C-8871003083D8"), 
				ACTIVITY_URI.resolve("#ssh"));
		
		return map;
	}

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (activityUriStr.startsWith(usecaseActivityRavenUri.toASCIIString())
				&& activityUriStr.endsWith(usecaseActivityClass)) {
			return true;
		}
		return activityUriStr.startsWith(externalToolRavenUri.toASCIIString())
				&& activityUriStr.endsWith(externalToolClass);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL externalToolXsd = getClass().getResource(EXTERNALTOOLACTIVITY_XSD);
		try {
			return Arrays.asList(externalToolXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find external tool schema " + externalToolXsd);
		}
	}
	
	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}
	
	private static URITools uriTools = new URITools();

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {
		
		ExternalToolConfig externalToolConfig = null;
		UsecaseConfig usecaseConfig = null;
		
		try { 
			externalToolConfig = unmarshallConfig(t2FlowParser,
					configBean, "xstream", ExternalToolConfig.class);
		} catch (ReaderException ex) {
			usecaseConfig = unmarshallConfig(t2FlowParser,
					configBean, "xstream", UsecaseConfig.class);
		}
		
		
		Configuration configuration = new Configuration();		
		configuration.setParent(getParserState().getCurrentProfile());
		getParserState().setCurrentConfiguration(configuration);
		try { 
		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));
		
		if (usecaseConfig != null && usecaseConfig.getRepositoryUrl() != null) {
			URI repositoryUri = URI.create(usecaseConfig.getRepositoryUrl());
			URI usecase = repositoryUri.resolve("#" + uriTools.validFilename(usecaseConfig.getUsecaseid()));
			configResource.addPropertyReference(ACTIVITY_URI.resolve("#toolId"), usecase);
		} else if (externalToolConfig  != null && externalToolConfig.getRepositoryUrl() != null) {
			URI repositoryUri = URI.create(externalToolConfig.getRepositoryUrl());
			URI usecase = repositoryUri.resolve("#" + uriTools.validFilename(externalToolConfig.getExternaltoolid()));
			configResource.addPropertyReference(ACTIVITY_URI.resolve("#toolId"), usecase);
		}
		
		if (externalToolConfig  != null) {
			configResource.addProperty(ACTIVITY_URI.resolve("#edited"), 
					new PropertyLiteral(externalToolConfig.isEdited()));
		
			
			if (externalToolConfig.getGroup() != null) {
				configResource.addPropertyAsNewResource(ACTIVITY_URI.resolve("#invocationGroup"), ACTIVITY_URI.resolve("#InvocationGroup"));
				// TODO: Invocation groups
			} else {				
				URI mechanismType = ACTIVITY_URI.resolve("#" + uriTools.validFilename(externalToolConfig.getMechanismType()));
				if (mappedMechanismTypes .containsKey(mechanismType)) {
					mechanismType = mappedMechanismTypes.get(mechanismType);
				}
				configResource.addPropertyReference(ACTIVITY_URI.resolve("#mechanismType"), 
						mechanismType);
				
				configResource.addPropertyAsString(ACTIVITY_URI.resolve("#mechanismName"), 
						externalToolConfig.getMechanismName());
				configResource.addProperty(ACTIVITY_URI.resolve("#mechanismXML"),
						new PropertyLiteral(externalToolConfig.getMechanismXML(), PropertyLiteral.XML_LITERAL));
			}

			configResource.addProperty(ACTIVITY_URI.resolve("#toolDescription"), 
					parseToolDescription(externalToolConfig.getUseCaseDescription()));

			configResource.addProperty(ACTIVITY_URI.resolve("#invocationGroup"), 
					parseGroup(externalToolConfig.getGroup()));
			
			
		}
		
		return configuration;
		} finally {
			getParserState().setCurrentConfiguration(null);
		}
	}

	protected PropertyObject parseToolDescription(
			UsecaseDescription toolDesc) {
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.setTypeURI(ACTIVITY_URI.resolve("#ToolDescription"));
		
		propertyResource.addPropertyAsString(DC.resolve("title"), 
				toolDesc.getUsecaseid());
		
		if (toolDesc.getGroup() != null) {
			propertyResource.addPropertyAsString(ACTIVITY_URI.resolve("#category"), 
				toolDesc.getGroup());
		}
		
		if (toolDesc.getDescription() != null) {
			propertyResource.addPropertyAsString(DC.resolve("description"), 
				toolDesc.getDescription());
		}
		
		propertyResource.addPropertyAsString(ACTIVITY_URI.resolve("#command"), 
				toolDesc.getCommand());
		propertyResource.addProperty(ACTIVITY_URI.resolve("#preparingTimeoutInSeconds"), 
				new PropertyLiteral(toolDesc.getPreparingTimeoutInSeconds()));
		propertyResource.addProperty(ACTIVITY_URI.resolve("#executionTimeoutInSeconds"), 
				new PropertyLiteral(toolDesc.getExecutionTimeoutInSeconds()));
		
		
		// Ignoring tags, REs, queue__preferred, queue__deny

		PropertyResource configResource = getParserState().getCurrentConfiguration().getPropertyResource();
		
		// static inputs
		for (ScriptInputStatic inputStatic : toolDesc.getStaticInputs().getDeUniLuebeckInbKnowarcUsecasesScriptInputStatic()) {
			String portName = inputStatic.getTag();
			PropertyResource staticInput = generatePortDefinition(portName,
					inputStatic.getTag(), inputStatic.getCharsetName(), true, false,
					inputStatic.isBinary(), inputStatic.isFile(),
					inputStatic.isTempFile(), inputStatic.isForceCopy(),
					false, true);
			
			configResource.addProperty(ACTIVITY_URI.resolve("#staticInput"), staticInput);
			if (inputStatic.getUrl() != null) {
				staticInput.addPropertyReference(ACTIVITY_URI.resolve("#source"), URI.create(inputStatic.getUrl()));
			} else {
				PropertyResource content = staticInput.addPropertyAsNewResource(ACTIVITY_URI.resolve("#source"), 
						CNT.resolve("#ContentAsText")); 
				content.addPropertyAsString(CNT.resolve("#chars"), 
						inputStatic.getContent().getValue());			
				// TODO: Support bytes?
			}
		}
		
		// Inputs
		for (Entry entry : toolDesc.getInputs().getEntry()) {
			String portName = entry.getString();
			ScriptInputUser scriptInput = entry.getDeUniLuebeckInbKnowarcUsecasesScriptInputUser();
			PropertyResource portDef = generatePortDefinition(portName,
					scriptInput.getTag(), scriptInput.getCharsetName(), true, scriptInput.isList(),
					scriptInput.isBinary(), scriptInput.isFile(),
					scriptInput.isTempFile(), scriptInput.isForceCopy(),
					scriptInput.isConcatenate(), false);
			configResource.addProperty(PORT_DEFINITION.resolve("#inputPortDefinition"), portDef);
		}
		// Outputs
		for (Entry entry : toolDesc.getOutputs().getEntry()) {
			String portName = entry.getString();
			ScriptOutput scriptOutput = entry.getDeUniLuebeckInbKnowarcUsecasesScriptOutput();
			PropertyResource portDef = generatePortDefinition(portName,
					scriptOutput.getPath(), null, true, false,
					scriptOutput.isBinary(), true, false, false, false, false);
			configResource.addProperty(PORT_DEFINITION.resolve("#outputPortDefinition"), portDef);
		}
				
		propertyResource.addProperty(ACTIVITY_URI.resolve("#includeStdIn"), 
				new PropertyLiteral(toolDesc.isIncludeStdIn()));		
		propertyResource.addProperty(ACTIVITY_URI.resolve("#includeStdOut"), 
				new PropertyLiteral(toolDesc.isIncludeStdOut()));
		propertyResource.addProperty(ACTIVITY_URI.resolve("#includeStdErr"), 
				new PropertyLiteral(toolDesc.isIncludeStdErr()));

		if (toolDesc.isIncludeStdIn()) {
			InputActivityPort stdin = new InputActivityPort(getParserState().getCurrentActivity(), STDIN);			
			stdin.setDepth(0);
		}
		if (toolDesc.isIncludeStdOut()) {
			OutputActivityPort stdout = new OutputActivityPort(getParserState().getCurrentActivity(), STDOUT);			
			stdout.setDepth(0);
		}
		if (toolDesc.isIncludeStdErr()) {
			OutputActivityPort stderr = new OutputActivityPort(getParserState().getCurrentActivity(), STDERR);			
			stderr.setDepth(0);
		}
		
		
		return propertyResource;
	}

	private PropertyResource generatePortDefinition(String portName,
			String tag, String charSet, boolean isInput, boolean isList,
			boolean isBinary, boolean isFile, boolean isTempFile,
			boolean isForceCopy, boolean isConcatenate, boolean isStatic) {
		PropertyResource resource = new PropertyResource();
		
		ActivityPort actPort;
		if (isStatic) {
			resource.setTypeURI(ACTIVITY_URI.resolve("#StaticInput"));		
		} else {
			
			if (isInput) {
				resource.setTypeURI(PORT_DEFINITION.resolve("#InputPortDefinition"));
				actPort = new InputActivityPort(getParserState().getCurrentActivity(), portName);
				URI portUri = uriTools.relativeUriForBean(actPort, getParserState().getCurrentConfiguration());		
				resource.addPropertyReference(PORT_DEFINITION.resolve("#definesInputPort"), portUri);
			} else {
				resource.setTypeURI(PORT_DEFINITION.resolve("#OutputPortDefinition"));
				actPort = new OutputActivityPort(getParserState().getCurrentActivity(), portName);
				URI portUri = uriTools.relativeUriForBean(actPort, getParserState().getCurrentConfiguration());		
				resource.addPropertyReference(PORT_DEFINITION.resolve("#definesOutputPort"), portUri);
			}
				
			if (isList) {
				actPort.setDepth(1);
			} else {			
				actPort.setDepth(0);
			}
		}
		
		URI dataType = PropertyLiteral.XSD_STRING;
		if (isBinary) {
			// FIXME: Is there a good URI for raw bytes? xsd:byte is just one byte, 
			// xsd:hexBinary and xsd:base64Binary both mandate an encoding
			dataType = ACTIVITY_URI.resolve("#binary");
		} else if (charSet != null) {
			resource.addPropertyReference(ACTIVITY_URI.resolve("#charset"), 
					CHARSET.resolve("#" + uriTools.validFilename(charSet)));
			// TODO: Check with http://www.w3.org/International/www-international.html if
			// this URI scheme really make sense
		} else {
			resource.addPropertyReference(ACTIVITY_URI.resolve("#charset"), 
					CHARSET.resolve("#UTF-8"));
		}				
		resource.addPropertyReference(PORT_DEFINITION.resolve("#dataType"), dataType);
			

		resource.addPropertyAsString(ACTIVITY_URI.resolve("#substitutes"), tag);
		URI subsitutionType;
		if (isFile) {
			subsitutionType = ACTIVITY_URI.resolve("#File");
		} else if (isTempFile) {
			subsitutionType = ACTIVITY_URI.resolve("#TempFile");
 		} else {
 			subsitutionType = ACTIVITY_URI.resolve("#Parameter");
		}
		resource.addPropertyReference(ACTIVITY_URI.resolve("#substitutionType"), subsitutionType);
				
		if ((isFile || isTempFile) && isForceCopy) {
			resource.addProperty(ACTIVITY_URI.resolve("#forceCopy"), new PropertyLiteral(true));
		}
		if (isList && isConcatenate) {
			resource.addProperty(ACTIVITY_URI.resolve("#concatenate"), new PropertyLiteral(true));
		}		
		
		return resource;
	}

	protected PropertyObject parseGroup(Group group) {
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.setTypeURI(ACTIVITY_URI.resolve("#InvocationGroup"));
		return propertyResource;
	}
}