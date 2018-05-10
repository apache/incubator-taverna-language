package org.apache.taverna.robundle.manifest.combine;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.setLastModifiedTime;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.taverna.robundle.utils.RDFUtils.literalAsFileTime;

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
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.manifest.Agent;
import org.apache.taverna.robundle.manifest.PathAnnotation;
import org.apache.taverna.robundle.manifest.PathMetadata;
import org.apache.taverna.robundle.xml.combine.Content;
import org.apache.taverna.robundle.xml.combine.ObjectFactory;
import org.apache.taverna.robundle.xml.combine.OmexManifest;
import org.xml.sax.InputSource;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Parse and generate COMBINE Archive OMEX manifest
 *
 * @author Stian Soiland-Reyes
 */
public class CombineManifest {
	public static class ManifestNamespacePrefixMapperJAXB_RI extends
			NamespacePrefixMapper {
		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[] { OMEX_METADATA.toString() };
		}

		@Override
		public String getPreferredPrefix(String namespaceUri,
				String suggestion, boolean requirePrefix) {
			if (namespaceUri.equals(OMEX_METADATA.toString()))
				return "";
			return suggestion;
		}
	}

	private static final Logger logger = Logger.getLogger(CombineManifest.class
			.getCanonicalName());
	private static final String MANIFEST_XML = "manifest.xml";
	private static final String OMEX_MANIFEST = "http://identifiers.org/combine.specifications/omex-manifest";
		private static final URI OMEX_METADATA = URI
			.create("http://identifiers.org/combine.specifications/omex-metadata");
	private static final String sparqlPrefixes = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/> \n"
			+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
			+ "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "PREFIX dct:   <http://purl.org/dc/terms/> \n";

	private static JAXBContext jaxbContext;
	private static ObjectFactory objectFactory = new ObjectFactory();
	private static boolean warnedPrefixMapper;

	public static boolean containsManifest(Bundle bundle) {
		return isRegularFile(manifestXmlPath(bundle));
	}

	protected static synchronized Marshaller createMarshaller()
			throws JAXBException {
		Marshaller marshaller = getJaxbContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		setPrefixMapper(marshaller);
		return marshaller;
	}

	protected static synchronized Unmarshaller createUnMarshaller()
			throws JAXBException {
		Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
		return unmarshaller;
	}

	private static List<RDFNode> creatingAgentsFor(Resource r) {
		logger.fine("Finding creator of " + r);
		String queryStr = sparqlPrefixes + "SELECT ?agent WHERE { \n" + " { \n"
				+ "  ?r dct:creator [ \n" + "	    rdfs:member ?agent \n"
				+ "  ] \n" + " } UNION { \n" + "   ?r dct:creator ?agent .\n "
				+ "   FILTER NOT EXISTS { ?agent rdfs:member ?member } \n"
				+ " } \n" + "} \n";
		logger.finer(QueryFactory.create(queryStr).toString());
		QueryExecution qexec = QueryExecutionFactory.create(queryStr,
				r.getModel());
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

	protected static synchronized JAXBContext getJaxbContext()
			throws JAXBException {
		if (jaxbContext == null)
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		return jaxbContext;
	}

	private static Path manifestXmlPath(Bundle bundle) {
		return bundle.getRoot().resolve(MANIFEST_XML);
	}

	private static Resource mboxForAgent(Resource agentResource) {
		logger.fine("Finding mbox of " + agentResource);
		String queryStr = sparqlPrefixes + "SELECT ?mbox WHERE { \n"
				+ "		{ ?agent foaf:mbox ?mbox } \n" + "	UNION  \n"
				+ "		{ ?agent vcard:hasEmail ?mbox } \n" + "	UNION  \n"
				+ "		{ ?agent vcard:email ?email .  \n"
				+ "       BIND(IRI(CONCAT(\"mbox:\", ?email)) AS ?mbox) \n" // legacy
				+ "	    } \n" + "} \n";
		logger.finer(QueryFactory.create(queryStr).toString());
		QueryExecution qexec = QueryExecutionFactory.create(queryStr,
				agentResource.getModel());
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

	private static String nameForAgent(Resource agentResource) {
		logger.fine("Finding name of " + agentResource);
		String queryStr = sparqlPrefixes
				+ "SELECT ?name WHERE { \n"
				+ "		{ ?agent foaf:name ?name } \n"
				+ "	UNION  \n"
				+ "		{ ?agent vcard:fn ?name } \n"
				+ "	UNION  \n"
				+ "		{ ?agent vcard:FN ?name } \n"
				+ // legacy
				"	UNION  \n"
				+ "		{ ?agent rdfs:label ?name } \n"
				+ " UNION  \n"
				+ "     { \n"
				+ "         { ?agent vcard:n ?n } UNION { ?agent vcard:hasName ?n } \n"
				+ "         ?n vcard:family-name ?family ; \n"
				+ "            vcard:given-name ?given . \n"
				+ "          BIND(CONCAT(?given, \" \", ?family) AS ?name) \n"
				+ "     } \n" + " UNION \n" + "     { "
				+ "         ?agent foaf:givenName ?given ; \n"
				+ "                foaf:familyName ?family \n"
				+ "          BIND(CONCAT(?given, \" \", ?family) AS ?name) \n"
				+ "     } \n" + " UNION \n" + "     { "
				+ "         ?agent foaf:firstName ?given ; \n"
				+ "                foaf:surname ?family \n"
				+ "          BIND(CONCAT(?given, \" \", ?family) AS ?name) \n"
				+ "     } \n" + "	}  \n";
		logger.finer(QueryFactory.create(queryStr).toString());
		QueryExecution qexec = QueryExecutionFactory.create(queryStr,
				agentResource.getModel());
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

	private static Model parseRDF(Path metadata) throws IOException {
		Model model = createDefaultModel();
		try (InputStream in = newInputStream(metadata)) {
			RDFParser.create()
					.base(fakeFileURI(metadata))
					.lang(Lang.RDFXML)
					.source(in)
					// TAVERNA-1044 avoid bailing out on broken XML
					.errorHandler(ErrorHandlerFactory.errorHandlerWarn)
					.parse(model.getGraph());
		}
		//System.out.println("Parsed:");
		//model.write(System.out, "turtle");
		return model;
	}

	/**
	 * Convert Path's URI to a fake file:/// URI
	 * <p>
	 * TAVERNA-1027: Workaround for JENA-1462 - RO Bundle URIs like
	 * app://fad6e1b4-c0d1-45be-a978-7a570b62aa8d/manifest.xml can't be used as base
	 * URI when parsing RDF/XML in Jena 3.6.0 or earlier
	 */
	private static String fakeFileURI(Path path) {
		return fakeFileURI(path.toAbsolutePath().toUri());
	}

	private static String fakeFileURI(URI uri) {
		// Assume path starts with "/"
		return "file://" + uri.getPath();
	}

	protected static void setPrefixMapper(Marshaller marshaller) {
		boolean setPrefixMapper = false;

		try {
			/*
			 * This only works with JAXB RI, in which case we can set the
			 * namespace prefix mapper
			 */
			Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new ManifestNamespacePrefixMapperJAXB_RI());
			/*
			 * Note: A similar mapper for the built-in java
			 * (com.sun.xml.bind.internal.namespacePrefixMapper) is no longer
			 * included here, as it will not (easily) compile with Maven.
			 */
			setPrefixMapper = true;
		} catch (Exception e) {
			logger.log(FINE, "Can't find NamespacePrefixMapper", e);
		}

		if (!setPrefixMapper && !warnedPrefixMapper) {
			logger.info("Could not set prefix mapper (missing or incompatible JAXB) "
					+ "- will use prefixes ns0, ns1, ..");
			warnedPrefixMapper = true;
		}
	}

	private Bundle bundle;

	private org.apache.taverna.robundle.manifest.Manifest manifest;

	public CombineManifest(org.apache.taverna.robundle.manifest.Manifest manifest) {
		this.manifest = manifest;
		this.bundle = manifest.getBundle();
	}

	@SuppressWarnings("deprecation")
	private Collection<URI> bundleSubjects() throws IOException {
		Set<URI> subjects = new HashSet<>();
		subjects.add(bundle.getRoot().toUri());
		for (PathMetadata pathMetadata : manifest.getAggregates()) {
			subjects.add(pathMetadata.getUri());
			if (pathMetadata.getFile() != null)
				subjects.add(pathMetadata.getFile().toUri());
			if (pathMetadata.getFolder() != null)
				subjects.add(pathMetadata.getFolder().toUri());
			// subjects.add(pathMetadata.getProxy());
		}
		for (PathAnnotation a : manifest.getAnnotations())
			subjects.add(a.getUri());
		subjects.remove(null);
		return subjects;
	}

	public Path createManifestXML() throws IOException {
		OmexManifest omexManifest = makeOmexManifest();

		Path manifestXml = manifestXmlPath(bundle);
		createDirectories(manifestXml.getParent());
		try (OutputStream outStream = newOutputStream(manifestXml)) {
			try {
				createMarshaller().marshal(omexManifest, outStream);
			} catch (JAXBException e) {
				throw new RuntimeException("Could not serialize OMEX Manifest",
						e);
			}
		}
		return manifestXml;
	}

	private void findAnnotations() throws IOException {
		Path metadataRdf = null;
		for (PathMetadata agg : manifest.getAggregates())
			if (OMEX_METADATA.equals(agg.getConformsTo())) {
				metadataRdf = agg.getFile();
				break; // TODO: Support not just the first one
				// TODO: support external metadata with agg.getUri() ?
			}
		if (metadataRdf == null)
			// fallback to hard-coded filename
			metadataRdf = bundle.getRoot().resolve("metadata.rdf");
		if (!exists(metadataRdf))
			return;

		Model metadata;
		try {
			metadata = parseRDF(metadataRdf);
		} catch (IOException e) {
			logger.log(WARNING, "Can't read " + metadataRdf, e);
			return;
		} catch (RiotException e) {
			logger.log(WARNING, "Can't parse " + metadataRdf, e);
			return;
		}

		Set<Pair<URI,URI>> foundAnnotations = new HashSet<>();
		for (URI subject : bundleSubjects()) {
			Resource resource = metadata.getResource(fakeFileURI(subject));
			if (!metadata.containsResource(resource)) {
				// No metadata about that resource, probably OK, but
				// could be an absolute/relative path issue
				logger.info("No metadata.rdf triples found about " + resource);
				continue;
			}

			URI about = manifest.relativeToBundleRoot(subject);
			URI content = manifest.relativeToBundleRoot(metadataRdf.toUri());
			if (! foundAnnotations.add(Pair.of(about, content))) {
				// Avoid duplication
				PathAnnotation ann = new PathAnnotation();
				ann.setAbout(subject);
				ann.setContent(content);
				manifest.getAnnotations().add(ann);
			}

			// Extract information that could be in our manifest
			PathMetadata pathMetadata = manifest.getAggregation(subject);

			// Created date. We'll prefer dcModified.
			Property dcCreated = metadata
					.getProperty("http://purl.org/dc/terms/created");
			Property dcModified = metadata
					.getProperty("http://purl.org/dc/terms/modified");
			Statement createdSt = resource.getProperty(dcModified);
			if (createdSt == null)
				createdSt = resource.getProperty(dcCreated);
			if (createdSt != null) {
				FileTime fileTime = literalAsFileTime(createdSt.getObject());
				if (fileTime != null) {
					pathMetadata.setCreatedOn(fileTime);
					if (pathMetadata.getFile() != null)
						setLastModifiedTime(pathMetadata.getFile(),
								fileTime);
				}
			}

			// add the COMBINE "creators" as RO "authors"
			List<Agent> authors = pathMetadata.getAuthoredBy ();

			for (RDFNode s : creatingAgentsFor(resource)) {
				if (authors == null)
				{
					authors = new ArrayList<Agent> ();
					pathMetadata.setAuthoredBy (authors);
				}

				if (s.isLiteral()) {
					authors.add (new Agent(s.asLiteral()
							.getLexicalForm()));
					continue;
				}
				Resource agentResource = s.asResource();
				Agent agent = new Agent();
				if (agentResource.isURIResource()) {
					URI agentUri = URI.create(agentResource.getURI());
					if (agentResource.getURI().startsWith("http://orcid.org/"))
						agent.setOrcid(agentUri);
					else
						agent.setUri(agentUri);
				} else {
					Resource mbox = mboxForAgent(agentResource);
					if (mbox != null && mbox.isURIResource())
						agent.setUri(URI.create(mbox.getURI()));
				}
				agent.setName(nameForAgent(agentResource));
				authors.add (agent);
			}
			// if there is a single COMBINE "creator" it is also the RO "creator"
			if (authors != null && authors.size () == 1)
				pathMetadata.setCreatedBy (authors.get (0));

			if (pathMetadata.getFile().equals(bundle.getRoot())
					|| pathMetadata.getFile().equals(metadataRdf)) {
				// Statements where about the RO itself
				manifest.setCreatedOn(pathMetadata.getCreatedOn());
				manifest.setCreatedBy(pathMetadata.getCreatedBy());
			}
		}
	}

	private OmexManifest makeOmexManifest() {
		Path manifestXml = bundle.getRoot().resolve("manifest.xml");
		OmexManifest omexManifest = objectFactory.createOmexManifest();

		PathMetadata aggr = manifest.getAggregation(manifestXml);
		if (aggr.getConformsTo() == null) {
			// Add the manifest itself
			aggr.setConformsTo(URI.create(OMEX_MANIFEST));
		}

		for (PathMetadata metadata : manifest.getAggregates()) {
			Content content = objectFactory.createContent();
			Path file = metadata.getFile();

			if (file == null) {
				content.setLocation(metadata.getUri().toString());
			} else {
				Path relPath = bundle.getRoot().relativize(file);
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

	public void readCombineArchive() throws IOException {
		readManifestXML();
		findAnnotations();

	}

	public void readManifestXML() throws IOException {
		Path manifestXml = manifestXmlPath(bundle);
		OmexManifest omexManifest;
		try (InputStream inStream = newInputStream(manifestXml)) {
			InputSource src = new InputSource(inStream);
			Source source = new SAXSource(src);
			omexManifest = createUnMarshaller().unmarshal(source,
					OmexManifest.class).getValue();
			// omexManifest = (OmexManifest) createUnMarshaller().unmarshal(inStream);
		} catch (JAXBException | ClassCastException e) {
			// logger.warning("Could not parse " + manifestXml);
			throw new IOException("Could not parse " + manifestXml, e);
		}
		if (!manifest.getManifest().contains(manifestXml))
			manifest.getManifest().add(manifestXml);

		for (Content c : omexManifest.getContent()) {
			PathMetadata metadata;
			if (c.getLocation().contains(":")) {
				try {
					URI uri = new URI(c.getLocation());
					if (!uri.isAbsolute()) {
						logger.warning("Not an absolute URI, but contains :"
								+ c.getLocation());
						continue;
					}
					metadata = manifest.getAggregation(uri);
				} catch (URISyntaxException e) {
					logger.warning("Invalid URI " + c.getLocation());
					continue;
				}
			} else {
				Path path = bundle.getRoot().resolve(c.getLocation());
				if (Files.isSameFile(bundle.getRoot(), path)) {
					// metadata about the archive itself
					if (c.getFormat() != null && ! c.getFormat().isEmpty()) {
						URI uri;
						try {
							uri = new URI(c.getFormat());
						} catch (URISyntaxException e) {
							logger.warning(MANIFEST_XML + " non-URI format for . expected http://identifiers.org/combine.specifications/omex");
							continue;
						}
						if (! manifest.getConformsTo().contains(uri)) {
							manifest.getConformsTo().add(uri);
						}
					}
					// Don't add / to the list of aggregations in RO,
					// as / is the RO itself!
					continue;
				}

				if (!exists(path)) {
					logger.warning(MANIFEST_XML + " listed relative path "
							+ path + ", but it does not exist in bundle");
					continue;
				}
				metadata = manifest.getAggregation(path);
			}

			// Format - is it an URI or media type?
			if (c.getFormat().contains(":")) {
				metadata.setConformsTo(URI.create(c.getFormat()));
			} else if (!c.getFormat().isEmpty()) {
				metadata.setMediatype(c.getFormat());
			} else if (metadata.getFile() != null) {
				metadata.setMediatype(manifest.guessMediaType(metadata
						.getFile()));
			} // else: Not needed for URIs
		}
	}

}
