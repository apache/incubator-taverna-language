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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.taverna.ro.vocabs.bundle;
import org.apache.taverna.ro.vocabs.foaf;
import org.apache.taverna.ro.vocabs.oa;
import org.apache.taverna.ro.vocabs.ore;
import org.apache.taverna.ro.vocabs.pav;
import org.apache.taverna.ro.vocabs.prov;
import org.apache.taverna.ro.vocabs.ro;
import org.apache.taverna.robundle.Bundles;

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

	private static Logger logger = Logger.getLogger(RDFToManifest.class
			.getCanonicalName());

	private static <T> ClosableIterable<T> iterate(ExtendedIterator<T> iterator) {
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

	private Individual findRO(OntModel model, URI base) {
		try (ClosableIterable<? extends OntResource> instances = iterate(ore.Aggregation
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

			// Check for any ORCIDs, note that "orcid" is mapped as
			// prov:alternateOf in our modified bundle.jsonld
			for (Individual alternate : listObjectProperties(agent, prov.alternateOf)) {
				if (alternate.isURIResource() && (
						alternate.getURI().startsWith("https://orcid.org/") ||
						alternate.getURI().startsWith("http://orcid.org/"))) {
					// TODO: Check against https://support.orcid.org/knowledgebase/articles/116780-structure-of-the-orcid-identifier
					a.setOrcid(URI.create(alternate.getURI()));
					break;
				}
			}
			if (agent.isURIResource()) {
				URI agentURI = relativizeFromBase(agent.getURI(), base);
				if ("orcid.org".equals(agentURI.getHost()) && a.getOrcid() == null) {
					a.setOrcid(agentURI);
				} else {
					a.setUri(agentURI);
				}
			}

			RDFNode name = agent.getPropertyValue(foaf.name);
			if (name != null && name.isLiteral())
				a.setName(name.asLiteral().getLexicalForm());
			creators.add(a);
		}
		return creators;
	}

	protected OntModel getOntModel() {
		OntModel ontModel = createOntologyModel(OWL_DL_MEM_RULE_INF);
		ontModel.setNsPrefix("foaf", foaf.NS);
		ontModel.setNsPrefix("prov", prov.NS);
		ontModel.setNsPrefix("ore", ore.NS);
		ontModel.setNsPrefix("pav", pav.NS);
		ontModel.setNsPrefix("dct", DCTerms.NS);
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

	public void readTo(InputStream manifestResourceAsStream, Manifest manifest,
			URI manifestResourceBaseURI) throws IOException, RiotException {
		OntModel model = new RDFToManifest().getOntModel();
		model.add(jsonLdAsJenaModel(manifestResourceAsStream,
				manifestResourceBaseURI));

		URI root = manifestResourceBaseURI.resolve("/");
		Individual researchObj = findRO(model, root);
		if (researchObj == null)
			throw new IOException("root ResearchObject not found - "
					+ "Not a valid RO Bundle manifest");

		// isDescribedBy URI
		for (Individual manifestResource : listObjectProperties(researchObj,
				ore.isDescribedBy)) {
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
		List<Agent> creators = getAgents(root, researchObj, pav.createdBy);
		if (!creators.isEmpty()) {
			manifest.setCreatedBy(creators.get(0));
			if (creators.size() > 1) {
				logger.warning("Ignoring additional createdBy agents");
			}
		}

		// createdOn
		RDFNode created = researchObj.getPropertyValue(pav.createdOn);
		manifest.setCreatedOn(literalAsFileTime(created));

		// history
		List<Path> history = new ArrayList<Path> ();
		for (Individual histItem : listObjectProperties (researchObj, prov.AQ.has_provenance)) {
			history.add(Bundles.uriToBundlePath(manifest.getBundle(), relativizeFromBase(histItem.getURI(), root)));
		}
		manifest.setHistory(history);

		// authoredBy
		List<Agent> authors = getAgents(root, researchObj, pav.authoredBy);
		if (!authors.isEmpty()) {
			manifest.setAuthoredBy(authors);
		}

		// authoredOn
		RDFNode authored = researchObj.getPropertyValue(pav.authoredOn);
		manifest.setAuthoredOn(literalAsFileTime(authored));

		// retrievedFrom
		RDFNode retrievedNode = researchObj.getPropertyValue(pav.retrievedFrom);
		if (retrievedNode != null) {
    		try {
    			manifest.setRetrievedFrom(new URI(retrievedNode.asResource().getURI()));
    		} catch (URISyntaxException ex) {
    			logger.log(Level.WARNING, "Error creating URI for retrievedFrom: " +
    					retrievedNode.asResource().getURI(), ex);
    		}
		}

		// retrievedBy
		List<Agent> retrievers = getAgents(root, researchObj, pav.retrievedBy);
		if (!retrievers.isEmpty()) {
			manifest.setRetrievedBy(retrievers.get(0));
			if (retrievers.size() > 1) {
				logger.warning("Ignoring additional retrievedBy agents");
			}
		}

		// retrievedOn
		RDFNode retrieved = researchObj.getPropertyValue(pav.retrievedOn);
		manifest.setRetrievedOn(literalAsFileTime(retrieved));

		// conformsTo
		for (Individual standard : listObjectProperties(researchObj,
			 	ro.conformsTo)) {
			if (standard.isURIResource()) {
				URI uri;
				try {
					uri = new URI(standard.getURI());
				} catch (URISyntaxException ex) {
					logger.log(Level.WARNING, "Invalid URI for conformsTo: " +
					           standard, ex);
					continue;
				}
				if (! manifest.getConformsTo().contains(uri)) {
					manifest.getConformsTo().add(uri);
				}
			}
		}

		// Aggregates
		for (Individual aggrResource : listObjectProperties(researchObj, ore.aggregates)) {
			String uriStr = aggrResource.getURI();
			// PathMetadata meta = new PathMetadata();
			if (uriStr == null) {
				logger.warning("Skipping aggregation without URI: "
						+ aggrResource);
				continue;
			}

			PathMetadata meta = manifest.getAggregation(relativizeFromBase(
					uriStr, root));

			Set<Individual> proxies = listObjectProperties(aggrResource,
					bundle.hasProxy);
			if (proxies.isEmpty()) {
				// FIXME: Jena does not follow OWL properties paths from hasProxy
				proxies = listObjectProperties(aggrResource, bundle.bundledAs);
			}
			if (!proxies.isEmpty()) {
				// Should really only be one anyway
				Individual proxy = proxies.iterator().next();

				String proxyUri = null;
				if (proxy.getURI() != null) {
					proxyUri = proxy.getURI();
				} else if (proxy.getSameAs() != null) {
					proxyUri = proxy.getSameAs().getURI();
				}

				Proxy proxyInManifest = meta.getOrCreateBundledAs();
				if (proxyUri != null) {
					proxyInManifest.setURI(relativizeFromBase(proxyUri, root));
				}

				RDFNode eName = proxy.getPropertyValue(ro.entryName);
				if (eName != null && eName.isLiteral()) {
					proxyInManifest.setFilename(eName.asLiteral().getString());;
				}
				RDFNode folder = proxy.getPropertyValue(bundle.inFolder);
				if (folder != null && folder.isURIResource()) {
					URI folderUri = URI.create(folder.asResource().getURI());
					if (! folderUri.resolve("/").equals(manifest.getBaseURI())) {
						logger.warning("Invalid bundledAs folder, outside base URI of RO: " + folderUri);
						continue;
					}
					Path folderPath = Paths.get(folderUri);
					// Note: folder need NOT exist in zip file, so we don't need to do
					// Files.createDirectories(folderPath);
					proxyInManifest.setFolder(folderPath);
				}

			}

			// createdBy
			creators = getAgents(root, aggrResource, pav.createdBy);
			if (!creators.isEmpty()) {
				meta.setCreatedBy(creators.get(0));
				if (creators.size() > 1) {
					logger.warning("Ignoring additional createdBy agents for "
							+ meta);
				}
			}

			// createdOn
			meta.setCreatedOn(literalAsFileTime(aggrResource
					.getPropertyValue(pav.createdOn)));

			// retrievedFrom
			RDFNode retrievedAggrNode = aggrResource.getPropertyValue(pav.retrievedFrom);
			if (retrievedAggrNode != null) {
    			try {
    				meta.setRetrievedFrom(new URI(retrievedAggrNode.asResource().getURI()));
    			} catch (URISyntaxException ex) {
    				logger.log(Level.WARNING, "Error creating URI for retrievedFrom: " +
    						retrievedAggrNode.asResource().getURI(), ex);
    			}
			}

			// retrievedBy
			List<Agent> retrieversAggr = getAgents(root, aggrResource, pav.retrievedBy);
			if (!retrieversAggr.isEmpty()) {
				meta.setRetrievedBy(retrieversAggr.get(0));
				if (retrieversAggr.size() > 1) {
					logger.warning("Ignoring additional retrievedBy agents for "
							+ meta);
				}
			}

			// retrievedOn
			RDFNode retrievedAggr = aggrResource.getPropertyValue(pav.retrievedOn);
			meta.setRetrievedOn(literalAsFileTime(retrievedAggr));

			// conformsTo
			for (Individual standard : listObjectProperties(aggrResource,
					ro.conformsTo)) {
				if (standard.getURI() != null) {
					meta.setConformsTo(relativizeFromBase(standard.getURI(),
							root));
				}
			}

			// format
			RDFNode mediaType = aggrResource.getPropertyValue(DCTerms.format);
			if (mediaType != null && mediaType.isLiteral()) {
				meta.setMediatype(mediaType.asLiteral().getLexicalForm());
			}
		}

		for (Individual ann : listObjectProperties(researchObj, bundle.hasAnnotation)) {
			/*
			 * Normally just one body per annotation, but just in case we'll
			 * iterate and split them out (as our PathAnnotation can only keep a
			 * single setContent() at a time)
			 */
			for (Individual body : listObjectProperties(
					model.getOntResource(ann), oa.hasBody)) {
				if (! body.isURIResource()) {
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
				for (Individual target : listObjectProperties(ann, oa.hasTarget))
					if (target.getURI() != null)
						pathAnn.getAboutList().add(
								relativizeFromBase(target.getURI(), root));
				manifest.getAnnotations().add(pathAnn);
			}
		}
	}
}
