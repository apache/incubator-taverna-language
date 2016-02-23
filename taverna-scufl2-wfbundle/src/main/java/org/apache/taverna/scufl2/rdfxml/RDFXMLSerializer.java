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


import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Typed;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.rdfxml.impl.NamespacePrefixMapperJAXB_RI;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage;
import org.apache.taverna.scufl2.xml.rdf.RDF;
import org.apache.taverna.scufl2.xml.rdf.Resource;
import org.apache.taverna.scufl2.xml.rdf.Type;
import org.apache.taverna.scufl2.xml.rdfs.SeeAlso;
import org.xml.sax.SAXException;

import org.apache.taverna.scufl2.xml.Blocking;
import org.apache.taverna.scufl2.xml.Control;
import org.apache.taverna.scufl2.xml.DataLink.MergePosition;
import org.apache.taverna.scufl2.xml.DataLinkEntry;
import org.apache.taverna.scufl2.xml.GranularPortDepth;
import org.apache.taverna.scufl2.xml.IterationStrategyStack.IterationStrategies;
import org.apache.taverna.scufl2.xml.ObjectFactory;
import org.apache.taverna.scufl2.xml.PortDepth;
import org.apache.taverna.scufl2.xml.PortNode.DesiredDepth;
import org.apache.taverna.scufl2.xml.ProcessorBinding.ActivityPosition;
import org.apache.taverna.scufl2.xml.ProcessorBinding.InputPortBinding;
import org.apache.taverna.scufl2.xml.ProcessorBinding.OutputPortBinding;
import org.apache.taverna.scufl2.xml.ProductOf;
import org.apache.taverna.scufl2.xml.ProfileDocument;
import org.apache.taverna.scufl2.xml.SeeAlsoType;
import org.apache.taverna.scufl2.xml.WorkflowBundleDocument;
import org.apache.taverna.scufl2.xml.WorkflowDocument;

public class RDFXMLSerializer {
	private static final String DOT_RDF = ".rdf";
	protected static final URI OA = URI.create("http://www.w3.org/ns/oa#");
	protected static final URI PAV = URI.create("http://purl.org/pav/");
	private static boolean warnedOnce = false;
	
	public class ProfileSerialisationVisitor implements Visitor {
		private org.apache.taverna.scufl2.xml.Activity activity;
		private final ProfileDocument doc;
		private org.apache.taverna.scufl2.xml.Profile profileElem;
		private Profile profile;
		private org.apache.taverna.scufl2.xml.ProcessorBinding processorBindingElem;

		public ProfileSerialisationVisitor(ProfileDocument doc) {
			this.doc = doc;
		}

		private void activity(Activity node) {
			activity = objectFactory.createActivity();
			activity.setAbout(uri(node));
			activity.setType(type(node));
			activity.setName(node.getName());
			doc.getAny().add(activity);
		}

		private void configuration(Configuration node) {
			org.apache.taverna.scufl2.xml.Configuration configuration = objectFactory
					.createConfiguration();
			configuration.setConfigure(resource(uri(node.getConfigures())));
			configuration.setName(node.getName());
			configuration.setType(type(node));
			
			URI configUri = uriTools.relativeUriForBean(node, profile);
            String jsonPath = configUri.toString().replaceFirst("/$", ".json");

			URI profilePath = uriTools.relativeUriForBean(profile, profile.getParent());
			
			String bundlePath = profilePath + jsonPath;
			
			UCFPackage bundle = profile.getParent().getResources();
			try {
                bundle.addResource(node.getJsonAsString(), bundlePath, "application/json");
            } catch (IOException e) {
                logger.log(WARNING, "Can't save JSON to " + bundlePath, e);
            }
			configuration.setAbout(configUri.toString());
			
			SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
			seeAlso.setResource(jsonPath);
			configuration.setSeeAlso(seeAlso);
            
			// TODO: No way in API to mark non-activated configurations
			profileElem.getActivateConfiguration().add(resource(uri(node)));
			doc.getAny().add(configuration);
		}

