package org.apache.taverna.robundle.manifest;

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


import static org.apache.jena.ontology.OntModelSpec.OWL_DL_MEM_RULE_INF;
import static org.apache.jena.rdf.model.ModelFactory.createOntologyModel;
import static org.apache.taverna.robundle.utils.PathHelper.relativizeFromBase;
import static org.apache.taverna.robundle.utils.RDFUtils.literalAsFileTime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.taverna.robundle.Bundles;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;

public class RDFToManifest {
	public static class ClosableIterable<T> implements AutoCloseable,
			Iterable<T> {

		private ExtendedIterator<T> iterator;

		public ClosableIterable(ExtendedIterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public void close() {
			iterator.close();
		}

		@Override
		public ExtendedIterator<T> iterator() {
			return iterator;
		}
	}

	private static final String BUNDLE = "http://purl.org/wf4ever/bundle#";

	private static final String BUNDLE_RDF = "/ontologies/bundle.owl";
	private static final String DCT = "http://purl.org/dc/terms/";
	private static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";
	private static final String FOAF_RDF = "/ontologies/foaf.rdf";

	private static Logger logger = Logger.getLogger(RDFToManifest.class
			.getCanonicalName());
	private static final String OA = "http://www.w3.org/ns/oa#";
	private static final String OA_RDF = "/ontologies/oa.rdf";
	private static final String ORE = "http://www.openarchives.org/ore/terms/";
	private static final String PAV = "http://purl.org/pav/";
	private static final String PAV_RDF = "/ontologies/pav.rdf";
	private static final String PROV = "http://www.w3.org/ns/prov#";
	private static final String PROV_AQ_RDF = "/ontologies/prov-aq.rdf";
	private static final String PROV_O = "http://www.w3.org/ns/prov-o#";
	private static final String PROV_O_RDF = "/ontologies/prov-o.rdf";
	@SuppressWarnings("unused")
	private static final String RO = "http://purl.org/wf4ever/ro#";

	public static <T> ClosableIterable<T> iterate(ExtendedIterator<T> iterator) {
		return new ClosableIterable<T>(iterator);
	}

	protected static Model jsonLdAsJenaModel(InputStream jsonIn, URI base)
			throws IOException, RiotException {
		Model model = ModelFactory.createDefaultModel();
		
		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		try { 
			// TAVERNA-971: set context classloader so jarcache.json is consulted
			// even through OSGi
			Thread.currentThread().setContextClassLoader(RDFToManifest.class.getClassLoader());
			
			// Now we can parse the JSON-LD without network access
			RDFDataMgr.read(model, jsonIn, base.toASCIIString(), Lang.JSONLD);
		} finally { 
			// Restore old context class loader (if any)
			Thread.currentThread().setContextClassLoader(oldCl);
		}
		return model;
	}

	protected static URI makeBaseURI() throws URISyntaxException {
		return new URI("app", UUID.randomUUID().toString(), "/", (String) null);
	}

	private ObjectProperty aggregates;
	private OntClass aggregation;
	private ObjectProperty authoredBy;
	private DatatypeProperty authoredOn;
	private OntModel bundle;
	private ObjectProperty conformsTo;
	private ObjectProperty createdBy;
	private DatatypeProperty createdOn;
	private ObjectProperty retrievedFrom;
	private ObjectProperty retrievedBy;
	private DatatypeProperty retrievedOn;
	private OntModel dct;
	private OntModel foaf;
	private DatatypeProperty foafName;
	private DatatypeProperty format;
	private ObjectProperty hasAnnotation;

	private ObjectProperty hasBody;

	private ObjectProperty hasProvenance;

	private ObjectProperty hasProxy;

	private ObjectProperty hasTarget;
	private ObjectProperty inFolder;
	private ObjectProperty isDescribedBy;
	private OntModel oa;
	private OntModel ore;
	private OntModel pav;

	private OntModel prov;

	private OntModel provaq;

	private ObjectProperty proxyFor;

	private ObjectProperty proxyIn;

	private OntClass standard;

	public RDFToManifest() {
		loadOntologies();
	}

	private void checkNotNull(Object... possiblyNulls) {
		int i = 0;
		for (Object check : possiblyNulls) {
			if (check == null)
				throw new IllegalStateException("Could not load item #" + i);
			i++;
		}
	}

	private Individual findRO(OntModel model, URI base) {
		try (ClosableIterable<? extends OntResource> instances = iterate(aggregation
				.listInstances())) {
			for (OntResource o : instances)
				// System.out.println("Woo " + o);
				return o.asIndividual();
		}
		// Fallback - resolve as "/"
		// TODO: Ensure it's an Aggregation?
		return model.getIndividual(base.toString());
	}

	private List<Agent> getAgents(URI base, Individual in,
			ObjectProperty property) {
		List<Agent> creators = new ArrayList<>();
		for (Individual agent : listObjectProperties(in, property)) {
			Agent a = new Agent();
			if (agent.getURI() != null)
				a.setUri(relativizeFromBase(agent.getURI(), base));

			RDFNode name = agent.getPropertyValue(foafName);
			if (name != null && name.isLiteral())
				a.setName(name.asLiteral().getLexicalForm());
			creators.add(a);
		}
		return creators;
	}

	protected OntModel getOntModel() {
		OntModel ontModel = createOntologyModel(OWL_DL_MEM_RULE_INF);
		ontModel.setNsPrefix("foaf", FOAF_0_1);
		ontModel.setNsPrefix("prov", PROV);
		ontModel.setNsPrefix("ore", ORE);
		ontModel.setNsPrefix("pav", PAV);
		ontModel.setNsPrefix("dct", DCT);
		// ontModel.getDocumentManager().loadImports(foaf.getOntModel());
		return ontModel;
	}

	private Set<Individual> listObjectProperties(OntResource ontResource,
			ObjectProperty prop) {
		LinkedHashSet<Individual> results = new LinkedHashSet<>();
		try (ClosableIterable<RDFNode> props = iterate(ontResource
				.listPropertyValues(prop))) {
			for (RDFNode node : props) {
				if (!node.isResource() || !node.canAs(Individual.class))
					continue;
				results.add(node.as(Individual.class));
			}
		}
		return results;
	}

	protected synchronized void loadBundle() {
		if (bundle != null)
			return;
		OntModel ontModel = loadOntologyFromClasspath(BUNDLE_RDF, BUNDLE);
		hasProxy = ontModel.getObjectProperty(BUNDLE + "hasProxy");
		hasAnnotation = ontModel.getObjectProperty(BUNDLE + "hasAnnotation");
		inFolder = ontModel.getObjectProperty(BUNDLE + "inFolder");
		checkNotNull(hasProxy, hasAnnotation, inFolder);
		bundle = ontModel;
	}

	protected synchronized void loadDCT() {
		if (dct != null)
			return;

		OntModel ontModel = loadOntologyFromClasspath(
				"/ontologies/dcterms_od.owl",
				"http://purl.org/wf4ever/dcterms_od");

		// properties from dct
		standard = ontModel.getOntClass(DCT + "Standard");
		conformsTo = ontModel.getObjectProperty(DCT + "conformsTo");

		// We'll cheat dc:format in
		format = ontModel
				.createDatatypeProperty("http://purl.org/dc/elements/1.1/"
						+ "format");
		checkNotNull(standard, conformsTo, format);

		dct = ontModel;
	}

	//
	protected synchronized void loadFOAF() {
		if (foaf != null)
			return;

		OntModel ontModel = loadOntologyFromClasspath(FOAF_RDF, FOAF_0_1);

		// properties from foaf
		foafName = ontModel.getDatatypeProperty(FOAF_0_1 + "name");
		checkNotNull(foafName);

		foaf = ontModel;
	}

	protected synchronized void loadOA() {
		if (oa != null)
			return;
		OntModel ontModel = loadOntologyFromClasspath(OA_RDF, OA);
		hasTarget = ontModel.getObjectProperty(OA + "hasTarget");
		hasBody = ontModel.getObjectProperty(OA + "hasBody");
		checkNotNull(hasTarget, hasBody);
		oa = ontModel;
	}

	protected void loadOntologies() {
		loadDCT();
		loadORE();
		loadFOAF();
		loadPROVO();
		loadPAV();
		loadPROVAQ();
		loadOA();
		loadBundle();
	}

	protected OntModel loadOntologyFromClasspath(String classPathUri, String uri) {
		OntModel ontModel = createOntologyModel();

		// Load from classpath
		InputStream inStream = getClass().getResourceAsStream(classPathUri);
		if (inStream == null)
			throw new IllegalArgumentException("Can't load " + classPathUri);
		// Ontology ontology = ontModel.createOntology(uri);
		ontModel.read(inStream, uri);
		try {
			inStream.close();
		} catch (IOException e) {
			// Shouldn't happen
		}
		return ontModel;
	}

	protected synchronized void loadORE() {
		if (ore != null)
			return;
		OntModel ontModel = loadOntologyFromClasspath(
				"/ontologies/ore-owl.owl", "http://purl.org/wf4ever/ore-owl");
		aggregation = ontModel.getOntClass(ORE + "Aggregation");

		aggregates = ontModel.getObjectProperty(ORE + "aggregates");
		proxyFor = ontModel.getObjectProperty(ORE + "proxyFor");
		proxyIn = ontModel.getObjectProperty(ORE + "proxyIn");
		isDescribedBy = ontModel.getObjectProperty(ORE + "isDescribedBy");

		checkNotNull(aggregation, aggregates, proxyFor, proxyIn, isDescribedBy);

		ore = ontModel;
	}

	protected synchronized void loadPAV() {
		if (pav != null)
			return;

		OntModel ontModel = loadOntologyFromClasspath(PAV_RDF, PAV);
		// properties from foaf
		createdBy = ontModel.getObjectProperty(PAV + "createdBy");
		createdOn = ontModel.getDatatypeProperty(PAV + "createdOn");
		authoredBy = ontModel.getObjectProperty(PAV + "authoredBy");
		authoredOn = ontModel.getDatatypeProperty(PAV + "authoredOn");
		retrievedFrom = ontModel.getObjectProperty(PAV + "retrievedFrom");
		retrievedBy = ontModel.getObjectProperty(PAV + "retrievedBy");
		retrievedOn = ontModel.getDatatypeProperty(PAV + "retrievedOn");

		checkNotNull(createdBy, createdOn, authoredBy, authoredOn,
				retrievedFrom, retrievedBy, retrievedOn);

		pav = ontModel;
	}

	protected synchronized void loadPROVAQ() {
		if (provaq != null)
			return;
		OntModel ontModel = loadOntologyFromClasspath(PROV_AQ_RDF, PAV);

		// properties from foaf
		hasProvenance = ontModel.getObjectProperty(PROV + "has_provenance");
		checkNotNull(hasProvenance);

		provaq = ontModel;
	}

	protected synchronized void loadPROVO() {
		if (prov != null)
			return;
		OntModel ontModel = loadOntologyFromClasspath(PROV_O_RDF, PROV_O);

		checkNotNull(ontModel);

		prov = ontModel;
	}

	@SuppressWarnings("deprecation")
	private static void setPathProxy(PathMetadata meta, URI proxy) {
		meta.setProxy(proxy);
	}

	public void readTo(InputStream manifestResourceAsStream, Manifest manifest,
			URI manifestResourceBaseURI) throws IOException, RiotException {
		OntModel model = new RDFToManifest().getOntModel();
		model.add(jsonLdAsJenaModel(manifestResourceAsStream,
				manifestResourceBaseURI));

		// model.write(System.out, "TURTLE");
		// System.out.println();

		URI root = manifestResourceBaseURI.resolve("/");
		Individual ro = findRO(model, root);
		if (ro == null)
			throw new IOException("root ResearchObject not found - "
					+ "Not a valid RO Bundle manifest");

		// isDescribedBy URI
		for (Individual manifestResource : listObjectProperties(ro,
				isDescribedBy)) {
			String uriStr = manifestResource.getURI();
			if (uriStr == null) {
				logger.warning("Skipping manifest without URI: "
						+ manifestResource);
				continue;
			}
			// URI relative = relativizeFromBase(uriStr, root);
			Path path = manifest.getBundle().getFileSystem().provider()
					.getPath(URI.create(uriStr));
			manifest.getManifest().add(path);
		}

		// createdBy
		List<Agent> creators = getAgents(root, ro, createdBy);
		if (!creators.isEmpty()) {
			manifest.setCreatedBy(creators.get(0));
			if (creators.size() > 1) {
				logger.warning("Ignoring additional createdBy agents");
			}
		}

		// createdOn
		RDFNode created = ro.getPropertyValue(createdOn);
		manifest.setCreatedOn(literalAsFileTime(created));
		
		// history
		List<Path> history = new ArrayList<Path> ();
		for (Individual histItem : listObjectProperties (ro, hasProvenance)) {
			history.add(Bundles.uriToBundlePath(manifest.getBundle(), relativizeFromBase(histItem.getURI(), root)));
		}
		manifest.setHistory(history);
		
		// authoredBy
		List<Agent> authors = getAgents(root, ro, authoredBy);
		if (!authors.isEmpty()) {
			manifest.setAuthoredBy(authors);
		}

		// authoredOn
		RDFNode authored = ro.getPropertyValue(authoredOn);
		manifest.setAuthoredOn(literalAsFileTime(authored));

		// retrievedFrom
		RDFNode retrievedNode = ro.getPropertyValue(retrievedFrom);
		manifest.setRetrievedFrom(retrievedNode);

		// retrievedBy
		List<Agent> retrievers = getAgents(root, ro, retrievedBy);
		if (!retrievers.isEmpty()) {
			manifest.setRetrievedBy(retrievers.get(0));
			if (retrievers.size() > 1) {
				logger.warning("Ignoring additional retrievedBy agents");
			}
		}

		// retrievedOn
		RDFNode retrieved = ro.getPropertyValue(retrievedOn);
		manifest.setRetrievedOn(literalAsFileTime(retrieved));

		// Aggregates
		for (Individual aggrResource : listObjectProperties(ro, aggregates)) {
			String uriStr = aggrResource.getURI();
			// PathMetadata meta = new PathMetadata();
			if (uriStr == null) {
				logger.warning("Skipping aggregation without URI: "
						+ aggrResource);
				continue;
			}

			PathMetadata meta = manifest.getAggregation(relativizeFromBase(
					uriStr, root));

			// hasProxy
			Set<Individual> proxies = listObjectProperties(aggrResource,
					hasProxy);
			if (!proxies.isEmpty()) {
				// We can only deal with the first one
				Individual proxy = proxies.iterator().next();

				String proxyUri = null;
				if (proxy.getURI() != null) {
					proxyUri = proxy.getURI();
				} else if (proxy.getSameAs() != null) {
					proxyUri = proxy.getSameAs().getURI();
				}

				if (proxyUri != null) {
					setPathProxy(meta, relativizeFromBase(proxyUri, root));
				}
			}

			// createdBy
			creators = getAgents(root, aggrResource, createdBy);
			if (!creators.isEmpty()) {
				meta.setCreatedBy(creators.get(0));
				if (creators.size() > 1) {
					logger.warning("Ignoring additional createdBy agents for "
							+ meta);
				}
			}

			// createdOn
			meta.setCreatedOn(literalAsFileTime(aggrResource
					.getPropertyValue(createdOn)));

			// retrievedFrom
			RDFNode retrievedAggrNode = aggrResource.getPropertyValue(retrievedFrom);
			meta.setRetrievedFrom(retrievedAggrNode);

			// retrievedBy
			List<Agent> retrieversAggr = getAgents(root, aggrResource, retrievedBy);
			if (!retrieversAggr.isEmpty()) {
				meta.setRetrievedBy(retrieversAggr.get(0));
				if (retrieversAggr.size() > 1) {
					logger.warning("Ignoring additional retrievedBy agents for "
							+ meta);
				}
			}

			// retrievedOn
			RDFNode retrievedAggr = aggrResource.getPropertyValue(retrievedOn);
			meta.setRetrievedOn(literalAsFileTime(retrievedAggr));

			// conformsTo
			for (Individual standard : listObjectProperties(aggrResource,
					conformsTo)) {
				if (standard.getURI() != null) {
					meta.setConformsTo(relativizeFromBase(standard.getURI(),
							root));
				}
			}

			// format
			RDFNode mediaType = aggrResource.getPropertyValue(format);
			if (mediaType != null && mediaType.isLiteral()) {
				meta.setMediatype(mediaType.asLiteral().getLexicalForm());
			}
		}

		for (Individual ann : listObjectProperties(ro, hasAnnotation)) {
			/*
			 * Normally just one body per annotation, but just in case we'll
			 * iterate and split them out (as our PathAnnotation can only keep a
			 * single setContent() at a time)
			 */
			for (Individual body : listObjectProperties(
					model.getOntResource(ann), hasBody)) {
				if (body.getURI() == null) {
					logger.warning("Can't find annotation body for anonymous "
							+ body);
					continue;
				}
				PathAnnotation pathAnn = new PathAnnotation();
				pathAnn.setContent(relativizeFromBase(body.getURI(), root));

				if (ann.getURI() != null)
					pathAnn.setUri(relativizeFromBase(ann.getURI(), root));
				else if (ann.getSameAs() != null
						&& ann.getSameAs().getURI() != null)
					pathAnn.setUri(relativizeFromBase(ann.getSameAs().getURI(),
							root));

				// Handle multiple about/hasTarget
				for (Individual target : listObjectProperties(ann, hasTarget))
					if (target.getURI() != null)
						pathAnn.getAboutList().add(
								relativizeFromBase(target.getURI(), root));
				manifest.getAnnotations().add(pathAnn);
			}
		}
	}
}
