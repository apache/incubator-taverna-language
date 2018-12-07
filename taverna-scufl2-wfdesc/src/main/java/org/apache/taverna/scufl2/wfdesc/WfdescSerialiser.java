package org.apache.taverna.scufl2.wfdesc;

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

import static org.apache.taverna.scufl2.api.common.Scufl2Tools.NESTED_WORKFLOW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.OWL;
import org.apache.taverna.ro.vocabs.prov;
import org.apache.taverna.ro.vocabs.roterms;
import org.apache.taverna.ro.vocabs.wf4ever;
import org.apache.taverna.ro.vocabs.wfdesc;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.api.port.InputPort;
import org.apache.taverna.scufl2.api.port.OutputPort;
import org.apache.taverna.scufl2.api.port.WorkflowPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;

public class WfdescSerialiser {

	private static Logger logger = Logger.getLogger(WfdescSerialiser.class.getCanonicalName());

	public static URI REST = URI.create("http://ns.taverna.org.uk/2010/activity/rest");
	public static URI WSDL = URI.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static URI SECURITY = WSDL.resolve("wsdl/security");
	public static URI OPERATION = WSDL.resolve("wsdl/operation");
	public static URI BEANSHELL = URI.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	public static URI RSHELL = URI.create("http://ns.taverna.org.uk/2010/activity/rshell");
	public static URI TOOL = URI.create("http://ns.taverna.org.uk/2010/activity/tool");

	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private URITools uriTools = new URITools();

	public Scufl2Tools getScufl2Tools() {
		return scufl2Tools;
	}

	public URITools getUriTools() {
		return uriTools;
	}

	private String uriForBean(WorkflowBean bean) {
		return uriTools.uriForBean(bean).toASCIIString();
	}