		private GranularPortDepth granularPortDepth(Integer integer) {
			if (integer == null)
				return null;
			GranularPortDepth p = objectFactory.createGranularPortDepth();
			p.setValue(integer);
			p.setDatatype(p.getDatatype());
			return p;
		}

		private void inputActivityPort(InputActivityPort node) {
			org.apache.taverna.scufl2.xml.InputActivityPort inputActivityPort = objectFactory
					.createInputActivityPort();
			inputActivityPort.setAbout(uri(node));
			inputActivityPort.setName(node.getName());
			inputActivityPort.setPortDepth(portDepth(node.getDepth()));

			org.apache.taverna.scufl2.xml.Activity.InputActivityPort wrapper = objectFactory
					.createActivityInputActivityPort();
			wrapper.setInputActivityPort(inputActivityPort);
			activity.getInputActivityPort().add(wrapper);
		}

		private void outputActivityPort(OutputActivityPort node) {
			org.apache.taverna.scufl2.xml.OutputActivityPort outputActivityPort = objectFactory
					.createOutputActivityPort();
			outputActivityPort.setAbout(uri(node));
			outputActivityPort.setName(node.getName());
			outputActivityPort.setPortDepth(portDepth(node.getDepth()));
			outputActivityPort.setGranularPortDepth(granularPortDepth(node
					.getGranularDepth()));

			org.apache.taverna.scufl2.xml.Activity.OutputActivityPort wrapper = objectFactory
					.createActivityOutputActivityPort();
			wrapper.setOutputActivityPort(outputActivityPort);
			activity.getOutputActivityPort().add(wrapper);
		}

		private PortDepth portDepth(Integer integer) {
			if (integer == null)
				return null;
			PortDepth p = objectFactory.createPortDepth();
			p.setValue(integer);
			p.setDatatype(p.getDatatype());
			return p;
		}

		private void processorBinding(ProcessorBinding node) {
			processorBindingElem = objectFactory.createProcessorBinding();
			processorBindingElem.setAbout(uri(node));
			processorBindingElem.setName(node.getName());
			processorBindingElem.setBindActivity(resource(uri(node
					.getBoundActivity())));
			processorBindingElem.setBindProcessor(resource(uri(node
					.getBoundProcessor())));
			if (node.getActivityPosition() != null) {
				ActivityPosition value = new ActivityPosition();
				value.setDatatype(value.getDatatype());
				value.setValue(node.getActivityPosition());
				processorBindingElem.setActivityPosition(value);
			}

			profileElem.getProcessorBinding().add(resource(uri(node)));
			doc.getAny().add(processorBindingElem);
		}

		private void processorInputPortBinding(ProcessorInputPortBinding node) {
			org.apache.taverna.scufl2.xml.InputPortBinding inputBinding = objectFactory
					.createInputPortBinding();
			inputBinding.setAbout(uri(node));
			inputBinding.setBindInputActivityPort(resource(uri(node
					.getBoundActivityPort())));
			inputBinding.setBindInputProcessorPort(resource(uri(node
					.getBoundProcessorPort())));
			InputPortBinding b = objectFactory
					.createProcessorBindingInputPortBinding();
			b.setInputPortBinding(inputBinding);
			processorBindingElem.getInputPortBinding().add(b);
		}

		private void processorOutputPortBinding(ProcessorOutputPortBinding node) {
			org.apache.taverna.scufl2.xml.OutputPortBinding outputBinding = objectFactory
					.createOutputPortBinding();
			outputBinding.setAbout(uri(node));
			outputBinding.setBindOutputActivityPort(resource(uri(node
					.getBoundActivityPort())));
			outputBinding.setBindOutputProcessorPort(resource(uri(node
					.getBoundProcessorPort())));
			OutputPortBinding b = objectFactory
					.createProcessorBindingOutputPortBinding();
			b.setOutputPortBinding(outputBinding);
			processorBindingElem.getOutputPortBinding().add(b);
		}

