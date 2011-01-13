package uk.org.taverna.scufl2.rdfxml;

import java.io.OutputStream;
import java.net.URI;
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
import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.rdfxml.impl.NamespacePrefixMapperImpl;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProfileDocument;
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

	private WorkflowBundle wfBundle;
	private JAXBContext jaxbContext;
	private static JAXBContext jaxbContextStatic;

	private static Logger logger = Logger.getLogger(RDFXMLSerializer.class.getCanonicalName());
	
	public RDFXMLSerializer() {
	}

	public RDFXMLSerializer(WorkflowBundle wfBundle) {
		this.setWfBundle(wfBundle);
	}

	public void workflowBundleDoc(OutputStream outputStream, URI path) throws JAXBException {
		WorkflowBundleDocument doc = getObjectFactory().createWorkflowBundleDocument();
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory().createRDF(doc);
		getMarshaller().marshal(element, outputStream);
	}

	private ObjectFactory getObjectFactory() {
		return new ObjectFactory();
	}

	public Marshaller getMarshaller() {
		String schemaPath = "xsd/scufl2.xsd";
		Marshaller marshaller;
		try {
			marshaller = getJaxbContext().createMarshaller();
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(getClass().getResource(schemaPath));
			//marshaller.setSchema(schema);
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
		WorkflowDocument doc = getObjectFactory().createWorkflowDocument();
		//doc.getAny().add(getObjectFactory().createWorkflowBundle());
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory().createRDF(doc);
		
		
		getMarshaller().marshal(element, outputStream);
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

	public void profileDoc(OutputStream outputStream, Profile pf, URI create) throws JAXBException {
		ProfileDocument doc = getObjectFactory().createProfileDocument();
		JAXBElement<RDF> element = new org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory().createRDF(doc);
		getMarshaller().marshal(element, outputStream);
		
	}

}
