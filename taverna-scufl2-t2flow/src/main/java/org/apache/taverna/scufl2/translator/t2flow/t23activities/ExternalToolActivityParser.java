package org.apache.taverna.scufl2.translator.t2flow.t23activities;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import static org.apache.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.Entry;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ExternalToolConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.Group;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ScriptInputStatic;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ScriptInputUser;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ScriptOutput;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.UsecaseConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.UsecaseDescription;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExternalToolActivityParser extends AbstractActivityParser {
	@SuppressWarnings("unused")
	private static final String STDOUT = "STDOUT";
	@SuppressWarnings("unused")
	private static final String STDIN = "STDIN";
	@SuppressWarnings("unused")
	private static final String STDERR = "STDERR";
	private static final String EXTERNALTOOLACTIVITY_XSD = "/org/apache/taverna/scufl2/translator/t2flow/xsd/externaltoolactivity.xsd";
	public static final URI CNT = URI.create("http://www.w3.org/2011/content#");
	public static final URI CHARSET = URI
			.create("http://www.iana.org/assignments/character-sets#");
	// or http://www.iana.org/assignments/charset-reg/ ?
	private static final URI usecaseActivityRavenUri = ravenURI
			.resolve("net.sf.taverna.t2.activities/usecase-activity/");
	private static final String usecaseActivityClass = "net.sf.taverna.t2.activities.usecase.UseCaseActivity";
	private static final URI externalToolRavenUri = ravenURI
			.resolve("net.sf.taverna.t2.activities/external-tool-activity/");
	private static final String externalToolClass = "net.sf.taverna.t2.activities.externaltool.ExternalToolActivity";
	public static final URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/tool");
	public static final URI DC = URI.create("http://purl.org/dc/elements/1.1/");

	@SuppressWarnings("unused")
	private Map<URI, URI> mappedMechanismTypes = makeMappedMechanismTypes();

	private Map<URI, URI> makeMappedMechanismTypes() {
		Map<URI, URI> map = new HashMap<>();
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
				&& activityUriStr.endsWith(usecaseActivityClass))
			return true;
		return activityUriStr.startsWith(externalToolRavenUri.toASCIIString())
				&& activityUriStr.endsWith(externalToolClass);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL externalToolXsd = ExternalToolActivityParser.class
				.getResource(EXTERNALTOOLACTIVITY_XSD);
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

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
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
			ObjectNode json = configuration.getJsonAsObjectNode();

			configuration.setType(ACTIVITY_URI.resolve("#Config"));

			if (usecaseConfig != null) {
				if (usecaseConfig.getRepositoryUrl() != null)
					json.put("repositoryUrl", usecaseConfig.getRepositoryUrl());
				json.put("toolId", usecaseConfig.getUsecaseid());
			} else if (externalToolConfig != null) {
				if (externalToolConfig.getRepositoryUrl() != null)
					json.put("repositoryUrl",
							externalToolConfig.getRepositoryUrl());
				json.put("toolId", externalToolConfig.getExternaltoolid());
				if (externalToolConfig.isEdited())
					json.put("edited", externalToolConfig.isEdited());
			}

			if (externalToolConfig != null) {
				Group group = externalToolConfig.getGroup();
				if (group != null) {
					ObjectNode invocationGroup = json.objectNode();
					json.put("invocationGroup", invocationGroup);
					invocationGroup.put("name", group.getInvocationGroupName());
					invocationGroup.put("mechanismType",
							group.getMechanismType());
					invocationGroup.put("mechanismName",
							group.getMechanismName());
					invocationGroup
							.put("mechanismXML", group.getMechanismXML());
				} else {
					json.put("mechanismType",
							externalToolConfig.getMechanismType());
					json.put("mechanismName",
							externalToolConfig.getMechanismName());
					json.put("mechanismXML",
							externalToolConfig.getMechanismXML());
				}
//				URI mechanismTypeURI = ACTIVITY_URI.resolve("#"
//						+ uriTools.validFilename(mechanismType));
//				if (mappedMechanismTypes.containsKey(mechanismTypeURI)) {
//					mechanismTypeURI = mappedMechanismTypes.get(mechanismTypeURI);
//				}
//				invocation.addPropertyReference(ACTIVITY_URI.resolve("#mechanismType"),
//						mechanismTypeURI);
//
//				invocation.addPropertyAsString(ACTIVITY_URI.resolve("#mechanismName"),
//						mechanismName);
//				invocation.addProperty(ACTIVITY_URI.resolve("#mechanismXML"), new PropertyLiteral(
//						mechanismXML, PropertyLiteral.XML_LITERAL));
                
                // TODO: Extract SSH hostname etc. from mechanismXML
//				parseMechanismXML(json);

				ObjectNode toolDescription = json.objectNode();
				parseToolDescription(toolDescription,
						externalToolConfig.getUseCaseDescription(), parserState);
				json.put("toolDescription", toolDescription);

//				configResource.addProperty(ACTIVITY_URI.resolve("#invocationGroup"),
//						parseGroup(externalToolConfig.getGroup()));

			}

			return configuration;
		} finally {
			parserState.setCurrentConfiguration(null);
		}
	}