		private void profile(Profile node) {
			profile = node;
			profileElem = objectFactory.createProfile();
			profileElem.setAbout(uri(node));
			profileElem.setName(node.getName());
			doc.getAny().add(profileElem);
		}

		private String uri(WorkflowBean node) {
			return uriTools.relativeUriForBean(node, profile).toASCIIString();
		}

		@Override
		public boolean visit(WorkflowBean node) {
			if (node instanceof Profile)
				profile((Profile) node);
			else if (node instanceof Activity)
				activity((Activity) node);
			else if (node instanceof InputActivityPort)
				inputActivityPort((InputActivityPort) node);
			else if (node instanceof OutputActivityPort)
				outputActivityPort((OutputActivityPort) node);
			else if (node instanceof ProcessorBinding)
				processorBinding((ProcessorBinding) node);
			else if (node instanceof ProcessorInputPortBinding)
				processorInputPortBinding((ProcessorInputPortBinding) node);
			else if (node instanceof ProcessorOutputPortBinding)
				processorOutputPortBinding((ProcessorOutputPortBinding) node);
			else if (node instanceof Configuration)
				configuration((Configuration) node);
			else
				throw new IllegalStateException("Unexpected node " + node);
			return true;
		}

		@Override
		public boolean visitEnter(WorkflowBean node) {
			return visit(node);
		}

		@Override
		public boolean visitLeave(WorkflowBean node) {
			return true;
		}
	}

	public class WorkflowSerialisationVisitor implements Visitor {
		private final org.apache.taverna.scufl2.xml.Workflow workflow;
		private org.apache.taverna.scufl2.xml.Processor proc;
		private Workflow wf;
		@SuppressWarnings("unused")
		private org.apache.taverna.scufl2.xml.DispatchStack dispatchStack;
		private org.apache.taverna.scufl2.xml.IterationStrategyStack iterationStrategyStack;
		private IterationStrategies iterationStrategies;
		private Stack<List<Object>> productStack;

		public WorkflowSerialisationVisitor(
				org.apache.taverna.scufl2.xml.Workflow workflow) {
			this.workflow = workflow;
		}

		private GranularPortDepth makeGranularPortDepth(Integer granularDepth) {
			if (granularDepth == null)
				return null;
			GranularPortDepth portDepth = objectFactory
					.createGranularPortDepth();
			portDepth.setValue(granularDepth);
			portDepth.setDatatype(portDepth.getDatatype());
			return portDepth;
		}

		private PortDepth makePortDepth(Integer depth) {
			if (depth == null)
				return null;
			PortDepth portDepth = objectFactory.createPortDepth();
			portDepth.setValue(depth);
			portDepth.setDatatype(portDepth.getDatatype());
			return portDepth;
		}

		private Resource makeResource(URI uri) {
			Resource resource = rdfObjectFactory.createResource();
			resource.setResource(uri.toASCIIString());
			return resource;
		}

