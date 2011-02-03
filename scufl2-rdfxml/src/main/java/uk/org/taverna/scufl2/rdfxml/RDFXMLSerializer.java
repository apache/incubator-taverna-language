package uk.org.taverna.scufl2.rdfxml;

import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3._1999._02._22_rdf_syntax_ns_.RDF;
import org.w3._1999._02._22_rdf_syntax_ns_.Resource;
import org.w3._2000._01.rdf_schema_.SeeAlso;
import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WriterException;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.rdfxml.impl.NamespacePrefixMapperJAXB_RI;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.PortDepth;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProfileDocument;
import uk.org.taverna.scufl2.rdfxml.jaxb.SeeAlsoType;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundleDocument;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowDocument;

public class RDFXMLSerializer {

	protected synchronized static JAXBContext getJAxbContextStatic()
			throws JAXBException {
		if (jaxbContextStatic == null) {
			Class<?>[] packages = { ObjectFactory.class,
					org.purl.dc.elements._1.ObjectFactory.class,
					org.purl.dc.terms.ObjectFactory.class,
					org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory.class,
					org.w3._2002._07.owl_.ObjectFactory.class,
					org.w3._2000._01.rdf_schema_.ObjectFactory.class };
			jaxbContextStatic = JAXBContext.newInstance(packages);
		}
		return jaxbContextStatic;
	}

	private ObjectFactory objectFactory = new ObjectFactory();
	private org.w3._2000._01.rdf_schema_.ObjectFactory rdfsObjectFactory = new org.w3._2000._01.rdf_schema_.ObjectFactory();
	private org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory rdfObjectFactory = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory();

	private URITools uriTools = new URITools();

	private boolean usingSchema = false;

	private WorkflowBundle wfBundle;
	private JAXBContext jaxbContext;
	private Map<WorkflowBean, URI> seeAlsoUris = new HashMap<WorkflowBean, URI>();
	private static JAXBContext jaxbContextStatic;

	private static Logger logger = Logger.getLogger(RDFXMLSerializer.class
			.getCanonicalName());

	public RDFXMLSerializer() {
	}

	public RDFXMLSerializer(WorkflowBundle wfBundle) {
		this.setWfBundle(wfBundle);
	}

	public void workflowBundleDoc(OutputStream outputStream, URI path)
			throws JAXBException, WriterException {
		uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle bundle = makeWorkflowBundleElem();
		WorkflowBundleDocument doc = objectFactory
				.createWorkflowBundleDocument();
		doc.getAny().add(bundle);
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory()
				.createRDF(doc);

		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(wfBundle, path);
	}

	protected uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle makeWorkflowBundleElem() {
		uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle bundle = objectFactory
				.createWorkflowBundle();
		// FIXME: Support other URIs
		bundle.setAbout("./");
		bundle.setName(wfBundle.getName());

		if (wfBundle.getSameBaseAs() != null) {
			Resource sameBaseAs = rdfObjectFactory.createResource();
			sameBaseAs.setResource(wfBundle.getSameBaseAs().toASCIIString());
			bundle.setSameBaseAs(sameBaseAs);
		}

		for (Workflow wf : wfBundle.getWorkflows()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Workflow wfElem = objectFactory
					.createWorkflowBundleWorkflow();
			SeeAlsoType seeAlsoElem = objectFactory.createSeeAlsoType();
			seeAlsoElem.setAbout(uriTools.relativeUriForBean(wf, wfBundle)
					.toASCIIString());
			;

			if (seeAlsoUris.containsKey(wf)) {
				SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
				seeAlso.setResource(seeAlsoUris.get(wf).toASCIIString());
				seeAlsoElem.setSeeAlso(seeAlso);
			} else {
				logger.warning("Can't find bundle URI for workflow document "
						+ wf.getName());
			}

			wfElem.setWorkflow(seeAlsoElem);
			bundle.getWorkflow().add(wfElem);

			if (wfBundle.getMainWorkflow() == wf) {
				Resource mainWorkflow = rdfObjectFactory.createResource();
				mainWorkflow.setResource(seeAlsoElem.getAbout());
				bundle.setMainWorkflow(mainWorkflow);
			}
		}

		for (Profile pf : wfBundle.getProfiles()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Profile wfElem = objectFactory
					.createWorkflowBundleProfile();
			SeeAlsoType seeAlsoElem = objectFactory.createSeeAlsoType();
			seeAlsoElem.setAbout(uriTools.relativeUriForBean(pf, wfBundle)
					.toASCIIString());
			;

			if (seeAlsoUris.containsKey(pf)) {
				SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
				seeAlso.setResource(seeAlsoUris.get(pf).toASCIIString());
				seeAlsoElem.setSeeAlso(seeAlso);
			} else {
				logger.warning("Can't find bundle URI for profile document "
						+ pf.getName());
			}

			wfElem.setProfile(seeAlsoElem);
			bundle.getProfile().add(wfElem);

			if (wfBundle.getMainProfile() == pf) {
				Resource mainProfile = rdfObjectFactory.createResource();
				mainProfile.setResource(seeAlsoElem.getAbout());
				bundle.setMainProfile(mainProfile);
			}
		}

		return bundle;
	}

