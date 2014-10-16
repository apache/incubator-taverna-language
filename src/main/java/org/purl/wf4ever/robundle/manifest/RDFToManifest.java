package org.purl.wf4ever.robundle.manifest;

import static org.purl.wf4ever.robundle.utils.PathHelper.relativizeFromBase;
import static org.purl.wf4ever.robundle.utils.RDFUtils.literalAsFileTime;

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

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;

import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class RDFToManifest {
	
	private static Logger logger = Logger.getLogger(RDFToManifest.class
			.getCanonicalName());

	static {
		setCachedHttpClientInJsonLD();
	}
	
	private static final String PROV = "http://www.w3.org/ns/prov#";
	private static final String PROV_O = "http://www.w3.org/ns/prov-o#";
	private static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";
	private static final String PAV = "http://purl.org/pav/";

	private static final String DCT = "http://purl.org/dc/terms/";
	private static final String RO = "http://purl.org/wf4ever/ro#";
	private static final String BUNDLE = "http://purl.org/wf4ever/bundle#";
	private static final String ORE = "http://www.openarchives.org/ore/terms/";
	private static final String OA = "http://www.w3.org/ns/oa#";
	private static final String OA_RDF = "/ontologies/oa.rdf";
	private static final String FOAF_RDF = "/ontologies/foaf.rdf";
	private static final String BUNDLE_RDF = "/ontologies/bundle.owl";	
	private static final String PAV_RDF = "/ontologies/pav.rdf";
	private static final String PROV_O_RDF = "/ontologies/prov-o.rdf";
	private static final String PROV_AQ_RDF = "/ontologies/prov-aq.rdf";
	private OntModel ore;
	private ObjectProperty aggregates;
	private ObjectProperty proxyFor;
	private ObjectProperty proxyIn;

	private OntClass aggregation;
	private OntModel foaf;
	private DatatypeProperty foafName;
	private OntModel pav;
	private ObjectProperty createdBy;
	private OntModel prov;
	private OntModel provaq;
	private ObjectProperty hasProvenance;
	private OntModel dct;
	private ObjectProperty conformsTo;
	private OntClass standard;
	private ObjectProperty authoredBy;
	private DatatypeProperty createdOn;
	private DatatypeProperty authoredOn;

	private DatatypeProperty format;

	private OntModel oa;

	private ObjectProperty hasBody;

	private ObjectProperty hasTarget;
	private ObjectProperty isDescribedBy;
	private OntModel bundle;
	private ObjectProperty hasProxy;
	private ObjectProperty inFolder;
	private ObjectProperty hasAnnotation;

	public RDFToManifest() {
		loadOntologies();
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
		OntModel ontModel = ModelFactory.createOntologyModel();

		// Load from classpath
		InputStream inStream = getClass().getResourceAsStream(classPathUri);
		if (inStream == null) {
			throw new IllegalArgumentException("Can't load " + classPathUri);
		}
		// Ontology ontology = ontModel.createOntology(uri);
		ontModel.read(inStream, uri);
		return ontModel;
	}

	
	
	protected static Model jsonLdAsJenaModel(InputStream jsonIn, URI base)
			throws IOException, RiotException {
		JenaJSONLD.init();
		Model model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(model, jsonIn, base.toASCIIString(), JenaJSONLD.JSONLD);
		return model;

		//
		// Object input = JSONUtils.fromInputStream(jsonIn);
		// JSONLDTripleCallback callback = new JenaTripleCallback();
		// Model model = (Model)JSONLD.toRDF(input, callback, new
		// Options(base.toASCIIString()));
		// return model;
	}
	
	/**
	 * Use a JarCacheStorage so that our JSON-LD @context can be loaded from our
	 * classpath and not require network connectivity
	 * 
	 */
	protected static void setCachedHttpClientInJsonLD() {
//		JarCacheStorage cacheStorage = new JarCacheStorage(
//				RDFToManifest.class.getClassLoader());
//		synchronized (DocumentLoader.class) {
//			HttpClient oldHttpClient = DocumentLoader.getHttpClient();
//			CachingHttpClient wrappedHttpClient = new CachingHttpClient(
//					oldHttpClient, cacheStorage, cacheStorage.getCacheConfig());
//			DocumentLoader.setHttpClient(wrappedHttpClient);
//		}
//		synchronized (JSONUtils.class) {
//			HttpClient oldHttpClient = JSONUtilsSub.getHttpClient();
//			CachingHttpClient wrappedHttpClient = new CachingHttpClient(
//					oldHttpClient, cacheStorage, cacheStorage.getCacheConfig());
//			JSONUtilsSub.setHttpClient(wrappedHttpClient);
//		}
	}

	private void checkNotNull(Object... possiblyNulls) {
		int i = 0;
		for (Object check : possiblyNulls) {
			if (check == null) {
				throw new IllegalStateException("Could not load item #" + i);
			}
			i++;
		}

	}

	protected OntModel getOntModel() {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontModel.setNsPrefix("foaf", FOAF_0_1);
		ontModel.setNsPrefix("prov", PROV);
		ontModel.setNsPrefix("ore", ORE);
		ontModel.setNsPrefix("pav", PAV);
		ontModel.setNsPrefix("dct", DCT);
		// ontModel.getDocumentManager().loadImports(foaf.getOntModel());
		return ontModel;
	}

	//
	protected synchronized void loadFOAF() {
		if (foaf != null) {
			return;
		}

		OntModel ontModel = loadOntologyFromClasspath(FOAF_RDF, FOAF_0_1);

		// properties from foaf
		foafName = ontModel.getDatatypeProperty(FOAF_0_1 + "name");
		checkNotNull(foafName);

		foaf = ontModel;
	}

	protected synchronized void loadPAV() {
		if (pav != null) {
			return;
		}

		OntModel ontModel = loadOntologyFromClasspath(PAV_RDF, PAV);
		// properties from foaf
		createdBy = ontModel.getObjectProperty(PAV + "createdBy");
		createdOn = ontModel.getDatatypeProperty(PAV + "createdOn");
		authoredBy = ontModel.getObjectProperty(PAV + "authoredBy");
		authoredOn = ontModel.getDatatypeProperty(PAV + "authoredOn");
		checkNotNull(createdBy, createdOn, authoredBy, authoredOn);

		pav = ontModel;
	}

	protected synchronized void loadPROVO() {
		if (prov != null) {
			return;
		}
		OntModel ontModel = loadOntologyFromClasspath(PROV_O_RDF, PROV_O);

		checkNotNull(ontModel);

		prov = ontModel;
	}

	protected synchronized void loadPROVAQ() {
		if (provaq != null) {
			return;
		}
		OntModel ontModel = loadOntologyFromClasspath(PROV_AQ_RDF, PAV);

		// properties from foaf
		hasProvenance = ontModel.getObjectProperty(PROV + "has_provenance");
		checkNotNull(hasProvenance);

		provaq = ontModel;
	}

	protected synchronized void loadDCT() {
		if (dct != null) {
			return;
		}

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

	protected synchronized void loadOA() {
		if (oa != null) {
			return;
		}
		OntModel ontModel = loadOntologyFromClasspath(OA_RDF, OA);
		hasTarget = ontModel.getObjectProperty(OA + "hasTarget");
		hasBody = ontModel.getObjectProperty(OA + "hasBody");
		checkNotNull(hasTarget, hasBody);
		oa = ontModel;
	}

	protected synchronized void loadBundle() {
		if (bundle != null) {
			return;
		}
		OntModel ontModel = loadOntologyFromClasspath(BUNDLE_RDF, BUNDLE);
		hasProxy = ontModel.getObjectProperty(BUNDLE + "hasProxy");
		hasAnnotation = ontModel.getObjectProperty(BUNDLE + "hasAnnotation");
		inFolder = ontModel.getObjectProperty(BUNDLE + "inFolder");
		checkNotNull(hasProxy, hasAnnotation, inFolder);
		bundle = ontModel;
	}

	
	protected synchronized void loadORE() {
		if (ore != null) {
			return;
		}
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

	public static <T> ClosableIterable<T> iterate(ExtendedIterator<T> iterator) {
		return new ClosableIterable<T>(iterator);
	}

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

	public void readTo(InputStream manifestResourceAsStream, Manifest manifest, URI manifestResourceBaseURI)
			throws IOException, RiotException {
		
		
		OntModel model = new RDFToManifest().getOntModel();
		model.add(jsonLdAsJenaModel(manifestResourceAsStream, manifestResourceBaseURI));
		
		model.write(System.out, "TURTLE");
		
		URI root = manifestResourceBaseURI.resolve("/");
		Individual ro = findRO(model, root);

		for (Individual manifestResource : listObjectProperties(ro, isDescribedBy)) {
			String uriStr = manifestResource.getURI();
			if (uriStr == null) {
				logger.warning("Skipping manifest without URI: "
						+ manifestResource);
				continue;
			}			
			//URI relative = relativizeFromBase(uriStr, root);
			Path path = manifest.getBundle().getFileSystem().provider().getPath(URI.create(uriStr));
			manifest.getManifest().add(path);			
		}
		
		List<Agent> creators = getAgents(root, ro, createdBy);
		if (!creators.isEmpty()) {
			manifest.setCreatedBy(creators.get(0));
			if (creators.size() > 1) {
				logger.warning("Ignoring additional createdBy agents");
			}
			
		}
		
		RDFNode created = ro.getPropertyValue(createdOn);
		manifest.setCreatedOn(literalAsFileTime(created));

		List<Agent> authors = getAgents(root, ro, authoredBy);
		if (!authors.isEmpty()) {
			manifest.setAuthoredBy(authors);
		}
		RDFNode authored = ro.getPropertyValue(authoredOn);
		manifest.setAuthoredOn(literalAsFileTime(authored));

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

			ResIterator proxies = model.listSubjectsWithProperty(proxyFor, aggrResource);
			if (proxies.hasNext()) {
				Resource proxy = proxies.next();
				if (proxy.getURI() != null) {
					meta.setProxy(relativizeFromBase(proxy.getURI(), root));
				}
			
			}
			

			creators = getAgents(root, aggrResource, createdBy);
			if (!creators.isEmpty()) {
				meta.setCreatedBy(creators.get(0));
				if (creators.size() > 1) {
					logger.warning("Ignoring additional createdBy agents for " + meta);
				}

			}
			meta.setCreatedOn(literalAsFileTime(aggrResource
					.getPropertyValue(createdOn)));

			for (Individual standard : listObjectProperties(aggrResource,
					conformsTo)) {
				if (standard.getURI() != null) {
					meta.setConformsTo(relativizeFromBase(standard.getURI(),
							root));
				}
			}

			RDFNode mediaType = aggrResource.getPropertyValue(format);
			if (mediaType != null && mediaType.isLiteral()) {
				meta.setMediatype(mediaType.asLiteral().getLexicalForm());
			}

		}
		
		for (Individual ann : listObjectProperties(ro, hasAnnotation)) {
				// Normally just one body per annotation, but just in case we'll
				// iterate and split them out (as our PathAnnotation can
				// only keep a single setContent() at a time)
				for (Individual body : listObjectProperties(
						model.getOntResource(ann), hasBody)) {
					PathAnnotation pathAnn = new PathAnnotation();
					if (body.getURI() != null) {
						pathAnn.setContent(relativizeFromBase(body.getURI(),
								root));
					} else {
						logger.warning("Can't find annotation body for anonymous "
								+ body);
					}
					
					if (ann.getURI() != null) {
						pathAnn.setAnnotation(relativizeFromBase(ann.getURI(),
								root));
					}

					// Handle multiple about/hasTarget		
					for (Individual target : listObjectProperties(ann, hasTarget)) {
							if (target.getURI() != null) {
								pathAnn.getAboutList().add(
										relativizeFromBase(target.getURI(),
												root));
							}
					}
					manifest.getAnnotations().add(pathAnn);
				}
			}

		

	}

	

	private List<Agent> getAgents(URI base, Individual in,
			ObjectProperty property) {
		List<Agent> creators = new ArrayList<>();
		for (Individual agent : listObjectProperties(in, property)) {
			Agent a = new Agent();
			if (agent.getURI() != null) {
				a.setUri(relativizeFromBase(agent.getURI(), base));
			}

			RDFNode name = agent.getPropertyValue(foafName);
			if (name != null && name.isLiteral()) {
				a.setName(name.asLiteral().getLexicalForm());
			}
			creators.add(a);
		}
		return creators;
	}

	protected static URI makeBaseURI() throws URISyntaxException {
		return new URI("app", UUID.randomUUID().toString(), "/", (String) null);
	}

	private Set<Individual> listObjectProperties(OntResource ontResource,
			ObjectProperty prop) {
		LinkedHashSet<Individual> results = new LinkedHashSet<>();
		try (ClosableIterable<RDFNode> props = iterate(ontResource
				.listPropertyValues(prop))) {
			for (RDFNode node : props) {
				if (!node.isResource() || !node.canAs(Individual.class)) {
					continue;
				}
				results.add(node.as(Individual.class));
			}
		}
		return results;
	}

	private Individual findRO(OntModel model, URI base) {
		try (ClosableIterable<? extends OntResource> instances = iterate(aggregation
				.listInstances())) {
			for (OntResource o : instances) {
				// System.out.println("Woo " + o);
				return o.asIndividual();
			}
		}
		// Fallback - resolve as "/"
		// TODO: Ensure it's an Aggregation?
		return model.getIndividual(base.toString());
	}

}