		@Override
		public boolean visit(WorkflowBean node) {
			if (node instanceof Workflow) {
				wf = (Workflow) node;
				workflow.setAbout("");
				workflow.setName(wf.getName());

				if (wf.getIdentifier() != null) {
					Resource wfId = rdfObjectFactory.createResource();
					wfId.setResource(wf.getIdentifier().toASCIIString());
					workflow.setWorkflowIdentifier(wfId);
				}
			}
						
			URI uri = uriTools.relativeUriForBean(node, wf);

			if (node instanceof InputWorkflowPort) {
				InputWorkflowPort ip = (InputWorkflowPort) node;
				org.apache.taverna.scufl2.xml.Workflow.InputWorkflowPort inP = objectFactory
						.createWorkflowInputWorkflowPort();
				org.apache.taverna.scufl2.xml.InputWorkflowPort inPort = objectFactory
						.createInputWorkflowPort();
				inP.setInputWorkflowPort(inPort);
				inPort.setName(ip.getName());

				URI portURI = uriTools.relativeUriForBean(ip, ip.getParent());
				inPort.setAbout(portURI.toASCIIString());

				PortDepth portDepth = makePortDepth(ip.getDepth());
				inPort.setPortDepth(portDepth);
				workflow.getInputWorkflowPort().add(inP);
			}
			if (node instanceof OutputWorkflowPort) {
				OutputWorkflowPort op = (OutputWorkflowPort) node;
				org.apache.taverna.scufl2.xml.Workflow.OutputWorkflowPort inP = objectFactory
						.createWorkflowOutputWorkflowPort();
				org.apache.taverna.scufl2.xml.OutputWorkflowPort outPort = objectFactory
						.createOutputWorkflowPort();
				inP.setOutputWorkflowPort(outPort);
				outPort.setName(op.getName());

				URI portURI = uriTools.relativeUriForBean(op, op.getParent());
				outPort.setAbout(portURI.toASCIIString());
				workflow.getOutputWorkflowPort().add(inP);
			}
			if (node instanceof Processor) {
				Processor processor = (Processor) node;
				org.apache.taverna.scufl2.xml.Workflow.Processor wfProc = objectFactory
						.createWorkflowProcessor();
				proc = objectFactory.createProcessor();
				wfProc.setProcessor(proc);
				proc.setName(processor.getName());
				URI procUri = uriTools.relativeUriForBean(processor, wf);
				proc.setAbout(procUri.toASCIIString());
				wfProc.setProcessor(proc);
				workflow.getProcessor().add(wfProc);
			}
			if (node instanceof InputProcessorPort) {
				InputProcessorPort inPort = (InputProcessorPort) node;
				org.apache.taverna.scufl2.xml.InputProcessorPort port = objectFactory
						.createInputProcessorPort();
				port.setAbout(uri.toASCIIString());
				port.setName(inPort.getName());
				port.setPortDepth(makePortDepth(inPort.getDepth()));
				org.apache.taverna.scufl2.xml.Processor.InputProcessorPort inputProcessorPort = objectFactory
						.createProcessorInputProcessorPort();
				inputProcessorPort.setInputProcessorPort(port);
				proc.getInputProcessorPort().add(inputProcessorPort);
			}
			if (node instanceof OutputProcessorPort) {
				org.apache.taverna.scufl2.xml.OutputProcessorPort port;
				OutputProcessorPort outPort = (OutputProcessorPort) node;
				port = objectFactory.createOutputProcessorPort();
				port.setAbout(uri.toASCIIString());
				port.setName(outPort.getName());
				port.setPortDepth(makePortDepth(outPort.getDepth()));
				port.setGranularPortDepth(makeGranularPortDepth(outPort
						.getGranularDepth()));

				org.apache.taverna.scufl2.xml.Processor.OutputProcessorPort outputProcessorPort = objectFactory
						.createProcessorOutputProcessorPort();
				outputProcessorPort.setOutputProcessorPort(port);
				proc.getOutputProcessorPort().add(outputProcessorPort);
			}
			if (node instanceof IterationStrategyStack) {
				iterationStrategyStack = objectFactory
						.createIterationStrategyStack();
				iterationStrategyStack.setAbout(uri.toASCIIString());
				org.apache.taverna.scufl2.xml.Processor.IterationStrategyStack processorIterationStrategyStack = objectFactory
						.createProcessorIterationStrategyStack();
				processorIterationStrategyStack
						.setIterationStrategyStack(iterationStrategyStack);
				proc.setIterationStrategyStack(processorIterationStrategyStack);
				productStack = new Stack<List<Object>>();
			}
			if (node instanceof IterationStrategyTopNode
					&& productStack.isEmpty()) {
				iterationStrategies = objectFactory
						.createIterationStrategyStackIterationStrategies();
				iterationStrategyStack
						.setIterationStrategies(iterationStrategies);
				iterationStrategies.setParseType(iterationStrategies
						.getParseType());
				List<Object> dotProductOrCrossProduct = iterationStrategies
						.getDotProductOrCrossProduct();
				productStack.add(dotProductOrCrossProduct);
			}
			if (node instanceof CrossProduct) {
				org.apache.taverna.scufl2.xml.CrossProduct crossProduct = objectFactory
						.createCrossProduct();
				crossProduct.setAbout(uri.toASCIIString());
				productStack.peek().add(crossProduct);
				ProductOf productOf = objectFactory.createProductOf();
				productOf.setParseType(productOf.getParseType());
				crossProduct.setProductOf(productOf);
				productStack.add(crossProduct.getProductOf().getCrossProductOrDotProductOrPortNode());
			}
			if (node instanceof DotProduct) {
				org.apache.taverna.scufl2.xml.DotProduct dotProduct = objectFactory
						.createDotProduct();
				dotProduct.setAbout(uri.toASCIIString());
				productStack.peek().add(dotProduct);
				ProductOf productOf = objectFactory.createProductOf();
				productOf.setParseType(productOf.getParseType());
				dotProduct.setProductOf(productOf);
				productStack.add(dotProduct.getProductOf()
						.getCrossProductOrDotProductOrPortNode());
			}
			if (node instanceof PortNode) {
				PortNode portNode = (PortNode) node;
				InputProcessorPort inPort = portNode.getInputProcessorPort();
				URI portUri = uriTools.relativeUriForBean(inPort, wf);
				org.apache.taverna.scufl2.xml.PortNode port = objectFactory.createPortNode();
				port.setAbout(uri.toASCIIString());
				if (portNode.getDesiredDepth() != null) {
					DesiredDepth value = objectFactory.createPortNodeDesiredDepth();
					value.setDatatype(value.getDatatype());
					value.setValue(portNode.getDesiredDepth());
					port.setDesiredDepth(value);
				}
				port.setIterateOverInputPort(makeResource(portUri));
				productStack.peek().add(port);
			}
			if (node instanceof DataLink) {
				DataLink dataLink = (DataLink) node;
				org.apache.taverna.scufl2.xml.DataLink link = objectFactory
						.createDataLink();
				link.setAbout(uri.toASCIIString());
				URI fromUri = uriTools.relativeUriForBean(
						dataLink.getReceivesFrom(), wf);
				URI toUri = uriTools.relativeUriForBean(dataLink.getSendsTo(),
						wf);
				link.setReceiveFrom(makeResource(fromUri));
				link.setSendTo(makeResource(toUri));

				if (dataLink.getMergePosition() != null) {
					MergePosition value = objectFactory.createDataLinkMergePosition();
					value.setValue(dataLink
							.getMergePosition());
					value.setDatatype(value.getDatatype());
					link.setMergePosition(value);
				}

				DataLinkEntry linkEntry = objectFactory.createDataLinkEntry();
				linkEntry.setDataLink(link);
				workflow.getDatalink().add(linkEntry);
			}
			if (node instanceof BlockingControlLink) {
				BlockingControlLink controlLink = (BlockingControlLink) node;
				URI blockUri = uriTools.relativeUriForBean(
						controlLink.getBlock(), wf);
				URI untilUri = uriTools.relativeUriForBean(
						controlLink.getUntilFinished(), wf);

				Blocking blocking = objectFactory.createBlocking();
				blocking.setAbout(uri.toASCIIString());
				blocking.setBlock(makeResource(blockUri));
				blocking.setUntilFinished(makeResource(untilUri));

				Control control = objectFactory.createControl();
				control.setBlocking(blocking);
				workflow.getControl().add(control);
			}

			// TODO: Datalinks

			return true;
		}

