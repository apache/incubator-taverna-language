package org.purl.wf4ever.wfdesc.scufl2;

import static uk.org.taverna.scufl2.api.common.Scufl2Tools.NESTED_WORKFLOW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.openrdf.OpenRDFException;
import org.openrdf.concepts.rdfs.Resource;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.purl.wf4ever.roterms.RotermsResource;
import org.purl.wf4ever.wf4ever.BeanshellScript;
import org.purl.wf4ever.wf4ever.CommandLineTool;
import org.purl.wf4ever.wf4ever.RESTService;
import org.purl.wf4ever.wf4ever.RScript;
import org.purl.wf4ever.wf4ever.SOAPService;
import org.purl.wf4ever.wfdesc.Input;
import org.purl.wf4ever.wfdesc.Output;
import org.purl.wf4ever.wfdesc.Process;
import org.w3.prov.Entity;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WriterException;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.OutputPort;
import uk.org.taverna.scufl2.api.port.WorkflowPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;

public class WfdescSerialiser {

    private static Logger logger = Logger.getLogger(WfdescSerialiser.class.getCanonicalName());
    
    public static URI REST = URI
            .create("http://ns.taverna.org.uk/2010/activity/rest");
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
		    
		    // Raven workaround - register SPARQLParserFactory
		    QueryParserRegistry.getInstance().add(new SPARQLParserFactory());
		    
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
	
