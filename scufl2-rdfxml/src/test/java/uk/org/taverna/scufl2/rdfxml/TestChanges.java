package uk.org.taverna.scufl2.rdfxml;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.purl.wf4ever.roevo.jaxb.Change;
import org.purl.wf4ever.roevo.jaxb.ChangeSpecification.HasChange;
import org.purl.wf4ever.roevo.jaxb.VersionableResource;
import org.w3._1999._02._22_rdf_syntax_ns.Resource;
import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.rdfxml.impl.NamespacePrefixMapperJAXB_RI;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;

public class TestChanges {

	private JAXBContext jaxbContext;

	protected JAXBContext getJaxbContext() throws JAXBException {
		if (jaxbContext == null) {
			Class<?>[] packages = { ObjectFactory.class,
					org.purl.dc.elements._1.ObjectFactory.class,
					org.purl.dc.terms.ObjectFactory.class,
					org.w3._1999._02._22_rdf_syntax_ns.ObjectFactory.class,
					org.w3._2002._07.owl.ObjectFactory.class,
					org.w3._2000._01.rdf_schema.ObjectFactory.class };
			jaxbContext = JAXBContext.newInstance(packages);
		}
		return jaxbContext;
	}

	public Marshaller getMarshaller() {
		String schemaPath = "xsd/roevo.xsd";
		Marshaller marshaller;
		try {
			marshaller = getJaxbContext().createMarshaller();

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(getClass().getResource(
					schemaPath));
			// FIXME: re-enable schema
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			throw new IllegalStateException("Could not load schema "
					+ schemaPath, e);
		}
		return marshaller;
	}

	@Test
	public void testName() throws Exception {

		org.purl.wf4ever.roevo.jaxb.ObjectFactory roevo = new org.purl.wf4ever.roevo.jaxb.ObjectFactory();
		org.w3.prov.jaxb.ObjectFactory prov = new org.w3.prov.jaxb.ObjectFactory();
		org.w3._1999._02._22_rdf_syntax_ns.ObjectFactory rdf = new org.w3._1999._02._22_rdf_syntax_ns.ObjectFactory();
		org.w3._2000._01.rdf_schema.ObjectFactory rdfs = new org.w3._2000._01.rdf_schema.ObjectFactory();
		org.w3._2002._07.owl.ObjectFactory owl = new org.w3._2002._07.owl.ObjectFactory();
		
		VersionableResource v3 = roevo.createVersionableResource();
		v3.setAbout("v3");
		v3.setGeneratedAtTime(prov.createGeneratedAtTime());
		v3.getGeneratedAtTime().setValue(today());
		v3.setWasChangedBy(roevo.createVersionableResourceWasChangedBy());
		Resource v2 = rdf.createResource();
		v3.getWasChangedBy().getChangeSpecification().setFromVersion(v2);		
		HasChange hasChange = roevo.createChangeSpecificationHasChange();
		v3.getWasChangedBy().getChangeSpecification().getHasChange().add(hasChange);
		Change addition = roevo.createChange();
		hasChange.setAddition(addition);
		Resource nested = rdf.createResource();
		nested.setResource("nested");
		addition.getRelatedResource().add(nested);

		VersionableResource v5 = roevo.createVersionableResource();
		v5.setWasRevisionOf(rdf.createResource());
		v5.getWasRevisionOf().setResource("v1");

	}

	private XMLGregorianCalendar today() {
		return null;
	}
}