		@Override
		public boolean visitEnter(WorkflowBean node) {
			return visit(node);
		}

		@Override
		public boolean visitLeave(WorkflowBean node) {
			if (node instanceof IterationStrategyTopNode)
				// Actually for any Cross/Dot product
				productStack.pop();
			return true;
		}
	}

	protected synchronized static JAXBContext getJAxbContextStatic()
			throws JAXBException {
		if (jaxbContextStatic == null) {
			Class<?>[] packages = { ObjectFactory.class,
					org.apache.taverna.scufl2.xml.rdf.ObjectFactory.class,
					org.apache.taverna.scufl2.xml.rdfs.ObjectFactory.class };
			jaxbContextStatic = JAXBContext.newInstance(packages);
		}
		return jaxbContextStatic;
	}

	public void annotation(final Annotation ann) {
		URI wfBundleURI = uriTools.uriForBean(wfBundle);
		URI annUri = uriTools.uriForBean(ann);
		URI bodyURI = ann.getBody();
		if (bodyURI == null || bodyURI.isAbsolute())
			// Workaround with separate file for the annotation alone
			bodyURI = annUri.resolve(uriTools.validFilename(ann.getName()) + DOT_RDF);
		URI pathUri = uriTools.relativePath(wfBundleURI, bodyURI);			
		if (ann.getBody() == null || ann.getBody().equals(wfBundleURI.resolve(pathUri)))
			// Set the relative path
			ann.setBody(pathUri);

		// TODO: Add annotation to RO manifest
		
//		// Miniature OA description for now
//		// See http://openannotation.org/spec/core/20130205/
//		final PropertyResource annProv = new PropertyResource();
//		annProv.setResourceURI(annUri);
//		annProv.setTypeURI(OA.resolve("#Annotation"));
//		
//		if (ann.getAnnotatedAt() != null) {
//			annProv.addProperty(OA.resolve("#annotedAt"),
//					new PropertyLiteral(ann.getAnnotatedAt()));
//		}
//		if (ann.getSerializedAt() != null) {
//			annProv.addProperty(OA.resolve("#serializedAt"),
//					new PropertyLiteral(ann.getSerializedAt()));
//		}
//		
//		if (ann.getAnnotatedBy() != null) {
//			annProv.addPropertyReference(OA.resolve("#annotatedBy"),
//					ann.getAnnotatedBy());
//		}
//		if (ann.getSerializedBy() != null) {
//			annProv.addPropertyReference(OA.resolve("#serializedBy"),
//					ann.getSerializedBy());
//		}
//		
//		if (ann.getBody() != null) {
//			annProv.addPropertyReference(OA.resolve("#hasBody"), ann.getBody());
//		} else if (! ann.getBodyStatements().isEmpty()){						
//			// FIXME: Hack - Our body is also the annotation!
//			annProv.addPropertyReference(OA.resolve("#hasBody"), pathUri);
//		}
//		
//		// CHECK: should this be a relative reference instead?
//		annProv.addPropertyReference(OA.resolve("#hasTarget"), 
//				uriTools.uriForBean(ann.getTarget()));					
//		// Serialize the metadata
//
//	
//		try {
//			/*
//			 * TODO: Serialize manually with nicer indentation/namespaces etc.,
//			 * as done for our other RDF/XML documents
//			 */
//			wfBundle.getResources()
//					.addResource(visitor.getDoc(), pathUri.toASCIIString(), APPLICATION_RDF_XML);
//		} catch (IOException e) {
//			logger.log(Level.WARNING, "Can't write annotation to " + pathUri, e);
//		}
		
	}

