package org.apache.taverna.scufl2.rdfxml;

import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.taverna.scufl2.api.annotation.Revision;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.purl.wf4ever.roevo.jaxb.Change;
import org.purl.wf4ever.roevo.jaxb.ChangeSpecification;
import org.purl.wf4ever.roevo.jaxb.ChangeSpecification.HasChange;
import org.purl.wf4ever.roevo.jaxb.RoEvoDocument;
import org.purl.wf4ever.roevo.jaxb.VersionableResource;
import org.w3._1999._02._22_rdf_syntax_ns_.Resource;


public class RevisionParser {
	private JAXBContext jaxbContext;

	protected JAXBContext getJaxbContext() throws JAXBException {
		if (jaxbContext == null) {
			Class<?>[] packages = {
					org.purl.wf4ever.roevo.jaxb.ObjectFactory.class,
					org.w3.prov.jaxb.ObjectFactory.class,
					org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory.class,
					org.w3._2000._01.rdf_schema_.ObjectFactory.class };
			jaxbContext = JAXBContext.newInstance(packages);
		}
		return jaxbContext;
	}

	@SuppressWarnings({ "unchecked" })
	public Map<URI, Revision> readRevisionChain(
			InputStream revisionDocumentStream, URI base)
			throws ReaderException {
		JAXBElement<RoEvoDocument> roEvoDoc;
		try {
			Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
			roEvoDoc = (JAXBElement<RoEvoDocument>) unmarshaller
					.unmarshal(revisionDocumentStream);
		} catch (JAXBException e) {
			throw new ReaderException(e);
		}

		RoEvoDocument document = roEvoDoc.getValue();
		if (document.getBase() != null)
			base = base.resolve(document.getBase());
		Map<URI, Revision> revisions = new LinkedHashMap<>();
		// NOTE: Silly hack to iterate/cast in one go.. will it work?
		for (VersionableResource verResource : document.getAny().toArray(
				new VersionableResource[0]))
			parse(base, verResource, revisions);
		return revisions;
	}

	private Revision parse(URI base, VersionableResource verResource,
			Map<URI, Revision> revisions) throws ReaderException {
		URI uri = base.resolve(verResource.getAbout());
		Revision revision = addOrExisting(uri, revisions);

		if (verResource.getGeneratedAtTime() != null) {
			XMLGregorianCalendar xmlCal = verResource.getGeneratedAtTime()
					.getValue();
			revision.setGeneratedAtTime(xmlCal.toGregorianCalendar());
		}

		Resource wasRevisionOf = verResource.getWasRevisionOf();
		if (wasRevisionOf != null) {
			// TODO Put these in a map
			Revision r = addOrExisting(
					base.resolve(wasRevisionOf.getResource()), revisions);
			revision.setPreviousRevision(r);
		}

		if (verResource.getWasChangedBy() != null) {
			ChangeSpecification changeSpec = verResource.getWasChangedBy()
					.getChangeSpecification();
			if (changeSpec.getFromVersion() != null) {
				Revision r = addOrExisting(
						base.resolve(changeSpec.getFromVersion().getResource()),
						revisions);
				if (revision.getPreviousRevision() != null
						&& revision.getPreviousRevision() != r)
					throw new ReaderException(
							"Inconsistent previous revision: "
									+ revision.getPreviousRevision()
											.getIdentifier() + " or "
									+ r.getIdentifier());
				revision.setPreviousRevision(r);
			}

			if (changeSpec.getType() != null)
				revision.setChangeSpecificationType(base.resolve(changeSpec
						.getType().getResource()));

			for (HasChange hasChange : changeSpec.getHasChange()) {
				if (hasChange.getAddition() != null) {
					Set<URI> additions = parse(hasChange.getAddition(), base);
					// Note: Use addAll in case a buggy XML has multiple
					// <hasChange><Addition>
					revision.getAdditionOf().addAll(additions);
				}
				if (hasChange.getModification() != null) {
					Set<URI> modifications = parse(hasChange.getModification(),
							base);
					revision.getModificationsOf().addAll(modifications);
				}
				if (hasChange.getRemoval() != null) {
					Set<URI> removals = parse(hasChange.getRemoval(), base);
					revision.getRemovalOf().addAll(removals);
				}
			}
		}

		for (Resource assoc : verResource.getWasAttributedTo())
			revision.getWasAttributedTo()
					.add(base.resolve(assoc.getResource()));

		for (Resource assoc : verResource.getHadOriginalSource()) {
			Revision r = addOrExisting(base.resolve(assoc.getResource()),
					revisions);
			revision.getHadOriginalSources().add(r);
		}

		return revision;
	}

	private Revision addOrExisting(URI uri, Map<URI, Revision> revisions) {
		Revision rev = revisions.get(uri);
		if (rev != null)
			return rev;
		rev = new Revision(uri, null);
		revisions.put(uri, rev);
		return rev;
	}

	private Set<URI> parse(Change addition, URI base) {
		Set<URI> uris = new LinkedHashSet<>();
		for (Resource r : addition.getRelatedResource())
			uris.add(base.resolve(r.getResource()));
		return uris;
	}
}
