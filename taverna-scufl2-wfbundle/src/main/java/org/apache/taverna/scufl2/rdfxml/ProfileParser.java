package org.apache.taverna.scufl2.rdfxml;
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


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.taverna.scufl2.xml.Configuration;
import org.apache.taverna.scufl2.xml.ProcessorBinding.InputPortBinding;
import org.apache.taverna.scufl2.xml.ProcessorBinding.OutputPortBinding;
import org.apache.taverna.scufl2.xml.Profile;
import org.apache.taverna.scufl2.xml.ProfileDocument;

public class ProfileParser extends AbstractParser {
    private static Logger logger = Logger.getLogger(ProfileParser.class
            .getCanonicalName());
    
	public ProfileParser() {
		super();
	}

	public ProfileParser(ThreadLocal<ParserState> parserState) {
		super(parserState);
	}

	@SuppressWarnings("unused")
	private Element getChildElement(Element element) {
		for (Node node : nodeIterable(element.getChildNodes()))
			if (node instanceof Element)
				return (Element) node;
		return null;
	}

	private Iterable<Node> nodeIterable(final NodeList childNodes) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {
					int position = 0;

					@Override
					public boolean hasNext() {
						return childNodes.getLength() > position;
					}

					@Override
					public Node next() {
						return childNodes.item(position++);
					}

					@Override
					public void remove() {
						Node node = childNodes.item(position);
						node.getParentNode().removeChild(node);
					}
				};
			}
		};
	}

	protected void parseActivity(
			org.apache.taverna.scufl2.xml.Activity original) {
		Activity activity = new Activity();

		getParserState().push(activity);
		try {
			mapBean(original.getAbout(), activity);
			if (original.getName() != null)
				activity.setName(original.getName());
			activity.setParent(getParserState().getCurrent(
					org.apache.taverna.scufl2.api.profiles.Profile.class));
			if (original.getType() != null)
				activity.setType(resolve(original.getType().getResource()));
			for (org.apache.taverna.scufl2.xml.Activity.InputActivityPort inputActivityPort : original
					.getInputActivityPort())
				parseInputActivityPort(inputActivityPort.getInputActivityPort());
			for (org.apache.taverna.scufl2.xml.Activity.OutputActivityPort outputActivityPort : original
					.getOutputActivityPort())
				parseOutputActivityPort(outputActivityPort
						.getOutputActivityPort());
		} finally {
			getParserState().pop();
		}
	}

   private static final URI INTERNAL_DISPATCH_PREFIX = URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/");

	protected void parseConfiguration(Configuration original)
			throws ReaderException {
		org.apache.taverna.scufl2.api.configurations.Configuration config = new org.apache.taverna.scufl2.api.configurations.Configuration();

		boolean ignoreConfig = false;
		
		if (original.getType() != null) {
			URI type = resolve(original.getType().getResource());
			if (! INTERNAL_DISPATCH_PREFIX.relativize(type).isAbsolute()) {
                logger.fine("Ignoring unsupported Dispatch stack configuration (SCUFL2-130)");
                logger.finest(original.getAbout());
                ignoreConfig = true;
			}
            config.setType(type);
		}

		if (original.getName() != null)
			config.setName(original.getName());

		if (!ignoreConfig) {
			mapBean(original.getAbout(), config);

			if (original.getConfigure() != null) {
				Configurable configurable = resolveBeanUri(original
						.getConfigure().getResource(), Configurable.class);
				config.setConfigures(configurable);
			}
			config.setParent(getParserState().getCurrent(
					org.apache.taverna.scufl2.api.profiles.Profile.class));
		}

		getParserState().push(config);

		if (original.getSeeAlso() != null) {
			String about = original.getSeeAlso().getResource();
    		if (about != null) {
    		    URI resource = resolve(about);
    		    URI bundleBase = parserState .get().getLocation();
    		    URI path = uriTools.relativePath(bundleBase, resource);    		    
    		    if (ignoreConfig) {
    		        logger.finest("Deleting " + path + " (SCUFL2-130)");
    		        parserState.get().getUcfPackage().removeResource(path.getRawPath());
    		    } else {
        		    try {
        		        // TODO: Should the path in the UCF Package be %-escaped or not?
        		        // See TestRDFXMLWriter.awkwardFilenames
                        config.setJson(parserState.get().getUcfPackage().getResourceAsString(path.getRawPath()));
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "Could not parse JSON configuration " + path, e);
        		    } catch (IOException e) {
                        logger.log(Level.WARNING, "Could not load JSON configuration " + path, e);
                    }
    		    }
    		}
		}
		
		for (Object o : original.getAny()) {
		    // Legacy SCUFL2 <= 0.11.0  PropertyResource configuration
		    // Just ignoring it for now :(
		    // 
		    // TODO: Parse and represent as JSON-LD?
		    logger.warning("Ignoring unsupported PropertyResource (from wfbundle 0.2.0 or older) for " + config + " " + o);
		}
		
		getParserState().pop();
	}

	protected void parseInputActivityPort(
			org.apache.taverna.scufl2.xml.InputActivityPort original) {
		InputActivityPort port = new InputActivityPort();
		mapBean(original.getAbout(), port);
		port.setParent(getParserState().getCurrent(Activity.class));

		port.setName(original.getName());
		if (original.getPortDepth() != null)
			port.setDepth(original.getPortDepth().getValue());
	}

	protected void parseInputPortBinding(
			org.apache.taverna.scufl2.xml.InputPortBinding original)
			throws ReaderException {
		ProcessorInputPortBinding binding = new ProcessorInputPortBinding();
		mapBean(original.getAbout(), binding);

		binding.setBoundActivityPort(resolveBeanUri(original
				.getBindInputActivityPort().getResource(),
				InputActivityPort.class));
		binding.setBoundProcessorPort(resolveBeanUri(original
				.getBindInputProcessorPort().getResource(),
				InputProcessorPort.class));
		binding.setParent(getParserState().getCurrent(ProcessorBinding.class));
	}

	protected void parseOutputActivityPort(
			org.apache.taverna.scufl2.xml.OutputActivityPort original) {
		OutputActivityPort port = new OutputActivityPort();
		mapBean(original.getAbout(), port);
		port.setParent(getParserState().getCurrent(Activity.class));

		port.setName(original.getName());
		if (original.getPortDepth() != null)
			port.setDepth(original.getPortDepth().getValue());
		if (original.getGranularPortDepth() != null)
			port.setGranularDepth(original.getGranularPortDepth().getValue());
	}

	protected void parseOutputPortBinding(
			org.apache.taverna.scufl2.xml.OutputPortBinding original)
			throws ReaderException {
		ProcessorOutputPortBinding binding = new ProcessorOutputPortBinding();
		mapBean(original.getAbout(), binding);

		binding.setBoundActivityPort(resolveBeanUri(original
				.getBindOutputActivityPort().getResource(),
				OutputActivityPort.class));
		binding.setBoundProcessorPort(resolveBeanUri(original
				.getBindOutputProcessorPort().getResource(),
				OutputProcessorPort.class));
		binding.setParent(getParserState().getCurrent(ProcessorBinding.class));
	}

	protected void parseProcessorBinding(
			org.apache.taverna.scufl2.xml.ProcessorBinding original)
			throws ReaderException {
		org.apache.taverna.scufl2.api.profiles.ProcessorBinding binding = new org.apache.taverna.scufl2.api.profiles.ProcessorBinding();
		binding.setParent(getParserState().getCurrent(
				org.apache.taverna.scufl2.api.profiles.Profile.class));
		mapBean(original.getAbout(), binding);
		getParserState().push(binding);

		if (original.getName() != null)
			binding.setName(original.getName());
		if (original.getActivityPosition() != null)
			binding.setActivityPosition(original.getActivityPosition()
					.getValue());

		URI processorUri = resolve(original.getBindProcessor().getResource());
		URI activityUri = resolve(original.getBindActivity().getResource());

		binding.setBoundProcessor((Processor) resolveBeanUri(processorUri));
		binding.setBoundActivity((Activity) resolveBeanUri(activityUri));

		for (InputPortBinding inputPortBinding : original.getInputPortBinding())
			parseInputPortBinding(inputPortBinding.getInputPortBinding());
		for (OutputPortBinding outputPortBinding : original
				.getOutputPortBinding())
			parseOutputPortBinding(outputPortBinding.getOutputPortBinding());

		getParserState().pop();
	}

	protected void parseProfile(Profile original, URI profileUri) {
		org.apache.taverna.scufl2.api.profiles.Profile p = new org.apache.taverna.scufl2.api.profiles.Profile();
		p.setParent(getParserState().getCurrent(WorkflowBundle.class));

		getParserState().push(p);

		if (original.getAbout() != null) {
			URI about = getParserState().getCurrentBase().resolve(
					original.getAbout());
			mapBean(about, p);
		} else
			mapBean(profileUri, p);

		if (original.getName() != null)
			p.setName(original.getName());
		// Note - we'll pop() in profileSecond() instead
	}

	protected void parseProfileSecond(Profile profileElem) {
		// TODO: Parse activates config etc.
		getParserState().pop();
	}

	protected void readProfile(URI profileUri, URI source)
			throws ReaderException, IOException {
		if (source.isAbsolute())
			throw new ReaderException("Can't read external profile source "
					+ source);
		InputStream bundleStream = getParserState().getUcfPackage()
				.getResourceAsInputStream(source.getRawPath());
		if (bundleStream == null)
		    throw new ReaderException("Can't find profile " + source.getPath());
		readProfile(profileUri, source, bundleStream);
	}

	@SuppressWarnings("unchecked")
	protected void readProfile(URI profileUri, URI source,
			InputStream bundleStream) throws ReaderException, IOException {
		JAXBElement<ProfileDocument> elem;
		try {
			elem = (JAXBElement<ProfileDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse profile document " + source,
					e);
		}

		URI base = getParserState().getLocation().resolve(source);
		if (elem.getValue().getBase() != null)
			base = base.resolve(elem.getValue().getBase());

		getParserState().setCurrentBase(base);

		org.apache.taverna.scufl2.xml.Profile profileElem = null;
		for (Object any : elem.getValue().getAny())
			if (any instanceof org.apache.taverna.scufl2.xml.Profile) {
				if (profileElem != null)
					throw new ReaderException("More than one <Profile> found");
				profileElem = (org.apache.taverna.scufl2.xml.Profile) any;
				parseProfile(profileElem, profileUri);
			} else if (any instanceof org.apache.taverna.scufl2.xml.Activity) {
				if (profileElem == null)
					throw new ReaderException("No <Profile> found");
				parseActivity((org.apache.taverna.scufl2.xml.Activity) any);
			} else if (any instanceof org.apache.taverna.scufl2.xml.ProcessorBinding) {
				if (profileElem == null)
					throw new ReaderException("No <Profile> found");
				parseProcessorBinding((org.apache.taverna.scufl2.xml.ProcessorBinding) any);
			} else if (any instanceof org.apache.taverna.scufl2.xml.Configuration) {
				if (profileElem == null)
					throw new ReaderException("No <Profile> found");
				parseConfiguration((org.apache.taverna.scufl2.xml.Configuration) any);
			}
		parseProfileSecond(profileElem);
	}
}
