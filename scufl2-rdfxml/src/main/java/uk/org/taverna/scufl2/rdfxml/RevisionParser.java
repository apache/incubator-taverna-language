package uk.org.taverna.scufl2.rdfxml;

import java.io.InputStream;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.purl.wf4ever.roevo.jaxb.ChangeSpecification;
import org.purl.wf4ever.roevo.jaxb.RoEvoDocument;
import org.purl.wf4ever.roevo.jaxb.VersionableResource;
import org.w3._1999._02._22_rdf_syntax_ns.Resource;

import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.io.ReaderException;

public class RevisionParser {

	private JAXBContext jaxbContext;

	protected JAXBContext getJaxbContext() throws JAXBException {
		if (jaxbContext == null) {
			Class<?>[] packages = { 
					org.purl.wf4ever.roevo.jaxb.ObjectFactory.class,
					org.w3.prov.jaxb.ObjectFactory.class,
					org.w3._1999._02._22_rdf_syntax_ns.ObjectFactory.class,
					org.w3._2000._01.rdf_schema.ObjectFactory.class };
			jaxbContext = JAXBContext.newInstance(packages);
		}
		return jaxbContext;
	}

	@SuppressWarnings({ "unchecked" })
	public Revision readRevisionChain(InputStream revisionDocumentStream, URI base) throws ReaderException {
		JAXBElement<RoEvoDocument> roEvoDoc;
		try {
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			roEvoDoc = (JAXBElement<RoEvoDocument>) unmarshaller.unmarshal(revisionDocumentStream);
		} catch (JAXBException e) {
			throw new ReaderException(e);
		}

		RoEvoDocument document = roEvoDoc.getValue();
		if (document.getBase() != null) {
			base = base.resolve(document.getBase());
		}
		VersionableResource verResource = (VersionableResource) document.getAny().get(0);
		return parse(base, verResource);
	}

	private Revision parse(URI base, VersionableResource verResource) {
		Revision revision = new Revision();
		revision.setIdentifier(base.resolve(verResource.getAbout()));
		if (verResource.getGeneratedAtTime() != null) {
			XMLGregorianCalendar xmlCal = verResource.getGeneratedAtTime().getValue();
			revision.setGeneratedAtTime(xmlCal.toGregorianCalendar());
		}
		
		Resource wasRevisionOf = verResource.getWasRevisionOf();
		if (wasRevisionOf != null) {
			// TODO Put these in a map
			Revision r = new Revision();
			r.setIdentifier(base.resolve(wasRevisionOf.getResource()));
			revision.setPreviousRevision(r);
		}
		
		if (verResource.getWasChangedBy() != null) {
			ChangeSpecification changeSpec = verResource.getWasChangedBy().getChangeSpecification();
			if (changeSpec.getFromVersion() != null) {
				// TODO Put these in a map
				Revision r = new Revision();
				r.setIdentifier(base.resolve(changeSpec.getFromVersion().getResource()));
				revision.setPreviousRevision(r);
				// TODO: Handle identifier conflict with wasRevisionOf
			}
			
			if (changeSpec.getType() != null) {
				revision.setChangeSpecificationType(base.resolve(
						changeSpec.getType().getResource()));
			}
		}
			
		for (Resource assoc : verResource.getWasAttributedTo()) {
			revision.getWasAttributedTo().add(
					base.resolve(assoc.getResource()));
		}
			
		for (Resource assoc : verResource.getHadOriginalSource()) {
			// TODO Put these in a map
			Revision r = new Revision();
			r.setIdentifier(base.resolve(assoc.getResource()));
			revision.getHadOriginalSources().add(r);
		}
			
		
		
		return revision;
	}
}
