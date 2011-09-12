package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
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

	private static final String EXTERNALTOOLACTIVITY_XSD = "/uk/org/taverna/scufl2/translator/t2flow/xsd/externaltoolactivity.xsd";

	public static URI CNT = URI.create("http://www.w3.org/2011/content#");

	public static URI CHARSET = URI
			.create("http://www.iana.org/assignments/character-sets#");
	// or http://www.iana.org/assignments/charset-reg/ ?

	private static URI usecaseActivityRavenUri = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/usecase-activity/");

	private static String usecaseActivityClass = "net.sf.taverna.t2.activities.usecase.UseCaseActivity";

	private static URI externalToolRavenUri = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/external-tool-activity/");

	private static String externalToolClass = "net.sf.taverna.t2.activities.externaltool.ExternalToolActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/tool");

	public static URI DC = URI.create("http://purl.org/dc/elements/1.1/");

	private Map<URI, URI> mappedMechanismTypes = makeMappedMechanismTypes();

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
		URL externalToolXsd = ExternalToolActivityParser.class.getResource(EXTERNALTOOLACTIVITY_XSD);
		try {
			return Arrays.asList(externalToolXsd.toURI());
		} catch (Exception e) {
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

		ExternalToolConfig externalToolConfig = null;
		UsecaseConfig usecaseConfig = null;

		try {
			externalToolConfig = unmarshallConfig(t2FlowParser, configBean,
					"xstream", ExternalToolConfig.class);
		} catch (ReaderException ex) {
			usecaseConfig = unmarshallConfig(t2FlowParser, configBean,
					"xstream", UsecaseConfig.class);
		}

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());
		parserState.setCurrentConfiguration(configuration);
		try {
			PropertyResource configResource = configuration
					.getPropertyResource();
			configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

			if (usecaseConfig != null
					&& usecaseConfig.getRepositoryUrl() != null) {
				URI repositoryUri = URI
						.create(usecaseConfig.getRepositoryUrl());
				URI usecase = repositoryUri.resolve("#"
						+ uriTools.validFilename(usecaseConfig.getUsecaseid()));
				configResource.addPropertyReference(
						ACTIVITY_URI.resolve("#toolId"), usecase);
			} else if (externalToolConfig != null
					&& externalToolConfig.getRepositoryUrl() != null) {
				URI repositoryUri = URI.create(externalToolConfig
						.getRepositoryUrl());
				URI usecase = repositoryUri.resolve("#"
						+ uriTools.validFilename(externalToolConfig
								.getExternaltoolid()));
				configResource.addPropertyReference(
						ACTIVITY_URI.resolve("#toolId"), usecase);
				if (configResource.getProperties().containsKey(
						ACTIVITY_URI.resolve("#toolId"))) {
					configResource.addProperty(ACTIVITY_URI.resolve("#edited"),
							new PropertyLiteral(externalToolConfig.isEdited()));
				}
			} else if (externalToolConfig != null
					&& externalToolConfig.getExternaltoolid() != null) {
				URI usecase = ACTIVITY_URI.resolve("#"
						+ uriTools.validFilename(externalToolConfig
								.getExternaltoolid()));
				configResource.addPropertyReference(
						ACTIVITY_URI.resolve("#toolId"), usecase);
			}

			if (externalToolConfig != null) {

				PropertyResource invocation = configResource
						.addPropertyAsNewResource(
								ACTIVITY_URI.resolve("#invocation"),
								ACTIVITY_URI.resolve("#Invocation"));

				String mechanismType;
				String mechanismName;
				String mechanismXML;
				if (externalToolConfig.getGroup() != null) {
					mechanismType = externalToolConfig.getGroup()
							.getMechanismType();
					mechanismName = externalToolConfig.getGroup()
							.getMechanismName();
					mechanismXML = externalToolConfig.getGroup()
							.getMechanismXML();
					invocation.setTypeURI(ACTIVITY_URI
							.resolve("#InvocationGroup"));
					invocation.addPropertyAsString(DC.resolve("identifier"),
							externalToolConfig.getGroup()
									.getInvocationGroupName());
				} else {
					mechanismType = externalToolConfig.getMechanismType();
					mechanismName = externalToolConfig.getMechanismName();
					mechanismXML = externalToolConfig.getMechanismXML();
				}
				URI mechanismTypeURI = ACTIVITY_URI.resolve("#"
						+ uriTools.validFilename(mechanismType));
				if (mappedMechanismTypes.containsKey(mechanismTypeURI)) {
					mechanismTypeURI = mappedMechanismTypes
							.get(mechanismTypeURI);
				}
				invocation.addPropertyReference(
						ACTIVITY_URI.resolve("#mechanismType"),
						mechanismTypeURI);

				invocation.addPropertyAsString(
						ACTIVITY_URI.resolve("#mechanismName"), mechanismName);
				invocation.addProperty(ACTIVITY_URI.resolve("#mechanismXML"),
						new PropertyLiteral(mechanismXML,
								PropertyLiteral.XML_LITERAL));
				try {
					parseMechanismXML(invocation);
				} catch (PropertyException ex) {
					throw new ReaderException("Can't parse mechanism XML", ex);
				}

				configResource.addProperty(ACTIVITY_URI
						.resolve("#toolDescription"),
						parseToolDescription(externalToolConfig
								.getUseCaseDescription(), parserState));

				configResource.addProperty(
						ACTIVITY_URI.resolve("#invocationGroup"),
						parseGroup(externalToolConfig.getGroup()));

			}

			return configuration;
		} finally {
			parserState.setCurrentConfiguration(null);
		}
	}

	private void parseMechanismXML(PropertyResource invocation)
			throws PropertyException {
		URI type = invocation.getPropertyAsResourceURI(ACTIVITY_URI
				.resolve("#mechanismType"));
		if (type.equals(ACTIVITY_URI.resolve("#local"))) {
			Element xml = invocation.getPropertyAsLiteral(
					ACTIVITY_URI.resolve("#mechanismXML"))
					.getLiteralValueAsElement();

			String directory = elementByTag(xml, "directory");
			String linkCommand = elementByTag(xml, "linkCommand");
			String shellPrefix = elementByTag(xml, "shellPrefix");
			PropertyResource node = new PropertyResource();
			node.setTypeURI(ACTIVITY_URI.resolve("#LocalNode"));
			if (directory != null) {
				node.addPropertyAsString(ACTIVITY_URI.resolve("#directory"),
						directory);
			}
			if (linkCommand != null) {
				node.addPropertyAsString(ACTIVITY_URI.resolve("#linkCommand"),
						linkCommand);
			}
			if (shellPrefix != null) {
				node.addPropertyAsString(ACTIVITY_URI.resolve("#shellPrefix"),
						shellPrefix);
			}
			if (!node.getProperties().isEmpty()) {
				// Only add if it is customized
				invocation.addProperty(ACTIVITY_URI.resolve("#node"), node);
			}

			invocation.getProperties().remove(
					ACTIVITY_URI.resolve("#mechanismXML"));
		} else if (type.equals(ACTIVITY_URI.resolve("#ssh"))) {
			Element xml = invocation.getPropertyAsLiteral(
					ACTIVITY_URI.resolve("#mechanismXML"))
					.getLiteralValueAsElement();
			for (Element sshNode : elementIter(xml
					.getElementsByTagName("sshNode"))) {
				String hostname = elementByTag(sshNode, "host");
				String port = elementByTag(sshNode, "port");
				String directory = elementByTag(sshNode, "directory");
				String linkCommand = elementByTag(sshNode, "linkCommand");
				String copyCommand = elementByTag(sshNode, "copyCommand");

				PropertyResource node = invocation.addPropertyAsNewResource(
						ACTIVITY_URI.resolve("#node"),
						ACTIVITY_URI.resolve("#SSHNode"));
				node.addPropertyAsString(ACTIVITY_URI.resolve("#hostname"),
						hostname);
				if (port != null) {
					PropertyLiteral portLit = new PropertyLiteral(port,
							PropertyLiteral.XSD_INT);
					node.addProperty(ACTIVITY_URI.resolve("#port"), portLit);
				}
				if (directory != null) {
					node.addPropertyAsString(
							ACTIVITY_URI.resolve("#directory"), directory);
				}
				if (linkCommand != null) {
					node.addPropertyAsString(
							ACTIVITY_URI.resolve("#linkCommand"), linkCommand);
				}
				if (copyCommand != null) {
					node.addPropertyAsString(
							ACTIVITY_URI.resolve("#copyCommand"), copyCommand);
				}
			}
			invocation.getProperties().remove(
					ACTIVITY_URI.resolve("#mechanismXML"));
		} else {

		}
	}

	private Iterable<Element> elementIter(final NodeList nodeList) {
		return new Iterable<Element>() {
			@Override
			public Iterator<Element> iterator() {
				return new Iterator<Element>() {
					int position = 0;

					@Override
					public boolean hasNext() {
						return nodeList.getLength() > position;
					}

					@Override
					public Element next() {
						return (Element) nodeList.item(position++);
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

				};
			}
		};
	}

	private String elementByTag(Element el, String tagName) {
		NodeList nodeList = el.getElementsByTagName(tagName);
		if (nodeList.getLength() == 0) {
			return null;
		}
		return nodeList.item(0).getTextContent();
	}

	protected PropertyObject parseToolDescription(UsecaseDescription toolDesc, ParserState parserState) {
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.setTypeURI(ACTIVITY_URI.resolve("#ToolDescription"));

		propertyResource.addPropertyAsString(DC.resolve("title"),
				toolDesc.getUsecaseid());

		if (toolDesc.getGroup() != null) {
			propertyResource.addPropertyAsString(
					ACTIVITY_URI.resolve("#category"), toolDesc.getGroup());
		}

		if (toolDesc.getDescription() != null) {
			propertyResource.addPropertyAsString(DC.resolve("description"),
					toolDesc.getDescription());
		}

		propertyResource.addPropertyAsString(ACTIVITY_URI.resolve("#command"),
				toolDesc.getCommand());
		propertyResource.addProperty(
				ACTIVITY_URI.resolve("#preparingTimeoutInSeconds"),
				new PropertyLiteral(toolDesc.getPreparingTimeoutInSeconds()));
		propertyResource.addProperty(
				ACTIVITY_URI.resolve("#executionTimeoutInSeconds"),
				new PropertyLiteral(toolDesc.getExecutionTimeoutInSeconds()));

		// Ignoring tags, REs, queue__preferred, queue__deny

		PropertyResource configResource = parserState
				.getCurrentConfiguration().getPropertyResource();

		// static inputs
		for (ScriptInputStatic inputStatic : toolDesc.getStaticInputs()
				.getDeUniLuebeckInbKnowarcUsecasesScriptInputStatic()) {
			String portName = inputStatic.getTag();
			PropertyResource staticInput = generatePortDefinition(portName,
					inputStatic.getTag(), inputStatic.getCharsetName(), true,
					false, inputStatic.isBinary(), inputStatic.isFile(),
					inputStatic.isTempFile(), inputStatic.isForceCopy(), false,
					true, parserState);

			configResource.addProperty(ACTIVITY_URI.resolve("#staticInput"),
					staticInput);
			if (inputStatic.getUrl() != null) {
				staticInput.addPropertyReference(
						ACTIVITY_URI.resolve("#source"),
						URI.create(inputStatic.getUrl()));
			} else {
				PropertyResource content = staticInput
						.addPropertyAsNewResource(
								ACTIVITY_URI.resolve("#source"),
								CNT.resolve("#ContentAsText"));
				content.addPropertyAsString(CNT.resolve("#chars"), inputStatic
						.getContent().getValue());
				// TODO: Support bytes?
			}
		}

		// Inputs
		for (Entry entry : toolDesc.getInputs().getEntry()) {
			String portName = entry.getString();
			ScriptInputUser scriptInput = entry
					.getDeUniLuebeckInbKnowarcUsecasesScriptInputUser();
			PropertyResource portDef = generatePortDefinition(portName,
					scriptInput.getTag(), scriptInput.getCharsetName(), true,
					scriptInput.isList(), scriptInput.isBinary(),
					scriptInput.isFile(), scriptInput.isTempFile(),
					scriptInput.isForceCopy(), scriptInput.isConcatenate(),
					false, parserState);
			configResource
					.addProperty(Scufl2Tools.PORT_DEFINITION
							.resolve("#inputPortDefinition"), portDef);
		}
		// Outputs
		for (Entry entry : toolDesc.getOutputs().getEntry()) {
			String portName = entry.getString();
			ScriptOutput scriptOutput = entry
					.getDeUniLuebeckInbKnowarcUsecasesScriptOutput();
			PropertyResource portDef = generatePortDefinition(portName,
					scriptOutput.getPath(), null, false, false,
					scriptOutput.isBinary(), true, false, false, false, false, parserState);
			configResource.addProperty(Scufl2Tools.PORT_DEFINITION
					.resolve("#outputPortDefinition"), portDef);
		}

		propertyResource.addProperty(ACTIVITY_URI.resolve("#includeStdIn"),
				new PropertyLiteral(toolDesc.isIncludeStdIn()));
		propertyResource.addProperty(ACTIVITY_URI.resolve("#includeStdOut"),
				new PropertyLiteral(toolDesc.isIncludeStdOut()));
		propertyResource.addProperty(ACTIVITY_URI.resolve("#includeStdErr"),
				new PropertyLiteral(toolDesc.isIncludeStdErr()));

		if (toolDesc.isIncludeStdIn()) {
			InputActivityPort stdin = new InputActivityPort(parserState
					.getCurrentActivity(), STDIN);
			stdin.setDepth(0);
		}
		if (toolDesc.isIncludeStdOut()) {
			OutputActivityPort stdout = new OutputActivityPort(parserState
					.getCurrentActivity(), STDOUT);
			stdout.setDepth(0);
			stdout.setGranularDepth(0);
		}
		if (toolDesc.isIncludeStdErr()) {
			OutputActivityPort stderr = new OutputActivityPort(parserState
					.getCurrentActivity(), STDERR);
			stderr.setDepth(0);
			stderr.setGranularDepth(0);
		}

		return propertyResource;
	}

	private PropertyResource generatePortDefinition(String portName,
			String tag, String charSet, boolean isInput, boolean isList,
			boolean isBinary, boolean isFile, boolean isTempFile,
			boolean isForceCopy, boolean isConcatenate, boolean isStatic, ParserState parserState) {
		PropertyResource resource = new PropertyResource();

		ActivityPort actPort;
		if (isStatic) {
			resource.setTypeURI(ACTIVITY_URI.resolve("#StaticInput"));
		} else {

			if (isInput) {
				resource.setTypeURI(Scufl2Tools.PORT_DEFINITION
						.resolve("#InputPortDefinition"));
				actPort = new InputActivityPort(parserState
						.getCurrentActivity(), portName);
				URI portUri = uriTools.relativeUriForBean(actPort,
						parserState.getCurrentConfiguration());
				resource.addPropertyReference(Scufl2Tools.PORT_DEFINITION
						.resolve("#definesInputPort"), portUri);
			} else {
				resource.setTypeURI(Scufl2Tools.PORT_DEFINITION
						.resolve("#OutputPortDefinition"));
				actPort = new OutputActivityPort(parserState
						.getCurrentActivity(), portName);
				URI portUri = uriTools.relativeUriForBean(actPort,
						parserState.getCurrentConfiguration());
				resource.addPropertyReference(Scufl2Tools.PORT_DEFINITION
						.resolve("#definesOutputPort"), portUri);
			}

			if (isList) {
				actPort.setDepth(1);
			} else {
				actPort.setDepth(0);
			}
		}

		URI dataType = PropertyLiteral.XSD_STRING;
		if (isFile || isTempFile) {
			if (isForceCopy) {
				resource.addProperty(ACTIVITY_URI.resolve("#forceCopy"),
						new PropertyLiteral(true));
			}

			if (isBinary) {
				// FIXME: Is there a good URI for raw bytes? xsd:byte is just
				// one byte,
				// xsd:hexBinary and xsd:base64Binary both mandate an encoding
				dataType = Scufl2Tools.PORT_DEFINITION.resolve("#binary");
			} else if (charSet != null) {
				resource.addPropertyReference(ACTIVITY_URI.resolve("#charset"),
						CHARSET.resolve("#" + uriTools.validFilename(charSet)));
				// TODO: Check with
				// http://www.w3.org/International/www-international.html if
				// this URI scheme really make sense
			} else {
				resource.addPropertyReference(ACTIVITY_URI.resolve("#charset"),
						CHARSET.resolve("#UTF-8"));
			}
		}
		resource.addPropertyReference(
				Scufl2Tools.PORT_DEFINITION.resolve("#dataType"), dataType);

		resource.addPropertyAsString(ACTIVITY_URI.resolve("#substitutes"), tag);
		URI subsitutionType;
		if (isFile) {
			subsitutionType = ACTIVITY_URI.resolve("#File");
		} else if (isTempFile) {
			subsitutionType = ACTIVITY_URI.resolve("#TempFile");
		} else {
			subsitutionType = ACTIVITY_URI.resolve("#Parameter");
		}
		resource.addPropertyReference(
				ACTIVITY_URI.resolve("#substitutionType"), subsitutionType);

		if (isList && isConcatenate) {
			resource.addProperty(ACTIVITY_URI.resolve("#concatenate"),
					new PropertyLiteral(true));
		}

		return resource;
	}

	protected PropertyObject parseGroup(Group group) {
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.setTypeURI(ACTIVITY_URI.resolve("#InvocationGroup"));
		return propertyResource;
	}
}