package uk.org.taverna.scufl2.rdfxml;

import java.io.OutputStream;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;

import org.w3._1999._02._22_rdf_syntax_ns_.RDF;
import org.w3c.dom.Document;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundleDocument;

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
		try {
			return getJaxbContext().createMarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		}		
	}

	public Document workflowDoc(Workflow wf, URI path) {
		// TODO Auto-generated method stub
		return null;
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

}
