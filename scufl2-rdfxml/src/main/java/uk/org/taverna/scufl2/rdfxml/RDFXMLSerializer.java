package uk.org.taverna.scufl2.rdfxml;

import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.impl.LazyMap;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.rdfxml.impl.NamespacePrefixMapperImpl;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
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
	
	protected ObjectFactory objectFactory = new ObjectFactory();
	protected org.w3._2000._01.rdf_schema_.ObjectFactory rdfsObjectFactory = new org.w3._2000._01.rdf_schema_.ObjectFactory();
	protected org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory rdfObjectFactory = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory();	

	protected URITools uriTools = new URITools();
	
	private boolean usingSchema = false;
	
	private WorkflowBundle wfBundle;
	private JAXBContext jaxbContext;
	private Map<WorkflowBean, URI> seeAlsoUris = new HashMap<WorkflowBean, URI>();
	private static JAXBContext jaxbContextStatic;

	private static Logger logger = Logger.getLogger(RDFXMLSerializer.class.getCanonicalName());
	
	public RDFXMLSerializer() {
	}

	public RDFXMLSerializer(WorkflowBundle wfBundle) {
		this.setWfBundle(wfBundle);
	}

	public void workflowBundleDoc(OutputStream outputStream, URI path) throws JAXBException {
		uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle bundle = makeWorkflowBundleElem();		
		WorkflowBundleDocument doc = objectFactory.createWorkflowBundleDocument();
		doc.getAny().add(bundle);
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory().createRDF(doc);
		getMarshaller().marshal(element, outputStream);
		seeAlsoUris.put(wfBundle, path);
	}

	protected uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle makeWorkflowBundleElem() {
		uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle bundle = objectFactory.createWorkflowBundle();
		// FIXME: Support other URIs
		bundle.setAbout(uriTools.relativeUriForBean(wfBundle, wfBundle).toASCIIString());
		bundle.setName(wfBundle.getName());

		if (wfBundle.getSameBaseAs() != null) {
			Resource sameBaseAs = rdfObjectFactory.createResource();
			sameBaseAs.setResource(wfBundle.getSameBaseAs().toASCIIString());
			bundle.setSameBaseAs(sameBaseAs);
		}
		
		for (Workflow wf : wfBundle.getWorkflows()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Workflow wfElem = objectFactory.createWorkflowBundleWorkflow();
			SeeAlsoType seeAlsoElem = objectFactory.createSeeAlsoType();
			// FIXME: Get URI for Workflow
			seeAlsoElem.setAbout(uriTools.relativeUriForBean(wf, wfBundle).toASCIIString());;
			
			SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
			seeAlso.setResource(seeAlsoUris.get(wf).toASCIIString());
			seeAlsoElem.setSeeAlso(seeAlso);
			
			wfElem.setWorkflow(seeAlsoElem);			
			bundle.getWorkflow().add(wfElem);
			
			if (wfBundle.getMainWorkflow() == wf) {
				Resource mainWorkflow = rdfObjectFactory.createResource();
				mainWorkflow.setResource(seeAlsoElem.getAbout());
				bundle.setMainWorkflow(mainWorkflow);
			}
		}
		
		for (Profile pf : wfBundle.getProfiles()) {
			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Profile wfElem = objectFactory.createWorkflowBundleProfile();
			SeeAlsoType seeAlsoElem = objectFactory.createSeeAlsoType();
			seeAlsoElem.setAbout(uriTools.relativeUriForBean(pf, wfBundle).toASCIIString());;
			
			SeeAlso seeAlso = rdfsObjectFactory.createSeeAlso();
			seeAlso.setResource(seeAlsoUris.get(pf).toASCIIString());
			seeAlsoElem.setSeeAlso(seeAlso);
			
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
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(getClass().getResource(schemaPath));
				// FIXME: re-enable schema
				marshaller.setSchema(schema);
			}
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
		} catch (JAXBException e) {
			throw new IllegalStateException(e);		
		} catch (SAXException e) {
			throw new IllegalStateException("Could not load schema " + schemaPath, e);
		}
		try {
			marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",new NamespacePrefixMapperImpl());
		} catch (JAXBException e) {
			logger.log(Level.WARNING, "Can't set namespace prefix mapper", e);
		}
		return marshaller;
		
	}

	public void workflowDoc(OutputStream outputStream, Workflow wf, URI path) throws JAXBException {
		WorkflowDocument doc = objectFactory.createWorkflowDocument();	
		uk.org.taverna.scufl2.rdfxml.jaxb.Workflow wfElem = objectFactory.createWorkflow();
		doc.getAny().add(wfElem);
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory().createRDF(doc);
		getMarshaller().marshal(element, outputStream);		
		seeAlsoUris.put(wf, path);
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

	public void profileDoc(OutputStream outputStream, Profile pf, URI path) throws JAXBException {
		ProfileDocument doc = objectFactory.createProfileDocument();
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory().createRDF(doc);
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
