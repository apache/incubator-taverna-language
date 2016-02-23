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
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.api.port.SenderPort;

import org.apache.taverna.scufl2.xml.Blocking;
import org.apache.taverna.scufl2.xml.CrossProduct;
import org.apache.taverna.scufl2.xml.DataLink;
import org.apache.taverna.scufl2.xml.DispatchStack;
import org.apache.taverna.scufl2.xml.DotProduct;
import org.apache.taverna.scufl2.xml.IterationStrategyStack;
import org.apache.taverna.scufl2.xml.PortNode;
import org.apache.taverna.scufl2.xml.Processor.InputProcessorPort;
import org.apache.taverna.scufl2.xml.Processor.OutputProcessorPort;
import org.apache.taverna.scufl2.xml.ProductOf;
import org.apache.taverna.scufl2.xml.WorkflowDocument;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class WorkflowParser extends AbstractParser {
	private static Logger logger = Logger.getLogger(WorkflowParser.class
			.getCanonicalName());
	@SuppressWarnings("unused")
	private static final JsonNodeFactory JSON_NODE_FACTORY = JsonNodeFactory.instance;

	public WorkflowParser() {
	}

	public WorkflowParser(ThreadLocal<ParserState> parserState) {
		super(parserState);
	}

	protected void parseControlLink(Blocking original) {
		URI blockUri = getParserState().getCurrentBase().resolve(
				original.getBlock().getResource());
		URI untilFinishedUri = getParserState().getCurrentBase().resolve(
				original.getUntilFinished().getResource());
		WorkflowBean block = resolveBeanUri(blockUri);
		WorkflowBean untilFinished = resolveBeanUri(untilFinishedUri);

		BlockingControlLink blocking = new BlockingControlLink();
		blocking.setBlock((org.apache.taverna.scufl2.api.core.Processor) block);
		blocking.setUntilFinished((org.apache.taverna.scufl2.api.core.Processor) untilFinished);

		blocking.setParent(getParserState().getCurrent(Workflow.class));
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				blocking);
	}

	protected void parseCrossDotOrPortNodeList(List<Object> nodeList)
			throws ReaderException {
		for (Object node : nodeList)
			if (node instanceof DotProduct)
				parseDotProduct((DotProduct) node);
			else if (node instanceof CrossProduct)
				parseCrossProduct((CrossProduct) node);
			else if (node instanceof PortNode)
				parsePortNode((PortNode) node);
			else
				throw new ReaderException("Unexpected node " + node);
	}

	protected void parseCrossProduct(CrossProduct original)
			throws ReaderException {
		org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct cross = new org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct();
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				cross);
		cross.setParent(getParserState().getCurrent(
				IterationStrategyParent.class));
		getParserState().push(cross);
		try {
			parseProductOf(original.getProductOf());
		} finally {
			getParserState().pop();
		}
	}

	protected void parseDataLink(DataLink original) {
		URI fromUri = getParserState().getCurrentBase().resolve(
				original.getReceiveFrom().getResource());
		URI toUri = getParserState().getCurrentBase().resolve(
				original.getSendTo().getResource());
		WorkflowBean from = resolveBeanUri(fromUri);
		WorkflowBean to = resolveBeanUri(toUri);

		org.apache.taverna.scufl2.api.core.DataLink link = new org.apache.taverna.scufl2.api.core.DataLink();
		link.setReceivesFrom((SenderPort) from);
		link.setSendsTo((ReceiverPort) to);
		if (original.getMergePosition() != null)
			link.setMergePosition(original.getMergePosition().getValue());
		link.setParent(getParserState().getCurrent(Workflow.class));
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				link);
	}

	protected void parseDispatchStack(DispatchStack original) {
        logger.fine("Ignoring Dispatch stack: not supported (SCUFL2-130)");
        return;

//        // FIXME: Legacy code - support parsing old dispatch stack configurations
//		Processor processor = getParserState().getCurrent(
//				org.apache.taverna.scufl2.api.core.Processor.class);
//        ObjectNode config = JSON_NODE_FACTORY.objectNode();
//        getParserState().getDispatchConfigs().put(processor, config);        
//		if (original.getDispatchStackLayers() != null) {
//			for (DispatchStackLayer dispatchStackLayer : original
//					.getDispatchStackLayers().getDispatchStackLayer()) {
//				parseDispatchStackLayer(dispatchStackLayer);
//			}
//		}
	}

