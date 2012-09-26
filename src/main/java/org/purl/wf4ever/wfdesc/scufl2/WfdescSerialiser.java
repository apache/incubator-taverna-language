package org.purl.wf4ever.wfdesc.scufl2;

import java.io.OutputStream;
import java.net.URI;

import javax.xml.namespace.QName;

import org.openrdf.OpenRDFException;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.purl.wf4ever.wf4ever.BeanshellScript;
import org.purl.wf4ever.wf4ever.CommandLineTool;
import org.purl.wf4ever.wf4ever.RScript;
import org.purl.wf4ever.wf4ever.SOAPService;
import org.purl.wf4ever.wfdesc.Description;
import org.purl.wf4ever.wfdesc.Input;
import org.purl.wf4ever.wfdesc.Output;
import org.purl.wf4ever.wfdesc.Process;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.Visitor.VisitorAdapter;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WriterException;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.OutputPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class WfdescSerialiser {

	public static URI WSDL = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static URI SECURITY = WSDL.resolve("wsdl/security");
	public static URI OPERATION = WSDL.resolve("wsdl/operation");
	public static URI BEANSHELL = URI
	.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	public static URI RSHELL = URI
	.create("http://ns.taverna.org.uk/2010/activity/rshell");
	public static URI TOOL = URI
	.create("http://ns.taverna.org.uk/2010/activity/tool");
	
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private SesameManager sesameManager;
	private URITools uriTools = new URITools();
	private WorkflowBundle wfBundle;

	public Repository getRepository() {
		return getSesameManager().getConnection().getRepository();
	}

	public Scufl2Tools getScufl2Tools() {
		return scufl2Tools;
	}

	public SesameManager getSesameManager() {
		if (sesameManager == null) {
			ElmoModule module = new ElmoModule();
			module.addConcept(Labelled.class);
			SesameManagerFactory factory = new SesameManagerFactory(module);
			factory.setInferencingEnabled(true);
			sesameManager = factory.createElmoManager();
		}
		return sesameManager;
	}

	public URITools getUriTools() {
		return uriTools;
	}

	private QName qnameForBean(WorkflowBean bean) {
		URI uri = uriTools.uriForBean(bean);
		org.openrdf.model.URI sesameUri = getRepository().getValueFactory()
				.createURI(uri.toASCIIString());
		return new QName(sesameUri.getNamespace(), sesameUri.getLocalName());
	}

	protected void save(uk.org.taverna.scufl2.api.core.Workflow workflow, final Profile profile) {
		workflow.accept(new VisitorAdapter() {
			Scufl2Tools scufl2Tools = new Scufl2Tools();
			public boolean visit(WorkflowBean node) {
				QName parentQName = qnameForBean(((Child) node).getParent());
				QName qName = qnameForBean(node);
				if (node instanceof uk.org.taverna.scufl2.api.core.Workflow) {
					org.purl.wf4ever.wfdesc.Workflow wf = sesameManager.create(
							qName, org.purl.wf4ever.wfdesc.Workflow.class);
					// TODO: Connect nested workflows
				} else if (node instanceof Processor) {
//					System.out.println("Eye");
					Process process = sesameManager
							.create(qName, Process.class);
					sesameManager
							.designate(parentQName,
									org.purl.wf4ever.wfdesc.Workflow.class)
							.getWfHasSubProcess().add(process);
					if (profile != null) {
						for (ProcessorBinding b : scufl2Tools.processorBindingsForProcessor((Processor)node, profile)) {							
							Activity a = b.getBoundActivity();
							URI type = a.getConfigurableType();
							try {
								Configuration c = scufl2Tools.configurationFor(a, profile);
							PropertyResource props = c.getPropertyResource();							
							if (type.equals(BEANSHELL)) { 									
								BeanshellScript script = sesameManager.designateEntity(process, BeanshellScript.class);							
								String s = props.getPropertyAsString(BEANSHELL.resolve("#script"));
								script.getWfScript().add(s);
							}
							if (type.equals(RSHELL)) { 									
								RScript script = sesameManager.designateEntity(process, RScript.class);
								String s = props.getPropertyAsString(RSHELL.resolve("#script"));
								script.getWfScript().add(s);
							}
							if (type.equals(WSDL)) {
								SOAPService soap = sesameManager.designateEntity(process, SOAPService.class);
								PropertyResource operation = props.getPropertyAsResource(WSDL.resolve("#operation"));
								soap.getWfWsdlURI().add(operation.getPropertyAsResourceURI(OPERATION.resolve("#wsdl")));
								soap.getWfWsdlOperationName().add(operation.getPropertyAsReference(OPERATION.resolve("#name")));								
							} 
							if (type.equals(TOOL)) {
								CommandLineTool cmd = sesameManager.designateEntity(process, CommandLineTool.class);
								PropertyResource desc = props.getPropertyAsResource(TOOL.resolve("#toolDescription"));
								cmd.getWfCommand().add(desc.getPropertyAsString(TOOL.resolve("#command")));
							} 
							} catch (IndexOutOfBoundsException ex) {
								continue;
							} catch (PropertyException ex) {
								continue;
							}
							
						}
					}				
				} else if (node instanceof InputPort) {
					Input input = sesameManager.create(qName, Input.class);
					Process process = sesameManager.designate(parentQName,
							Process.class);
					process.getWfHasInput().add(input);
				} else if (node instanceof OutputPort) {
					Output output = sesameManager.create(qName, Output.class);
					sesameManager.designate(parentQName, Process.class)
							.getWfHasOutput().add(output);
				} else if (node instanceof DataLink) {
					DataLink link = (DataLink) node;
					org.purl.wf4ever.wfdesc.DataLink dl = sesameManager.create(
							qnameForBean(link),
							org.purl.wf4ever.wfdesc.DataLink.class);
					Output source = sesameManager.designate(
							qnameForBean(link.getReceivesFrom()), Output.class);
					dl.getWfHasSource().add(source);
					Input sink = sesameManager.designate(
							qnameForBean(link.getSendsTo()), Input.class);
					dl.getWfHasSink().add(sink);
					sesameManager
							.designate(parentQName,
									org.purl.wf4ever.wfdesc.Workflow.class)
							.getWfHasDataLink().add(dl);
				} else {
					return false;
				}
				if (node instanceof Named) {
					Named named = (Named) node;
					Labelled labelled = sesameManager.designate(qName, Labelled.class, Description.class);
					labelled.getLabel().add(named.getName());
				}
				return true;
			}

			@Override
			public boolean visitEnter(WorkflowBean node) {
				if (node instanceof Processor
						|| node instanceof uk.org.taverna.scufl2.api.core.Workflow) {
					visit(node);
					return true;
				}
//				 System.out.println("Skipping " + node);
				return false;
			};
		});
	}

	public void save(WorkflowBundle wfBundle, OutputStream output)
			throws WriterException {
		synchronized (this) {
			if (this.wfBundle != null) {
				throw new IllegalStateException(
						"This serializer is not thread-safe and can only save one WorkflowBundle at a time");
			}
			this.wfBundle = wfBundle;
		}
		try {
			final URI baseURI;
			if (wfBundle.getMainWorkflow() != null) {
				Workflow mainWorkflow = wfBundle.getMainWorkflow();
				baseURI = uriTools.uriForBean(mainWorkflow);
				save(mainWorkflow, wfBundle.getMainProfile());
			} else {
				throw new WriterException(
						"wfdesc format requires a main workflow");
			}
			ContextAwareConnection connection = sesameManager.getConnection();
			try {
				connection.setNamespace("wfdesc",
						"http://purl.org/wf4ever/wfdesc#");
				connection.setNamespace("wf4ever",
						"http://purl.org/wf4ever/wf4ever#");
				connection.setNamespace("rdfs",
						"http://www.w3.org/2000/01/rdf-schema#");

				connection.export(new OrganizedRDFWriter(
						new TurtleWriterWithBase(output, baseURI)));
			} catch (OpenRDFException e) {
				throw new WriterException("Can't write to output", e);
			}
		} finally {
			synchronized (this) {
				this.wfBundle = null;
			}
		}
	}

	public void setScufl2Tools(Scufl2Tools scufl2Tools) {
		this.scufl2Tools = scufl2Tools;
	}

	public void setSesameManager(SesameManager sesameManager) {
		this.sesameManager = sesameManager;
	}

	public void setUriTools(URITools uriTools) {
		this.uriTools = uriTools;
	}

}