//	private void parseMechanismXML(ObjectNode invocation) {
//	    type = invocatoin.
//	    
//		URI type = invocation.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#mechanismType"));
//		if (type.equals(ACTIVITY_URI.resolve("#local"))) {
//			Element xml = invocation.getPropertyAsLiteral(ACTIVITY_URI.resolve("#mechanismXML"))
//					.getLiteralValueAsElement();
//
//			String directory = elementByTag(xml, "directory");
//			String linkCommand = elementByTag(xml, "linkCommand");
//			String shellPrefix = elementByTag(xml, "shellPrefix");
//			PropertyResource node = new PropertyResource();
//			node.setTypeURI(ACTIVITY_URI.resolve("#LocalNode"));
//			if (directory != null) {
//				node.addPropertyAsString(ACTIVITY_URI.resolve("#directory"), directory);
//			}
//			if (linkCommand != null) {
//				node.addPropertyAsString(ACTIVITY_URI.resolve("#linkCommand"), linkCommand);
//			}
//			if (shellPrefix != null) {
//				node.addPropertyAsString(ACTIVITY_URI.resolve("#shellPrefix"), shellPrefix);
//			}
//			if (!node.getProperties().isEmpty()) {
//				// Only add if it is customized
//				invocation.addProperty(ACTIVITY_URI.resolve("#node"), node);
//			}
//
//			invocation.getProperties().remove(ACTIVITY_URI.resolve("#mechanismXML"));
//		} else if (type.equals(ACTIVITY_URI.resolve("#ssh"))) {
//			Element xml = invocation.getPropertyAsLiteral(ACTIVITY_URI.resolve("#mechanismXML"))
//					.getLiteralValueAsElement();
//			for (Element sshNode : elementIter(xml.getElementsByTagName("sshNode"))) {
//				String hostname = elementByTag(sshNode, "host");
//				String port = elementByTag(sshNode, "port");
//				String directory = elementByTag(sshNode, "directory");
//				String linkCommand = elementByTag(sshNode, "linkCommand");
//				String copyCommand = elementByTag(sshNode, "copyCommand");
//
//				PropertyResource node = invocation.addPropertyAsNewResource(
//						ACTIVITY_URI.resolve("#node"), ACTIVITY_URI.resolve("#SSHNode"));
//				node.addPropertyAsString(ACTIVITY_URI.resolve("#hostname"), hostname);
//				if (port != null) {
//					PropertyLiteral portLit = new PropertyLiteral(port, PropertyLiteral.XSD_INT);
//					node.addProperty(ACTIVITY_URI.resolve("#port"), portLit);
//				}
//				if (directory != null) {
//					node.addPropertyAsString(ACTIVITY_URI.resolve("#directory"), directory);
//				}
//				if (linkCommand != null) {
//					node.addPropertyAsString(ACTIVITY_URI.resolve("#linkCommand"), linkCommand);
//				}
//				if (copyCommand != null) {
//					node.addPropertyAsString(ACTIVITY_URI.resolve("#copyCommand"), copyCommand);
//				}
//			}
//			invocation.clearProperties(ACTIVITY_URI.resolve("#mechanismXML"));
//		} else {
//
//		}
//	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	private String elementByTag(Element el, String tagName) {
		NodeList nodeList = el.getElementsByTagName(tagName);
		if (nodeList.getLength() == 0)
			return null;
		return nodeList.item(0).getTextContent();
	}

	protected ObjectNode parseToolDescription(ObjectNode json, UsecaseDescription toolDesc,
			ParserState parserState) {
	    	    ObjectNode description = json.objectNode();
	    description.put("dc:title", toolDesc.getUsecaseid());
		if (toolDesc.getGroup() != null)
		    description.put("group", toolDesc.getGroup());
		if (toolDesc.getDescription() != null)
		      description.put("dc:description", toolDesc.getDescription());

		description.put("command", toolDesc.getCommand());

		description.put("preparingTimeoutInSeconds",
				toolDesc.getPreparingTimeoutInSeconds());
		description.put("executionTimeoutInSeconds",
				toolDesc.getExecutionTimeoutInSeconds());

		// Ignoring tags, REs, queue__preferred, queue__deny

        ArrayNode staticInputs = json.arrayNode();
        // static inputs
		for (ScriptInputStatic inputStatic : toolDesc.getStaticInputs()
				.getDeUniLuebeckInbKnowarcUsecasesScriptInputStatic()) {
		    ObjectNode input = json.objectNode();
		    staticInputs.add(input);
		    input.put("tag", inputStatic.getTag());
		    input.put("file", inputStatic.isFile());
            input.put("tempFile", inputStatic.isTempFile());
            input.put("binary", inputStatic.isBinary());
            input.put("charsetName", inputStatic.getCharsetName());
            input.put("forceCopy", inputStatic.isForceCopy());
			if (inputStatic.getUrl() != null)
	            input.put("url", inputStatic.getUrl());
			if (inputStatic.getContent() != null)
                input.put("content", inputStatic.getContent().getValue());
		}
		if (staticInputs.size() > 0)
		    json.put("staticInputs", staticInputs);
        
//		for (ScriptInputStatic inputStatic : toolDesc.getStaticInputs()
//				.getDeUniLuebeckInbKnowarcUsecasesScriptInputStatic()) {
//			String portName = inputStatic.getTag();
//			PropertyResource staticInput = generatePortDefinition(portName, inputStatic.getTag(),
//					inputStatic.getCharsetName(), true, false, inputStatic.isBinary(),
//					inputStatic.isFile(), inputStatic.isTempFile(), inputStatic.isForceCopy(),
//					false, true, parserState);
//
//			configResource.addProperty(ACTIVITY_URI.resolve("#staticInput"), staticInput);
//			if (inputStatic.getUrl() != null) {
//				staticInput.addPropertyReference(ACTIVITY_URI.resolve("#source"),
//						URI.create(inputStatic.getUrl()));
//			} else {
//				PropertyResource content = staticInput.addPropertyAsNewResource(
//						ACTIVITY_URI.resolve("#source"), CNT.resolve("#ContentAsText"));
//				content.addPropertyAsString(CNT.resolve("#chars"), inputStatic.getContent()
//						.getValue());
//				// TODO: Support bytes?
//			}
//		}

		
		
		// Inputs
		ArrayNode inputs = json.arrayNode();
        for (Entry entry : toolDesc.getInputs().getEntry()) {
            ObjectNode mapping = json.objectNode();
            mapping.put("port", entry.getString());
            ObjectNode input = json.objectNode();
            mapping.put("input", input);

            ScriptInputUser scriptInput = entry
                    .getDeUniLuebeckInbKnowarcUsecasesScriptInputUser();
            input.put("tag", scriptInput.getTag());
            input.put("file", scriptInput.isFile());
            input.put("tempFile", scriptInput.isTempFile());
            input.put("binary", scriptInput.isBinary());

            input.put("charsetName", scriptInput.getCharsetName());
            input.put("forceCopy", scriptInput.isForceCopy());
            input.put("list", scriptInput.isList());
            input.put("concatenate", scriptInput.isConcatenate());
        }
		if (inputs.size() > 0)
		    json.put("inputs", inputs);
//		for (Entry entry : toolDesc.getInputs().getEntry()) {
//			String portName = entry.getString();
//			ScriptInputUser scriptInput = entry.getDeUniLuebeckInbKnowarcUsecasesScriptInputUser();
//			PropertyResource portDef = generatePortDefinition(portName, scriptInput.getTag(),
//					scriptInput.getCharsetName(), true, scriptInput.isList(),
//					scriptInput.isBinary(), scriptInput.isFile(), scriptInput.isTempFile(),
//					scriptInput.isForceCopy(), scriptInput.isConcatenate(), false, parserState);
//			configResource.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#inputPortDefinition"),
//					portDef);
//		}
		
		// Outputs		
		ArrayNode outputs = json.arrayNode();
		for (Entry entry : toolDesc.getOutputs().getEntry()) {
			ObjectNode mapping = json.objectNode();
			mapping.put("port", entry.getString());
			ObjectNode output = json.objectNode();
			mapping.put("output", output);

			ScriptOutput scriptOutput = entry
					.getDeUniLuebeckInbKnowarcUsecasesScriptOutput();
			output.put("path", scriptOutput.getPath());
			output.put("binary", scriptOutput.isBinary());
		}
		if (outputs.size() > 0)
			json.put("outputs", outputs);
//		for (Entry entry : toolDesc.getOutputs().getEntry()) {
//			String portName = entry.getString();
//			ScriptOutput scriptOutput = entry.getDeUniLuebeckInbKnowarcUsecasesScriptOutput();
//			PropertyResource portDef = generatePortDefinition(portName, scriptOutput.getPath(),
//					null, false, false, scriptOutput.isBinary(), true, false, false, false, false,
//					parserState);
//			configResource.addProperty(
//					Scufl2Tools.PORT_DEFINITION.resolve("#outputPortDefinition"), portDef);
        // }

        json.put("includeStdIn", toolDesc.isIncludeStdIn());
        json.put("includeStdOut", toolDesc.isIncludeStdOut());
        json.put("includeStdErr", toolDesc.isIncludeStdErr());

//		if (toolDesc.isIncludeStdIn()) {
//			InputActivityPort stdin = new InputActivityPort(parserState.getCurrentActivity(), STDIN);
//			stdin.setDepth(0);
//		}
//		if (toolDesc.isIncludeStdOut()) {
//			OutputActivityPort stdout = new OutputActivityPort(parserState.getCurrentActivity(),
//					STDOUT);
//			stdout.setDepth(0);
//			stdout.setGranularDepth(0);
//		}
//		if (toolDesc.isIncludeStdErr()) {
//			OutputActivityPort stderr = new OutputActivityPort(parserState.getCurrentActivity(),
//					STDERR);
//			stderr.setDepth(0);
//			stderr.setGranularDepth(0);
//		}

		return description;
	}