//	protected void parseDispatchStackLayer(DispatchStackLayer original) {
//	    Processor processor = getParserState().getCurrent(Processor.class);
//	    URI type = getParserState().getCurrentBase().resolve(
//				original.getType().getResource());
//	    URI config = getParserState().getCurrentBase().resolve(original.getAbout());
//	    // TODO: SCUFL2-130
//	    // Add Legacy code for wfbundle 0.3.0 to
//	    // support parsing old dispatch stack configurations
//	    // 
//	    // The difficult bit is that the layers themselves has moved to 
//	    // to be a Configuration on a Processor - but we are here within
//	    // parsing of the Workflow. In 0.3.0 each layer is then configured
//	    // separately. So we need to pass over somehow the current stack 
//	    // to the ParserState so that it can be picked up in ProfileParser
//	    // and added to each of the profiles -- or at least where the
//	    // stack layers have been configured.
//	    // 
//	    // Here's an idea on how it can work. Here we should push each layer to a
//	    // List<Pair<URI,URI>> that we can keep in the ParserState. 
//	    // Then, within ProfileParser, we can pick them up and
//      // recreate what the Processor config would look like for
//      // the default configs - and then delete those dangling configs
//	}

	protected void parseDotProduct(DotProduct original) throws ReaderException {
		org.apache.taverna.scufl2.api.iterationstrategy.DotProduct dot = new org.apache.taverna.scufl2.api.iterationstrategy.DotProduct();
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				dot);
		dot.setParent(getParserState()
				.getCurrent(IterationStrategyParent.class));

		getParserState().push(dot);
		try {
			parseProductOf(original.getProductOf());
		} finally {
			getParserState().pop();
		}
	}

	protected void parseInputWorkflowPort(
			org.apache.taverna.scufl2.xml.InputWorkflowPort original) {
		org.apache.taverna.scufl2.api.port.InputWorkflowPort port = new org.apache.taverna.scufl2.api.port.InputWorkflowPort();
		port.setName(original.getName());
		if (original.getPortDepth() != null)
			port.setDepth(original.getPortDepth().getValue());
		port.setParent(getParserState().getCurrent(Workflow.class));
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	protected void parseIterationStrategyStack(IterationStrategyStack original)
			throws ReaderException {
		org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack iterationStrategyStack = new org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack();
		iterationStrategyStack.setParent(getParserState().getCurrent(
				Processor.class));

		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				iterationStrategyStack);
		if (original.getIterationStrategies() != null) {
			getParserState().push(iterationStrategyStack);
			try {
				parseCrossDotOrPortNodeList(original.getIterationStrategies()
						.getDotProductOrCrossProduct());
			} finally {
				getParserState().pop();
			}
		}
	}

	protected void parseOutputWorkflowPort(
			org.apache.taverna.scufl2.xml.OutputWorkflowPort original) {
		org.apache.taverna.scufl2.api.port.OutputWorkflowPort port = new org.apache.taverna.scufl2.api.port.OutputWorkflowPort();
		port.setName(original.getName());
		port.setParent(getParserState().getCurrent(Workflow.class));
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	protected void parsePortNode(PortNode original) {
		org.apache.taverna.scufl2.api.iterationstrategy.PortNode node = new org.apache.taverna.scufl2.api.iterationstrategy.PortNode();
		node.setParent(getParserState().getCurrent(
				IterationStrategyParent.class));
		if (original.getDesiredDepth() != null)
			node.setDesiredDepth(original.getDesiredDepth().getValue());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				node);
		URI inputPortUri = getParserState().getCurrentBase().resolve(
				original.getIterateOverInputPort().getResource());
		org.apache.taverna.scufl2.api.port.InputProcessorPort inputPort = (org.apache.taverna.scufl2.api.port.InputProcessorPort) resolveBeanUri(inputPortUri);
		node.setInputProcessorPort(inputPort);
	}

	protected void parseProcessor(
			org.apache.taverna.scufl2.xml.Processor processor)
			throws ReaderException {
		org.apache.taverna.scufl2.api.core.Processor p = new org.apache.taverna.scufl2.api.core.Processor();
		getParserState().push(p);
		try {
			p.setParent(getParserState().getCurrent(Workflow.class));
			mapBean(getParserState().getCurrentBase().resolve(
					processor.getAbout()), p);
			if (processor.getName() != null)
				p.setName(processor.getName());
			for (InputProcessorPort inputProcessorPort : processor
					.getInputProcessorPort())
				processorInputProcessorPort(inputProcessorPort
						.getInputProcessorPort());
			for (OutputProcessorPort outputProcessorPort : processor
					.getOutputProcessorPort())
				processorOutputProcessorPort(outputProcessorPort
						.getOutputProcessorPort());
			if (processor.getDispatchStack() != null)
			    // Legacy wfbundle
				parseDispatchStack(processor.getDispatchStack()
						.getDispatchStack());
			if (processor.getIterationStrategyStack() != null)
				parseIterationStrategyStack(processor
						.getIterationStrategyStack()
						.getIterationStrategyStack());
		} finally {
			getParserState().pop();
		}
	}

	protected void parseProductOf(ProductOf productOf) throws ReaderException {
		if (productOf == null)
			return;
		parseCrossDotOrPortNodeList(productOf
				.getCrossProductOrDotProductOrPortNode());
	}

	protected void parseWorkflow(
			org.apache.taverna.scufl2.xml.Workflow workflow, URI wfUri)
			throws ReaderException {
		Workflow wf = new Workflow();
		wf.setParent(getParserState().getCurrent(WorkflowBundle.class));

		if (workflow.getAbout() != null)
			mapBean(getParserState().getCurrentBase().resolve(
					workflow.getAbout()), wf);
			// TODO: Compare resolved URI with desired wfUri
		else
			mapBean(wfUri, wf);

		getParserState().push(wf);
		try {
			if (workflow.getName() != null)
				wf.setName(workflow.getName());
			if (workflow.getWorkflowIdentifier() != null
					&& workflow.getWorkflowIdentifier().getResource() != null)
				wf.setIdentifier(getParserState().getCurrentBase().resolve(
						workflow.getWorkflowIdentifier().getResource()));

			for (org.apache.taverna.scufl2.xml.Workflow.InputWorkflowPort inputWorkflowPort : workflow
					.getInputWorkflowPort())
				parseInputWorkflowPort(inputWorkflowPort.getInputWorkflowPort());
			for (org.apache.taverna.scufl2.xml.Workflow.OutputWorkflowPort outputWorkflowPort : workflow
					.getOutputWorkflowPort())
				parseOutputWorkflowPort(outputWorkflowPort
						.getOutputWorkflowPort());
			for (org.apache.taverna.scufl2.xml.Workflow.Processor processor : workflow
					.getProcessor())
				parseProcessor(processor.getProcessor());
			for (org.apache.taverna.scufl2.xml.DataLinkEntry dataLinkEntry : workflow
					.getDatalink())
				parseDataLink(dataLinkEntry.getDataLink());
			for (org.apache.taverna.scufl2.xml.Control c : workflow
					.getControl())
				parseControlLink(c.getBlocking());
		} finally {
			getParserState().pop();
		}
	}

	protected void processorInputProcessorPort(
			org.apache.taverna.scufl2.xml.InputProcessorPort original) {
		org.apache.taverna.scufl2.api.port.InputProcessorPort port = new org.apache.taverna.scufl2.api.port.InputProcessorPort();
		port.setName(original.getName());
		if (original.getPortDepth() != null)
			port.setDepth(original.getPortDepth().getValue());
		port.setParent(getParserState().getCurrent(Processor.class));
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	protected void processorOutputProcessorPort(
			org.apache.taverna.scufl2.xml.OutputProcessorPort original) {
		org.apache.taverna.scufl2.api.port.OutputProcessorPort port = new org.apache.taverna.scufl2.api.port.OutputProcessorPort();
		port.setName(original.getName());
		if (original.getPortDepth() != null)
			port.setDepth(original.getPortDepth().getValue());
		if (original.getGranularPortDepth() != null)
			port.setGranularDepth(original.getGranularPortDepth().getValue());
		port.setParent(getParserState().getCurrent(
				org.apache.taverna.scufl2.api.core.Processor.class));
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	@SuppressWarnings("unchecked")
	protected void readWorkflow(URI wfUri, URI source) throws ReaderException,
			IOException {
		if (source.isAbsolute())
			throw new ReaderException("Can't read external workflow source "
					+ source);

		InputStream bundleStream = getParserState().getUcfPackage()
				.getResourceAsInputStream(source.getRawPath());

		JAXBElement<WorkflowDocument> elem;
		try {
			elem = (JAXBElement<WorkflowDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException(
					"Can't parse workflow document " + source, e);
		}

		URI base = getParserState().getLocation().resolve(source);
		if (elem.getValue().getBase() != null)
			base = base.resolve(elem.getValue().getBase());

		if (elem.getValue().getAny().size() != 1)
			throw new ReaderException("Expects only a <Workflow> element in "
					+ source);
		org.apache.taverna.scufl2.xml.Workflow workflow = (org.apache.taverna.scufl2.xml.Workflow) elem
				.getValue().getAny().get(0);

		getParserState().setCurrentBase(base);
		parseWorkflow(workflow, wfUri);
	}
}