	public Marshaller getMarshaller() {
		String schemaPath = "xsd/scufl2.xsd";
		Marshaller marshaller;
		try {
			marshaller = getJaxbContext().createMarshaller();

			if (isUsingSchema()) {
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(getClass().getResource(
						schemaPath));
				// FIXME: re-enable schema
				marshaller.setSchema(schema);
			}
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
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

	protected void setPrefixMapper(Marshaller marshaller) {
		boolean setPrefixMapper = false;

		try {
			// This only works with JAXB RI, in which case we can set the
			// namespace
			// prefix mapper
			Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new NamespacePrefixMapperJAXB_RI());
			// Note: A similar mapper for the built-in java
			// (com.sun.xml.bind.internal.namespacePrefixMapper)
			// is no longer included here, as it will not (easily) compile with
			// Maven.
			setPrefixMapper = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!setPrefixMapper) {
			logger.warning("Could not set prefix mapper (incompatible JAXB) "
					+ "- will use prefixes ns0, ns1, ..");
		}
	}

	public void workflowDoc(OutputStream outputStream, Workflow wf, URI path)
			throws JAXBException, WriterException {
		uk.org.taverna.scufl2.rdfxml.jaxb.Workflow wfElem = makeWorkflow(wf,
				path);
		WorkflowDocument doc = objectFactory.createWorkflowDocument();
		doc.getAny().add(wfElem);
		
		URI wfUri = uriTools.relativeUriForBean(wf, wfBundle);		
		doc.setBase(uriTools.relativePath(path, wfUri).toASCIIString());

		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory()
				.createRDF(doc);
		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(wf, path);
	}

	protected uk.org.taverna.scufl2.rdfxml.jaxb.Workflow makeWorkflow(
			Workflow wf, URI documentPath) {
		uk.org.taverna.scufl2.rdfxml.jaxb.Workflow workflow = objectFactory
				.createWorkflow();
		workflow.setAbout("");
		workflow.setName(wf.getName());
	
		if (wf.getWorkflowIdentifier() != null) {
			Resource wfId = rdfObjectFactory.createResource();
			wfId.setResource(wf.getWorkflowIdentifier().toASCIIString());
			workflow.setWorkflowIdentifier(wfId);
		}
	
		for (InputWorkflowPort ip : wf.getInputPorts()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.InputWorkflowPort inP = objectFactory.createWorkflowInputWorkflowPort();
			uk.org.taverna.scufl2.rdfxml.jaxb.InputWorkflowPort inPort = objectFactory.createInputWorkflowPort();
			inP.setInputWorkflowPort(inPort);
			inPort.setName(ip.getName());
			
			URI portURI = uriTools.relativeUriForBean(ip, wf);
			inPort.setAbout(portURI.toASCIIString());
			// FIXME: Add xml:base - no need for relativePath here!
			if (ip.getDepth() != null) {
				PortDepth portDepth = objectFactory.createPortDepth();
				portDepth.setValue(BigInteger.valueOf(ip.getDepth()));			
				inPort.setPortDepth(portDepth);
			}
			workflow.getInputWorkflowPort().add(inP);			
		}
		
		for (OutputWorkflowPort op : wf.getOutputPorts()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.OutputWorkflowPort inP = objectFactory.createWorkflowOutputWorkflowPort();
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputWorkflowPort outPort = objectFactory.createOutputWorkflowPort();
			inP.setOutputWorkflowPort(outPort);
			outPort.setName(op.getName());
			
			URI portURI = uriTools.relativeUriForBean(op, wf);
			outPort.setAbout(portURI.toASCIIString());			
			workflow.getOutputWorkflowPort().add(inP);			
		}
		
		for (Processor p : wf.getProcessors()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.Processor wfProc = objectFactory.createWorkflowProcessor();
			uk.org.taverna.scufl2.rdfxml.jaxb.Processor proc = objectFactory.createProcessor();
			wfProc.setProcessor(proc);			
			proc.setName(p.getName());
			
			URI procUri = uriTools.relativeUriForBean(p, wf);
			proc.setAbout(procUri.toASCIIString());
			wfProc.setProcessor(proc);			
			workflow.getProcessor().add(wfProc);
			
			// TODO: Input/Output

			// TODO: dispatchStack
			// TODO: iteration strategy
		}
		
		// TODO: Datalinks
		
		return workflow;
	}

	public void setWfBundle(WorkflowBundle wfBundle) {
		this.wfBundle = wfBundle;
	}

	public WorkflowBundle getWfBundle() {
		return wfBundle;
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}

	public JAXBContext getJaxbContext() throws JAXBException {
		if (jaxbContext == null) {
			return getJAxbContextStatic();
		}
		return jaxbContext;
	}

	public void profileDoc(OutputStream outputStream, Profile pf, URI path)
			throws JAXBException, WriterException {
		ProfileDocument doc = objectFactory.createProfileDocument();
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory()
				.createRDF(doc);
		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(pf, path);

	}

	public void setUsingSchema(boolean usingSchema) {
		this.usingSchema = usingSchema;
	}

	public boolean isUsingSchema() {
		return usingSchema;
	}

}