	private ObjectFactory objectFactory = new ObjectFactory();

	private org.apache.taverna.scufl2.xml.rdfs.ObjectFactory rdfsObjectFactory = new org.apache.taverna.scufl2.xml.rdfs.ObjectFactory();
	private org.apache.taverna.scufl2.xml.rdf.ObjectFactory rdfObjectFactory = new org.apache.taverna.scufl2.xml.rdf.ObjectFactory();
	private URITools uriTools = new URITools();
	private boolean usingSchema = false;

	private WorkflowBundle wfBundle;

	private JAXBContext jaxbContext;

	private Map<WorkflowBean, URI> seeAlsoUris = new HashMap<>();
	private static JAXBContext jaxbContextStatic;
	private static Logger logger = Logger.getLogger(RDFXMLSerializer.class
			.getCanonicalName());
	public RDFXMLSerializer() {
	}

	public RDFXMLSerializer(WorkflowBundle wfBundle) {
		setWfBundle(wfBundle);
	}

	public JAXBContext getJaxbContext() throws JAXBException {
		if (jaxbContext == null)
			return getJAxbContextStatic();
		return jaxbContext;
	}

	public Marshaller getMarshaller() {
		String schemaPath = "xsd/scufl2.xsd";
		Marshaller marshaller;
		try {
			marshaller = getJaxbContext().createMarshaller();

			if (isUsingSchema()) {
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(getClass().getResource(
						schemaPath));
				// FIXME: re-enable schema
				marshaller.setSchema(schema);
			}
			marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
			marshaller
					.setProperty(
							"jaxb.schemaLocation",
							"http://ns.taverna.org.uk/2010/scufl2# http://ns.taverna.org.uk/2010/scufl2/scufl2.xsd "
									+ "http://www.w3.org/1999/02/22-rdf-syntax-ns# http://ns.taverna.org.uk/2010/scufl2/rdf.xsd");
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			throw new IllegalStateException("Could not load schema "
					+ schemaPath, e);
		}
		setPrefixMapper(marshaller);
		return marshaller;
	}