	protected OntModel save(final WorkflowBundle bundle) {
		final OntModel model = ModelFactory.createOntologyModel();
		bundle.accept(new VisitorWithPath() {
			Scufl2Tools scufl2Tools = new Scufl2Tools();

			public boolean visit() {
				WorkflowBean node = getCurrentNode();
				// System.out.println(node);
				if (node instanceof WorkflowBundle) {
					return true;
				}
				// @SuppressWarnings("rawtypes")

				if (node instanceof org.apache.taverna.scufl2.api.core.Workflow) {
					entityForBean(node, wfdesc.Workflow);
				} else if (node instanceof Processor) {
					Processor processor = (Processor) node;
					Individual process = entityForBean(processor, wfdesc.Process);
					Individual wf = entityForBean(processor.getParent(), wfdesc.Workflow);
					wf.addProperty(wfdesc.hasSubProcess, process);
				} else if (node instanceof InputPort) {
					WorkflowBean parent = ((Child) node).getParent();
					Individual input = entityForBean(node, wfdesc.Input);
					Individual process = entityForBean(parent, wfdesc.Process);
					process.addProperty(wfdesc.hasInput, input);

				} else if (node instanceof OutputPort) {
					WorkflowBean parent = ((Child) node).getParent();
					Individual output = entityForBean(node, wfdesc.Output);
					Individual process = entityForBean(parent, wfdesc.Process);
					process.addProperty(wfdesc.hasOutput, output);
				} else if (node instanceof DataLink) {
					WorkflowBean parent = ((Child) node).getParent();
					DataLink link = (DataLink) node;
					Individual dl = entityForBean(link, wfdesc.DataLink);

					Individual source = entityForBean(link.getReceivesFrom(), wfdesc.Output);
					dl.addProperty(wfdesc.hasSource, source);

					Individual sink = entityForBean(link.getSendsTo(), wfdesc.Input);
					dl.addProperty(wfdesc.hasSink, sink);
					Individual wf = entityForBean(parent, wfdesc.Workflow);
					wf.addProperty(wfdesc.hasDataLink, dl);
				} else if (node instanceof Profile) {
					// So that we can get at the ProcessorBinding - buy only if
					// it is the main Profile
					return node == bundle.getMainProfile();
				} else if (node instanceof ProcessorBinding) {
					ProcessorBinding b = (ProcessorBinding) node;
					Activity a = b.getBoundActivity();
					Processor boundProcessor = b.getBoundProcessor();
					Individual process = entityForBean(boundProcessor, wfdesc.Process);

					// Note: We don't describe the activity and processor
					// binding in wfdesc. Instead we
					// assign additional types and attributes to the parent
					// processor

					try {
						URI type = a.getType();
						Configuration c = scufl2Tools.configurationFor(a, b.getParent());
						JsonNode json = c.getJson();
						if (type.equals(BEANSHELL)) {
							process.addRDFType(wf4ever.BeanshellScript);
							String s = json.get("script").asText();
							process.addProperty(wf4ever.script, s);
							JsonNode localDep = json.get("localDependency");
							if (localDep != null && localDep.isArray()) {
								for (int i = 0; i < localDep.size(); i++) {
									String depStr = localDep.get(i).asText();
									// FIXME: Better class for dependency?
									Individual dep = model.createIndividual(OWL.Thing);
									dep.addLabel(depStr, null);
									dep.addComment("JAR dependency", "en");
									process.addProperty(roterms.requiresSoftware, dep);
									// Somehow this gets the whole thing to fall
									// out of the graph!
									// QName depQ = new
									// QName("http://google.com/", ""+
									// UUID.randomUUID());
									// sesameManager.rename(dep, depQ);

								}
							}
						}
						if (type.equals(RSHELL)) {
							process.addRDFType(wf4ever.RScript);
							String s = json.get("script").asText();
							process.addProperty(wf4ever.script, s);
						}
						if (type.equals(WSDL)) {
							process.addRDFType(wf4ever.SOAPService);
							JsonNode operation = json.get("operation");
							URI wsdl = URI.create(operation.get("wsdl").asText());
							process.addProperty(wf4ever.wsdlURI, wsdl.toASCIIString());
							process.addProperty(wf4ever.wsdlOperationName, operation.get("name").asText());
							process.addProperty(wf4ever.rootURI, wsdl.resolve("/").toASCIIString());
						}
						if (type.equals(REST)) {
							process.addRDFType(wf4ever.RESTService);
							// System.out.println(json);
							JsonNode request = json.get("request");
							String absoluteURITemplate = request.get("absoluteURITemplate").asText();
							String uriTemplate = absoluteURITemplate.replace("{", "");
							uriTemplate = uriTemplate.replace("}", "");
							// TODO: Detect {}
							try {
								URI root = new URI(uriTemplate).resolve("/");
								process.addProperty(wf4ever.rootURI, root.toASCIIString());
							} catch (URISyntaxException e) {
								logger.warning("Potentially invalid URI template: " + absoluteURITemplate);
								// Uncomment to temporarily break
								// TestInvalidURITemplate:
								// rest.getWfRootURI().add(URI.create("http://example.com/FRED"));
							}
						}
						if (type.equals(TOOL)) {
							process.addRDFType(wf4ever.CommandLineTool);
							JsonNode desc = json.get("toolDescription");
							// System.out.println(json);
							JsonNode command = desc.get("command");
							if (command != null) {
								process.addProperty(wf4ever.command, command.asText());
							}
						}
						if (type.equals(NESTED_WORKFLOW)) {
							Workflow nestedWf = scufl2Tools.nestedWorkflowForProcessor(boundProcessor, b.getParent());
							// The parent process is a specialization of the
							// nested workflow
							// (because the nested workflow could exist as
							// several processors)
							specializationOf(boundProcessor, nestedWf);
							process.addRDFType(wfdesc.Workflow);

							// Just like the Processor specializes the nested
							// workflow, the
							// ProcessorPorts specialize the WorkflowPort
							for (ProcessorPortBinding portBinding : b.getInputPortBindings()) {
								// Map from activity port (not in wfdesc) to
								// WorkflowPort
								WorkflowPort wfPort = nestedWf.getInputPorts()
										.getByName(portBinding.getBoundActivityPort().getName());
								if (wfPort == null) {
									continue;
								}
								specializationOf(portBinding.getBoundProcessorPort(), wfPort);
							}
							for (ProcessorPortBinding portBinding : b.getOutputPortBindings()) {
								WorkflowPort wfPort = nestedWf.getOutputPorts()
										.getByName(portBinding.getBoundActivityPort().getName());
								if (wfPort == null) {
									continue;
								}
								specializationOf(portBinding.getBoundProcessorPort(), wfPort);
							}
						}
					} catch (IndexOutOfBoundsException ex) {
					}
					return false;
				} else {
					// System.out.println("--NO!");
					return false;
				}
				for (Annotation ann : scufl2Tools.annotationsFor(node, bundle)) {
					String annotationBody = ann.getBody().toASCIIString();
					String baseURI = bundle.getGlobalBaseURI().resolve(ann.getBody()).toASCIIString();
					InputStream annotationStream;
					try {
						annotationStream = bundle.getResources().getResourceAsInputStream(annotationBody);

						try {
							// FIXME: Don't just assume Lang.TURTLE
							RDFDataMgr.read(model, annotationStream, baseURI, Lang.TURTLE);
						} catch (RiotException e) {
							logger.log(Level.WARNING, "Can't parse RDF Turtle from " + annotationBody, e);
						} finally {
							annotationStream.close();
						}
					} catch (IOException e) {
						logger.log(Level.WARNING, "Can't read " + annotationBody, e);
					}
				}
				if (node instanceof Named) {
					Named named = (Named) node;
					entityForBean(node, OWL.Thing).addLabel(named.getName(), null);
				}
				return true;
			}

			private void specializationOf(WorkflowBean special, WorkflowBean general) {
				Individual specialEnt = entityForBean(special, prov.Entity);
				Individual generalEnt = entityForBean(general, prov.Entity);
				specialEnt.addProperty(prov.specializationOf, generalEnt);
			}

			private Individual entityForBean(WorkflowBean bean, Resource thing) {
				return model.createIndividual(uriForBean(bean), thing);
			}

			// @Override
			// public boolean visitEnter(WorkflowBean node) {
			// if (node instanceof Processor
			// || node instanceof org.apache.taverna.scufl2.api.core.Workflow
			// || node instanceof Port || node instanceof DataLink) {
			// visit(node);
			// return true;
			// }
			// // The other node types (e.g. dispatch stack, configuration) are
			// // not (directly) represented in wfdesc
			//// System.out.println("Skipping " + node);
			// return false;
			// };
		});
		return model;
	}