	protected void save(final WorkflowBundle bundle) {
	    bundle.accept(new VisitorWithPath() {
			Scufl2Tools scufl2Tools = new Scufl2Tools();
            public boolean visit() {
                WorkflowBean node = getCurrentNode(); 
//                System.out.println(node);
                if (node instanceof WorkflowBundle) {
                    return true;
                }
//				@SuppressWarnings("rawtypes")
								
                if (node instanceof uk.org.taverna.scufl2.api.core.Workflow) {
				    entityForBean(node, org.purl.wf4ever.wfdesc.Workflow.class);					
				} else if (node instanceof Processor) {
				    Processor processor = (Processor)node;
				    Process process = entityForBean(processor, Process.class);
				    entityForBean(processor.getParent(), org.purl.wf4ever.wfdesc.Workflow.class).getWfHasSubProcess().add(process);
				} else if (node instanceof InputPort) {
					WorkflowBean parent = ((Child) node).getParent();
                    Input input = entityForBean(node, Input.class);
					Process process = entityForBean(parent, Process.class); 
					process.getWfHasInput().add(input);
				} else if (node instanceof OutputPort) {
                    WorkflowBean parent = ((Child) node).getParent();
				    Output output = entityForBean(node, Output.class);
                    Process process = entityForBean(parent, Process.class);
                    process.getWfHasOutput().add(output);
				} else if (node instanceof DataLink) {
				    WorkflowBean parent = ((Child) node).getParent();
                    DataLink link = (DataLink) node;
                    org.purl.wf4ever.wfdesc.DataLink dl = entityForBean(link,
                            org.purl.wf4ever.wfdesc.DataLink.class);
                    Output source = entityForBean(link.getReceivesFrom(),
                            Output.class);
                    dl.getWfHasSource().add(source);
                    Input sink = entityForBean(link.getSendsTo(), Input.class);
                    dl.getWfHasSink().add(sink);
                    entityForBean(parent,
                            org.purl.wf4ever.wfdesc.Workflow.class)
                            .getWfHasDataLink().add(dl);
				} else if (node instanceof Profile) {
				    // So that we can get at the ProcessorBinding - buy only if it is the main Profile
				    return node == bundle.getMainProfile();
                } else if (node instanceof ProcessorBinding) {
                    ProcessorBinding b = (ProcessorBinding) node;
                    Activity a = b.getBoundActivity();
                    Processor boundProcessor = b.getBoundProcessor();
                    Process process = entityForBean(boundProcessor, Process.class);
                    
                    // Note: We don't describe the activity and processor binding in wfdesc. Instead we 
                    // assign additional types and attributes to the parent processor
                    
                    try {
                        URI type = a.getType();
                        Configuration c = scufl2Tools.configurationFor(a, b.getParent());
                        JsonNode json = c.getJson();
                        if (type.equals(BEANSHELL)) {                                   
                            BeanshellScript script = getSesameManager().designateEntity(process, BeanshellScript.class);                         
                            String s = json.get("script").asText();
                            script.getWfScript().add(s);

                            JsonNode localDep = json.get("localDependency");
                            if (localDep != null && localDep.isArray()) {
                                for (int i=0; i<localDep.size(); i++) {
                                    String depStr = localDep.get(i).asText();
                                    RotermsResource res = getSesameManager().designateEntity(script, RotermsResource.class);
                                    Resource dep = getSesameManager().create(Resource.class);
                                    dep.setRdfsLabel(depStr);
                                    dep.setRdfsComment("JAR dependency");
                                    res.getWfRequiresSoftware().add(dep);
                                    // Somehow this gets the whole thing to fall out of the graph!
//                                  QName depQ = new QName("http://google.com/", ""+ UUID.randomUUID());
//                                  sesameManager.rename(dep, depQ);
                                    
                                }
                            }
                        }
                        if (type.equals(RSHELL)) {                                  
                            RScript script = getSesameManager().designateEntity(process, RScript.class);
                            String s = json.get("script").asText();
                            script.getWfScript().add(s);
                        }
                        if (type.equals(WSDL)) {
                            SOAPService soap = getSesameManager().designateEntity(process, SOAPService.class);
                            JsonNode operation = json.get("operation");
                            URI wsdl = URI.create(operation.get("wsdl").asText());
                            soap.getWfWsdlURI().add(wsdl);
                            soap.getWfWsdlOperationName().add(operation.get("name").asText());
                            soap.getWfRootURI().add(wsdl.resolve("/"));

                        } 
                        if (type.equals(REST)) {
                            RESTService rest = getSesameManager().designateEntity(process, RESTService.class);
//                            System.out.println(json);
                            JsonNode request = json.get("request");
                            String uriTemplate = request.get("absoluteURITemplate").asText();
                            uriTemplate = uriTemplate.replace("{", "");
                            uriTemplate = uriTemplate.replace("}", "");
                            // TODO: Detect {}
                            URI root = URI.create(uriTemplate).resolve("/");
                            rest.getWfRootURI().add(root);
                        } 
                        if (type.equals(TOOL)) {
                            CommandLineTool cmd = getSesameManager().designateEntity(process, CommandLineTool.class);
                            JsonNode desc = json.get("toolDescription");
                            System.out.println(json);
                            JsonNode command = desc.get("command");
                            if (command != null) { 
                                cmd.getWfCommand().add(command.asText());
                            }
                        } 
                        if (type.equals(NESTED_WORKFLOW)) {
                            Workflow nestedWf = scufl2Tools.nestedWorkflowForProcessor(boundProcessor, b.getParent());
                            // The parent process is a specialization of the nested workflow
                            // (because the nested workflow could exist as several processors)
                            specializationOf(boundProcessor, nestedWf);
                            getSesameManager().designateEntity(process, org.purl.wf4ever.wfdesc.Workflow.class);
                            
                            // Just like the Processor specializes the nested workflow, the 
                            // ProcessorPorts specialize the WorkflowPort 
                            for (ProcessorPortBinding portBinding : b.getInputPortBindings()) {
                                // Map from activity port (not in wfdesc) to WorkflowPort
                                WorkflowPort wfPort = nestedWf.getInputPorts().getByName(portBinding.getBoundActivityPort().getName());
                                if (wfPort == null) { 
                                    continue;
                                }
                                specializationOf(portBinding.getBoundProcessorPort(), wfPort);                                                                                         
                            }
                            for (ProcessorPortBinding portBinding : b.getOutputPortBindings()) {
                                WorkflowPort wfPort = nestedWf.getOutputPorts().getByName(portBinding.getBoundActivityPort().getName());
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
//				    System.out.println("--NO!");
					return false;
				}
                for (Annotation ann : scufl2Tools.annotationsFor(node, bundle)) {
                    String annotationBody = ann.getBody().toASCIIString();
                    String baseURI = bundle.getGlobalBaseURI().resolve(ann.getBody()).toASCIIString();
                    InputStream annotationStream;
                    try {                        
                        annotationStream = bundle.getResources().getResourceAsInputStream(
                                annotationBody);
                   
                        RDFFormat dataFormat = RDFFormat.TURTLE;
                        try {
                            getSesameManager().getConnection().add(annotationStream, baseURI, dataFormat);                    
                        } catch (OpenRDFException e) {
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
                    Labelled labelled = entityForBean(node, Labelled.class);
					labelled.getLabel().add(named.getName());
				}
				return true;
			}

            private void specializationOf(WorkflowBean special, WorkflowBean general) {
                Entity specialEnt = entityForBean(special, Entity.class); 
                Entity generalEnt = entityForBean(general, Entity.class);
                specialEnt.getWfSpecializationOf().add(generalEnt);
            }

            private <T> T entityForBean(WorkflowBean bean, Class<T> type) {
                return getSesameManager().create(qnameForBean(bean), type);
            }

//			@Override
//			public boolean visitEnter(WorkflowBean node) {
//                if (node instanceof Processor
//                        || node instanceof uk.org.taverna.scufl2.api.core.Workflow
//                        || node instanceof Port || node instanceof DataLink) {
//					visit(node);
//					return true;
//				}
//                // The other node types (e.g. dispatch stack, configuration) are
//                // not (directly) represented in wfdesc
////				 System.out.println("Skipping " + node);
//				return false;
//			};
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
				save(wfBundle);
			} else {
				throw new WriterException(
						"wfdesc format requires a main workflow");
			}
			ContextAwareConnection connection = getSesameManager().getConnection();
			try {

				connection.setNamespace("rdfs",
						"http://www.w3.org/2000/01/rdf-schema#");
				connection.setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
				connection.setNamespace("owl", "http://www.w3.org/2002/07/owl#");
				connection.setNamespace("prov", "http://www.w3.org/ns/prov#");
				connection.setNamespace("wfdesc",
				        "http://purl.org/wf4ever/wfdesc#");
				connection.setNamespace("wf4ever",
				        "http://purl.org/wf4ever/wf4ever#");
				connection.setNamespace("roterms",
				        "http://purl.org/wf4ever/roterms#");
				connection.setNamespace("dc", "http://purl.org/dc/elements/1.1/");
				connection.setNamespace("dcterms", "http://purl.org/dc/terms/");
				connection.setNamespace("comp", "http://purl.org/DP/components#");
				connection.setNamespace("dep", "http://scape.keep.pt/vocab/dependencies#");
				connection.setNamespace("biocat", "http://biocatalogue.org/attribute/");
				
                connection.setNamespace("", "#");

				
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