	public WorkflowBundle getWfBundle() {
		return wfBundle;
	}

	public boolean isUsingSchema() {
		return usingSchema;
	}

	protected ProfileDocument makeProfile(Profile pf, URI path) {
		ProfileDocument doc = objectFactory.createProfileDocument();

		objectFactory.createProfile();
		pf.accept(new ProfileSerialisationVisitor(doc) {
		});
		return doc;
	}

	protected org.apache.taverna.scufl2.xml.Workflow makeWorkflow(
			Workflow wf, URI documentPath) {
		org.apache.taverna.scufl2.xml.Workflow workflow = objectFactory
				.createWorkflow();
		wf.accept(new WorkflowSerialisationVisitor(workflow) {
		});
		return workflow;
	}

	protected org.apache.taverna.scufl2.xml.WorkflowBundle makeWorkflowBundleElem() {
		org.apache.taverna.scufl2.xml.WorkflowBundle bundle = objectFactory
				.createWorkflowBundle();
		// FIXME: Support other URIs
		bundle.setAbout("");
		bundle.setName(wfBundle.getName());

		if (wfBundle.getGlobalBaseURI() != null) {
			Resource globalBaseURI = rdfObjectFactory.createResource();
			globalBaseURI.setResource(wfBundle.getGlobalBaseURI().toASCIIString());
			bundle.setGlobalBaseURI(globalBaseURI);
		}

		for (Workflow wf : wfBundle.getWorkflows()) {
			org.apache.taverna.scufl2.xml.WorkflowBundle.Workflow wfElem = objectFactory
					.createWorkflowBundleWorkflow();
			SeeAlsoType seeAlsoElem = objectFactory.createSeeAlsoType();
			seeAlsoElem.setAbout(uriTools.relativeUriForBean(wf, wfBundle)
					.toASCIIString());

			if (seeAlsoUris.containsKey(wf)) {
				SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
				seeAlso.setResource(seeAlsoUris.get(wf).toASCIIString());
				seeAlsoElem.setSeeAlso(seeAlso);
			} else
				logger.warning("Can't find bundle URI for workflow document "
						+ wf.getName());

			wfElem.setWorkflow(seeAlsoElem);
			bundle.getWorkflow().add(wfElem);

			if (wfBundle.getMainWorkflow() == wf) {
				Resource mainWorkflow = rdfObjectFactory.createResource();
				mainWorkflow.setResource(seeAlsoElem.getAbout());
				bundle.setMainWorkflow(mainWorkflow);
			}
		}

		for (Profile pf : wfBundle.getProfiles()) {
			org.apache.taverna.scufl2.xml.WorkflowBundle.Profile wfElem = objectFactory
					.createWorkflowBundleProfile();
			SeeAlsoType seeAlsoElem = objectFactory.createSeeAlsoType();
			seeAlsoElem.setAbout(uriTools.relativeUriForBean(pf, wfBundle)
					.toASCIIString());

			if (seeAlsoUris.containsKey(pf)) {
				SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
				seeAlso.setResource(seeAlsoUris.get(pf).toASCIIString());
				seeAlsoElem.setSeeAlso(seeAlso);
			} else
				logger.warning("Can't find bundle URI for profile document "
						+ pf.getName());

			wfElem.setProfile(seeAlsoElem);
			bundle.getProfile().add(wfElem);

			if (wfBundle.getMainProfile() == pf) {
				Resource mainProfile = rdfObjectFactory.createResource();
				mainProfile.setResource(seeAlsoElem.getAbout());
				bundle.setMainProfile(mainProfile);
			}
		}

		for (Annotation ann : wfBundle.getAnnotations())
			annotation(ann);
		
		return bundle;
	}