	public void save(WorkflowBundle wfBundle, OutputStream output) throws WriterException {
		OntModel model;

		final URI baseURI;
		if (wfBundle.getMainWorkflow() != null) {
			Workflow mainWorkflow = wfBundle.getMainWorkflow();
			baseURI = uriTools.uriForBean(mainWorkflow);
			model = save(wfBundle);
		} else {
			throw new WriterException("wfdesc format requires a main workflow");
		}

		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
		model.setNsPrefix("wfdesc", "http://purl.org/wf4ever/wfdesc#");
		model.setNsPrefix("wf4ever", "http://purl.org/wf4ever/wf4ever#");
		model.setNsPrefix("roterms", "http://purl.org/wf4ever/roterms#");
		model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		model.setNsPrefix("comp", "http://purl.org/DP/components#");
		model.setNsPrefix("dep", "http://scape.keep.pt/vocab/dependencies#");
		model.setNsPrefix("biocat", "http://biocatalogue.org/attribute/");

		model.setNsPrefix("", "#");

		try {
			model.write(output, Lang.TURTLE.getName(), baseURI.toString());
		} catch (RiotException e) {
			throw new WriterException("Can't write to output", e);
		}

	}

	public void setScufl2Tools(Scufl2Tools scufl2Tools) {
		this.scufl2Tools = scufl2Tools;
	}

	public void setUriTools(URITools uriTools) {
		this.uriTools = uriTools;
	}

}