//
//	private PropertyResource generatePortDefinition(String portName, String tag, String charSet,
//			boolean isInput, boolean isList, boolean isBinary, boolean isFile, boolean isTempFile,
//			boolean isForceCopy, boolean isConcatenate, boolean isStatic, ParserState parserState) {
//
//		ActivityPort actPort;
//		if (isStatic) {
//			resource.setTypeURI(ACTIVITY_URI.resolve("#StaticInput"));
//		} else {
//
//			if (isInput) {
//				resource.setTypeURI(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"));
//				actPort = new InputActivityPort(parserState.getCurrentActivity(), portName);
//				URI portUri = uriTools.relativeUriForBean(actPort,
//						parserState.getCurrentConfiguration());
//				resource.addPropertyReference(
//						Scufl2Tools.PORT_DEFINITION.resolve("#definesInputPort"), portUri);
//			} else {
//				resource.setTypeURI(Scufl2Tools.PORT_DEFINITION.resolve("#OutputPortDefinition"));
//				actPort = new OutputActivityPort(parserState.getCurrentActivity(), portName);
//				URI portUri = uriTools.relativeUriForBean(actPort,
//						parserState.getCurrentConfiguration());
//				resource.addPropertyReference(
//						Scufl2Tools.PORT_DEFINITION.resolve("#definesOutputPort"), portUri);
//			}
//
//			if (isList) {
//				actPort.setDepth(1);
//			} else {
//				actPort.setDepth(0);
//			}
//		}
//
//		URI dataType = PropertyLiteral.XSD_STRING;
//		if (isFile || isTempFile) {
//			if (isForceCopy) {
//				resource.addProperty(ACTIVITY_URI.resolve("#forceCopy"), new PropertyLiteral(true));
//			}
//
//			if (isBinary) {
//				// FIXME: Is there a good URI for raw bytes? xsd:byte is just
//				// one byte,
//				// xsd:hexBinary and xsd:base64Binary both mandate an encoding
//				dataType = Scufl2Tools.PORT_DEFINITION.resolve("#binary");
//			} else if (charSet != null) {
//				resource.addPropertyReference(ACTIVITY_URI.resolve("#charset"),
//						CHARSET.resolve("#" + uriTools.validFilename(charSet)));
//				// TODO: Check with
//				// http://www.w3.org/International/www-international.html if
//				// this URI scheme really make sense
//			} else {
//				resource.addPropertyReference(ACTIVITY_URI.resolve("#charset"),
//						CHARSET.resolve("#UTF-8"));
//			}
//		}
//		resource.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#dataType"), dataType);
//
//		resource.addPropertyAsString(ACTIVITY_URI.resolve("#substitutes"), tag);
//		URI subsitutionType;
//		if (isFile) {
//			subsitutionType = ACTIVITY_URI.resolve("#File");
//		} else if (isTempFile) {
//			subsitutionType = ACTIVITY_URI.resolve("#TempFile");
//		} else {
//			subsitutionType = ACTIVITY_URI.resolve("#Parameter");
//		}
//		resource.addPropertyReference(ACTIVITY_URI.resolve("#substitutionType"), subsitutionType);
//
//		if (isList && isConcatenate) {
//			resource.addProperty(ACTIVITY_URI.resolve("#concatenate"), new PropertyLiteral(true));
//		}
//
//		return resource;
//	}

//	protected PropertyObject parseGroup(Group group) {
//		PropertyResource propertyResource = new PropertyResource();
//		propertyResource.setTypeURI(ACTIVITY_URI.resolve("#InvocationGroup"));
//		return propertyResource;
//	}
}