	public void profileDoc(OutputStream outputStream, Profile pf, URI path)
			throws JAXBException, WriterException {
		ProfileDocument doc = makeProfile(pf, path);

		URI wfUri = uriTools.relativeUriForBean(pf, wfBundle);
		doc.setBase(uriTools.relativePath(path, wfUri).toASCIIString());

		JAXBElement<RDF> element = rdfObjectFactory.createRDF(doc);
		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(pf, path);
	}

	private Resource resource(String uri) {
		Resource r = rdfObjectFactory.createResource();
		r.setResource(uri);
		return r;
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}

	protected void setPrefixMapper(Marshaller marshaller) {
		boolean setPrefixMapper = false;

		try {
			/*
			 * This only works with JAXB RI, in which case we can set the
			 * namespace prefix mapper
			 */
			Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new NamespacePrefixMapperJAXB_RI());
			/*
			 * Note: A similar mapper for the built-in java
			 * (com.sun.xml.bind.internal.namespacePrefixMapper) is no longer
			 * included here, as it will not (easily) compile with Maven.
			 */
			setPrefixMapper = true;
		} catch (Exception e) {
			logger.log(FINE, "Can't find NamespacePrefixMapper", e);
		}

		if (!setPrefixMapper && ! warnedOnce) {
			logger.info("Could not set prefix mapper (missing or incompatible JAXB) "
					+ "- will use prefixes ns0, ns1, ..");
			warnedOnce = true;
		}
	}

	public void setUsingSchema(boolean usingSchema) {
		this.usingSchema = usingSchema;
	}

	public void setWfBundle(WorkflowBundle wfBundle) {
		this.wfBundle = wfBundle;
	}

	private Type type(Typed typed) {
		if (typed.getType() == null)
			return null;
		Type t = rdfObjectFactory.createType();
		t.setResource(typed.getType().toASCIIString());
		return t;
	}

	public void workflowBundleDoc(OutputStream outputStream, URI path)
			throws JAXBException, WriterException {
		org.apache.taverna.scufl2.xml.WorkflowBundle bundle = makeWorkflowBundleElem();
		WorkflowBundleDocument doc = objectFactory
				.createWorkflowBundleDocument();
		doc.getAny().add(bundle);

		doc.setBase(path.relativize(URI.create("./")).toASCIIString());
		JAXBElement<RDF> element = rdfObjectFactory.createRDF(doc);

		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(wfBundle, path);
	}

	public void workflowDoc(OutputStream outputStream, Workflow wf, URI path)
			throws JAXBException, WriterException {
		org.apache.taverna.scufl2.xml.Workflow wfElem = makeWorkflow(wf,
				path);
		WorkflowDocument doc = objectFactory.createWorkflowDocument();
		doc.getAny().add(wfElem);

		URI wfUri = uriTools.relativeUriForBean(wf, wfBundle);
		doc.setBase(uriTools.relativePath(path, wfUri).toASCIIString());

		JAXBElement<RDF> element = rdfObjectFactory.createRDF(doc);
		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(wf, path);
	}
}
