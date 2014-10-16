package org.purl.wf4ever.robundle.manifest.combine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.identifiers.combine_specifications.omex_manifest.Content;
import org.identifiers.combine_specifications.omex_manifest.ObjectFactory;
import org.identifiers.combine_specifications.omex_manifest.OmexManifest;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.manifest.Agent;
import org.purl.wf4ever.robundle.manifest.PathAnnotation;
import org.purl.wf4ever.robundle.manifest.PathMetadata;
import org.purl.wf4ever.robundle.utils.RDFUtils;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Parse and generate COMBINE Archive OMEX manifest
 * 
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class CombineManifest {

	private static final URI OMEX_METADATA = URI
			.create("http://identifiers.org/combine.specifications/omex-metadata");

	private static final String MANIFEST_XML = "manifest.xml";

	public static class ManifestNamespacePrefixMapperJAXB_RI extends
			NamespacePrefixMapper {
		@Override
		public String getPreferredPrefix(String namespaceUri,
				String suggestion, boolean requirePrefix) {
			if (namespaceUri.equals(OMEX_METADATA.toString())) {
				return "";
			}
			return suggestion;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[] { OMEX_METADATA.toString() };
		}

	}

	private static Logger logger = Logger.getLogger(CombineManifest.class
			.getCanonicalName());

	private org.purl.wf4ever.robundle.manifest.Manifest manifest;
	private Bundle bundle;
	private static boolean warnedPrefixMapper;

	private static ObjectFactory objectFactory = new ObjectFactory();

	private static JAXBContext jaxbContext;

	public CombineManifest(org.purl.wf4ever.robundle.manifest.Manifest manifest) {
		this.manifest = manifest;
		this.bundle = manifest.getBundle();
	}

	private OmexManifest makeOmexManifest() {
		Path manifestXml = bundle.getRoot().resolve("manifest.xml");		
		OmexManifest omexManifest = objectFactory.createOmexManifest();

		PathMetadata aggr = manifest.getAggregation(manifestXml);
		if (aggr.getConformsTo() == null) {
			// Add the manifest itself
			aggr.setConformsTo(URI
					.create("http://identifiers.org/combine.specifications/omex-manifest"));
		}

		for (PathMetadata metadata : manifest.getAggregates()) {
			Content content = objectFactory.createContent();
			Path file = metadata.getFile();

			if (file == null) {
				content.setLocation(metadata.getUri().toString());
			} else {
				Path relPath = metadata.getFile().relativize(bundle.getRoot());
				content.setLocation("./" + relPath);
			}
			if (metadata.getMediatype() != null
					&& !metadata.getMediatype().isEmpty()) {
				content.setFormat(metadata.getMediatype());
			} else if (metadata.getConformsTo() != null) {
				content.setFormat(metadata.getConformsTo().toString());
			} else {
				// Binary fallback as 'format' is required attribute
				content.setFormat("application/octet-stream");
			}

			// TODO: Handle 'master' attribute

			omexManifest.getContent().add(content);

		}
		// TODO: Should we add .ro/manifest.json and .ro/* ?
		return omexManifest;
	}

	public Path createManifestXML() throws IOException {
		OmexManifest omexManifest = makeOmexManifest();
		
		Path manifestXml = manifestXmlPath(bundle);
		Files.createDirectories(manifestXml.getParent());
		try (OutputStream outStream = Files.newOutputStream(manifestXml)) {
			try {
				createMarshaller().marshal(omexManifest, outStream);
			} catch (JAXBException e) {
				throw new RuntimeException("Could not serialize OMEX Manifest",
						e);
			}
		}
		return manifestXml;
	}
	
	public void readCombineArchive() throws IOException {
		readManifestXML();
		findAnnotations();
		
	}

	public void readManifestXML() throws IOException {
		Path manifestXml = manifestXmlPath(bundle);
		OmexManifest omexManifest;
		try (InputStream inStream = Files.newInputStream(manifestXml)) {
			InputSource src = new InputSource(inStream);
			Source source = new SAXSource(src);
			omexManifest = createUnMarshaller().unmarshal(source, OmexManifest.class).getValue();
			//omexManifest = (OmexManifest) createUnMarshaller().unmarshal(inStream);
		} catch (JAXBException|ClassCastException e) {
			// logger.warning("Could not parse " + manifestXml);
			throw new IOException("Could not parse " + manifestXml, e);
		}
		if (!manifest.getManifest().contains(manifestXml)) {
			manifest.getManifest().add(manifestXml);
		}
		for (Content c : omexManifest.getContent()) {
			PathMetadata metadata;
			if (c.getLocation().contains(":")) {
				try { 
					URI uri = new URI(c.getLocation());
					if (uri.isAbsolute()) { 
						metadata = manifest.getAggregation(uri);
					} else { 
						logger.warning("Not an absolute URI, but contains :" + c.getLocation());
						continue;
					} 
				} catch (URISyntaxException e) {
					logger.warning("Invalid URI " + c.getLocation());
					continue; 
				}
			} else { 			
				Path path = bundle.getRoot().resolve(c.getLocation());
				if (Files.exists(path)) {
					metadata = manifest.getAggregation(path);
				} else {
					logger.warning(MANIFEST_XML + " listed relative path " + path
							+ ", but it does not exist in bundle");
					continue;
				}
			}
			
			// Format - is it an URI or media type?
			if (c.getFormat().contains(":")) { 
				metadata.setConformsTo(URI.create(c.getFormat()));
			} else if (! c.getFormat().isEmpty()) {			
				metadata.setMediatype(c.getFormat());
			} else if (metadata.getFile() != null) { 
				metadata.setMediatype(manifest.guessMediaType(metadata.getFile()));
			} // else: Not needed for URIs
		}
	}

	private static Path manifestXmlPath(Bundle bundle) {
		return bundle.getRoot().resolve(MANIFEST_XML);
	}

	protected static void setPrefixMapper(Marshaller marshaller) {
		boolean setPrefixMapper = false;

		try {
			// This only works with JAXB RI, in which case we can set the
			// namespace prefix mapper
			Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new ManifestNamespacePrefixMapperJAXB_RI());
			// Note: A similar mapper for the built-in java
			// (com.sun.xml.bind.internal.namespacePrefixMapper)
			// is no longer included here, as it will not (easily) compile with
			// Maven.
			setPrefixMapper = true;
		} catch (Exception e) {
			logger.log(Level.FINE, "Can't find NamespacePrefixMapper", e);
		}

		if (!setPrefixMapper && !warnedPrefixMapper) {
			logger.info("Could not set prefix mapper (missing or incompatible JAXB) "
					+ "- will use prefixes ns0, ns1, ..");
			warnedPrefixMapper = true;
		}
	}

	protected static synchronized Marshaller createMarshaller()
			throws JAXBException {
		Marshaller marshaller = getJaxbContext().createMarshaller();
		setPrefixMapper(marshaller);
		return marshaller;
	}

	protected static synchronized Unmarshaller createUnMarshaller()
			throws JAXBException {
		Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
		return unmarshaller;
	}

	protected static synchronized JAXBContext getJaxbContext()
			throws JAXBException {
		if (jaxbContext == null) {
			jaxbContext = JAXBContext
					.newInstance(org.identifiers.combine_specifications.omex_manifest.ObjectFactory.class);
		}
		return jaxbContext;
	}

	public static boolean containsManifest(Bundle bundle) {
		return Files.isRegularFile(manifestXmlPath(bundle));
	}


	private void findAnnotations() throws IOException {
		Path metadataRdf = null;
		for (PathMetadata agg : manifest.getAggregates()) {
			if (OMEX_METADATA.equals(agg.getConformsTo())) { 
				metadataRdf = agg.getFile();
				break; // TODO: Support not just the first one				
				// TODO: support external metadata with agg.getUri() ?				
			}
		}
		if (metadataRdf == null) {
			// fallback to hard-coded filename
			metadataRdf = bundle.getRoot().resolve("metadata.rdf");
		}
		if (! Files.exists(metadataRdf)) { 
			return;
		}

		Model metadata;
		try {
			metadata = parseRDF(metadataRdf);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Can't read " + metadataRdf, e);
			return;
		} catch (RiotException e) {
			logger.log(Level.WARNING, "Can't parse " + metadataRdf, e);
			return;
		}
		
		for (URI about : bundleSubjects()) {
			Resource resource = metadata.getResource(about.toString());
			if (! metadata.containsResource(resource)) {
				continue;
			}

			PathAnnotation ann = new PathAnnotation();
			ann.setAbout(manifest.relativeToBundleRoot(about));
			ann.setContent(manifest.relativeToBundleRoot(metadataRdf.toUri()));
			manifest.getAnnotations().add(ann);
			
			// Extract information that could be in our manifest
			PathMetadata pathMetadata = manifest.getAggregation(about);
			
			// Created date. We'll prefer dcModified.
			Property dcCreated = metadata.getProperty("http://purl.org/dc/terms/created");
			Property dcModified = metadata.getProperty("http://purl.org/dc/terms/modified");
			Statement createdSt = resource.getProperty(dcModified);
			if (createdSt == null) {
				createdSt = resource.getProperty(dcCreated);
			}
			if (createdSt != null) {
				FileTime fileTime = RDFUtils.literalAsFileTime(createdSt.getObject());
				if (fileTime == null && createdSt.getResource().isResource()) { 
					// perhaps one of those strange mixups of XML and RDF...
					Property dcW3CDTF = metadata.getProperty("http://purl.org/dc/terms/W3CDTF");					
					Statement w3cSt = createdSt.getResource().getProperty(dcW3CDTF);
					if (w3cSt != null) {
						fileTime = RDFUtils.literalAsFileTime(w3cSt.getObject());
					}

				}
				if (fileTime != null) { 
					pathMetadata.setCreatedOn(fileTime);
					if (pathMetadata.getFile() != null) {
						Files.setLastModifiedTime(pathMetadata.getFile(), fileTime);
					}
				}
			}
			
			for (RDFNode s : creatingAgentsFor(resource)) {
				if (pathMetadata.getCreatedBy() != null) {
					logger.warning("Ignoring additional createdBy agents for " + resource);
					break;
				}
				if (s.isLiteral()) {
					pathMetadata.setCreatedBy(new Agent(s.asLiteral().getLexicalForm()));
				} else {
					Resource agentResource = s.asResource();
					Agent agent = new Agent();
					if (agentResource.isURIResource()) {
						URI agentUri = URI.create(agentResource.getURI());
						if (agentResource.getURI().startsWith("http://orcid.org/")) {
							agent.setOrcid(agentUri);
						} else {
							agent.setUri(agentUri);
						}
					} else { 
						Resource mbox = mboxForAgent(agentResource);
						if (mbox != null && mbox.isURIResource()) {
							agent.setUri(URI.create(mbox.getURI()));
						}
					}
					agent.setName(nameForAgent(agentResource));
					pathMetadata.setCreatedBy(agent);
				}
			}
			if (pathMetadata.getFile().equals(bundle.getRoot()) || 
					pathMetadata.getFile().equals(metadataRdf)) { 
				// Statements where about the RO itself
				manifest.setCreatedOn(pathMetadata.getCreatedOn());
				manifest.setCreatedBy(pathMetadata.getCreatedBy());
			}
			
			
			
		}
	}

	private static List<RDFNode> creatingAgentsFor(Resource r) {
		logger.fine("Finding creator of "  + r);
		String queryStr = sparqlPrefixes + 
				"SELECT ?agent WHERE { \n"
				+ " { \n"
				+ "  ?r dct:creator [ \n"
				+ "	    rdfs:member ?agent \n"
				+ "  ] \n"
				+ " } UNION { \n"
				+ "   ?r dct:creator ?agent .\n "
				+ "   FILTER NOT EXISTS { ?agent rdfs:member ?member } \n"
				+ " } \n"
				+ "} \n";
		logger.finer(QueryFactory.create(queryStr).toString());
		QueryExecution qexec = QueryExecutionFactory.create(queryStr, r.getModel());
		QuerySolutionMap binding = new QuerySolutionMap();
		binding.add("r", r);
		qexec.setInitialBinding(binding);
		ResultSet select = qexec.execSelect();
		List<RDFNode> agents = new ArrayList<>();
		
		while (select.hasNext()) {
			RDFNode agent = select.next().get("agent");
			logger.fine("Found: " + agent);
			agents.add(agent);
		}
		return agents;
	}
	
	private static String nameForAgent(Resource agentResource) {
		logger.fine("Finding name of "  + agentResource);
		String queryStr = sparqlPrefixes + 
			"SELECT ?name WHERE { \n"+ 
			"		{ ?agent foaf:name ?name } \n"+ 
			"	UNION  \n"+ 
			"		{ ?agent vcard:fn ?name } \n"+  
			"	UNION  \n"+ 
			"		{ ?agent vcard:FN ?name } \n"+ // legacy  
			"	UNION  \n"+ 
			"		{ ?agent rdfs:label ?name } \n"
			+ " UNION  \n"
			+ "     { \n"
			+ "         { ?agent vcard:n ?n } UNION { ?agent vcard:hasName ?n } \n"
			+ "         ?n vcard:family-name ?family ; \n"
			+ "            vcard:given-name ?given . \n"
			+ "          BIND(CONCAT(?given, \" \", ?family) AS ?name) \n"
			+ "     } \n"
			+ " UNION \n"
			+ "     { "
			+ "         ?agent foaf:givenName ?given ; \n"
			+ "                foaf:familyName ?family \n"
			+ "          BIND(CONCAT(?given, \" \", ?family) AS ?name) \n"
			+ "     } \n"  
			+ " UNION \n"
			+ "     { "
			+ "         ?agent foaf:firstName ?given ; \n"
			+ "                foaf:surname ?family \n"
			+ "          BIND(CONCAT(?given, \" \", ?family) AS ?name) \n"
			+ "     } \n"+  
			"	}  \n";
		logger.finer(QueryFactory.create(queryStr).toString());
		QueryExecution qexec = QueryExecutionFactory.create(queryStr, agentResource.getModel());
		QuerySolutionMap binding = new QuerySolutionMap();
		binding.add("agent", agentResource);
		qexec.setInitialBinding(binding);
		ResultSet select = qexec.execSelect();
		if (select.hasNext()) {
			String name = select.next().getLiteral("name").getString();			
			logger.fine(name);
			return name;
		}
		logger.fine("(null)");
		return null; 
	}

	private static Resource mboxForAgent(Resource agentResource) {
		logger.fine("Finding mbox of "  + agentResource);
		String queryStr = sparqlPrefixes + "SELECT ?mbox WHERE { \n"
				+ "		{ ?agent foaf:mbox ?mbox } \n" + "	UNION  \n"
				+ "		{ ?agent vcard:hasEmail ?mbox } \n" + "	UNION  \n"
				+ "		{ ?agent vcard:email ?email .  \n"
				+ "       BIND(IRI(CONCAT(\"mbox:\", ?email)) AS ?mbox) \n" // legacy
				+ "	    } \n"
				+ "} \n";
		logger.finer(QueryFactory.create(queryStr).toString());
		QueryExecution qexec = QueryExecutionFactory.create(queryStr, agentResource.getModel());
		QuerySolutionMap binding = new QuerySolutionMap();
		binding.add("agent", agentResource);
		qexec.setInitialBinding(binding);
		ResultSet select = qexec.execSelect();
		if (select.hasNext()) {
			Resource mbox = select.next().getResource("mbox");		
			logger.fine("Found mbox: " + mbox);
			return mbox;
		}
		logger.fine("mbox not found");
		return null; 
	}

	private static final String sparqlPrefixes = 			
			"PREFIX foaf:  <http://xmlns.com/foaf/0.1/> \n"+ 
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"+ 
			"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n" +
			"PREFIX dct:   <http://purl.org/dc/terms/> \n";

	
	private Collection<URI> bundleSubjects() throws IOException {
		Set<URI> subjects = new HashSet<>();
		subjects.add(bundle.getRoot().toUri());
		for (PathMetadata pathMetadata : manifest.getAggregates()) {
			subjects.add(pathMetadata.getUri());
			if (pathMetadata.getFile() != null) { 
				subjects.add(pathMetadata.getFile().toUri());
			}
			if (pathMetadata.getFolder() != null) { 
				subjects.add(pathMetadata.getFolder().toUri());
			}			
//			subjects.add(pathMetadata.getProxy());
		}
		for (PathAnnotation a : manifest.getAnnotations()) {
			subjects.add(a.getUri());
		}
		subjects.remove(null);
		return subjects;
	}

	private static Model parseRDF(Path metadata) throws IOException {
		Model model = ModelFactory.createDefaultModel();
		try (InputStream in = Files.newInputStream(metadata)) { 
			RDFDataMgr.read(model, in, metadata.toUri().toASCIIString(), RDFLanguages.RDFXML);
		}
		return model;
	}
	
}
