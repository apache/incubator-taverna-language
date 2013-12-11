package org.purl.wf4ever.robundle.manifest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;

import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

public class RDFToManifest {
    private static Logger logger = Logger.getLogger(RDFToManifest.class.getCanonicalName());
    
    private static final URI ROOT = URI.create("/");
    private static final String PROV = "http://www.w3.org/ns/prov#";
    private static final String PROV_O = "http://www.w3.org/ns/prov-o#";
    private static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";
    private static final String PAV = "http://purl.org/pav/";

    private static final String DCT = "http://purl.org/dc/terms/";
    private static final String RO = "http://purl.org/wf4ever/ro#";
    private static final String ORE = "http://www.openarchives.org/ore/terms/";
    private static final String FOAF_RDF = "/ontologies/foaf.rdf";
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
    private OntClass Standard;
    private ObjectProperty authoredBy;
    private DatatypeProperty createdOn;
    private DatatypeProperty authoredOn;

    private DatatypeProperty format;

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
    }


    protected OntModel loadOntologyFromClasspath(String classPathUri, String uri) {
        OntModel ontModel = ModelFactory.createOntologyModel();

        // Load from classpath
        InputStream inStream = getClass().getResourceAsStream(classPathUri);
        if (inStream == null) {
            throw new IllegalArgumentException("Can't load " + classPathUri);
        }
//        Ontology ontology = ontModel.createOntology(uri);
        ontModel.read(inStream, uri);
        return ontModel;
    }


    
    
    protected static Model jsonLdAsJenaModel(InputStream jsonIn, URI base) throws IOException,
            RiotException {
        JenaJSONLD.init();
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, jsonIn, base.toASCIIString(), JenaJSONLD.JSONLD);
        return model;
        
//        
//        Object input = JSONUtils.fromInputStream(jsonIn);
//        JSONLDTripleCallback callback = new JenaTripleCallback();        
//        Model model = (Model)JSONLD.toRDF(input, callback, new Options(base.toASCIIString()));
//        return model;
    }
    
    private void checkNotNull(Object... possiblyNulls) {
        int i=0;
        for (Object check : possiblyNulls) {
            if (check == null) {
                throw new IllegalStateException("Could not load item #" + i);
            }
            i++;
        }
        
    }


    protected OntModel getOntModel() {
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.setNsPrefix("foaf", FOAF_0_1);
        ontModel.setNsPrefix("prov", PROV);
        ontModel.setNsPrefix("ore", ORE);
        ontModel.setNsPrefix("pav", PAV);
        ontModel.setNsPrefix("dct", DCT);
//        ontModel.getDocumentManager().loadImports(foaf.getOntModel());
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
        checkNotNull(createdBy,createdOn, authoredBy, authoredOn);
                
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

        OntModel ontModel = loadOntologyFromClasspath("/ontologies/dcterms_od.owl", "http://purl.org/wf4ever/dcterms_od");            
        
        // properties from dct
        Standard = ontModel.getOntClass(DCT + "Standard");
        conformsTo = ontModel.getObjectProperty(DCT + "conformsTo");

        // We'll cheat dc:format in
        format = ontModel.createDatatypeProperty("http://purl.org/dc/elements/1.1/" + "format");
        checkNotNull(Standard, conformsTo, format);
                
        dct = ontModel;  
        
    }
    
    protected synchronized void loadORE() {
        if (ore != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath("/ontologies/ore-owl.owl", "http://purl.org/wf4ever/ore-owl");
        aggregation = ontModel.getOntClass(ORE + "Aggregation");

        aggregates = ontModel.getObjectProperty(ORE + "aggregates");
        proxyFor = ontModel.getObjectProperty(ORE + "proxyFor");
        proxyIn = ontModel.getObjectProperty(ORE + "proxyIn");
        
        checkNotNull(aggregation, aggregates, proxyFor, proxyIn);
        
        
        ore = ontModel;
    }
    
    public static <T> ClosableIterable<T> iterate(ExtendedIterator<T> iterator) {
        return new ClosableIterable<T>(iterator);
    }
    
    public static class ClosableIterable<T> implements AutoCloseable, Iterable<T> {

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

    public void readTo(InputStream resourceAsStream, Manifest manifest) throws IOException, RiotException {
        OntModel model = new RDFToManifest().getOntModel();
        URI base;
        try {
            base = makeBaseURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Can't make base URI of form app://{uuid}/", e);
        }
        try (InputStream jsonIn = getClass().getResourceAsStream("/manifest.json")) {
            model.add(jsonLdAsJenaModel(jsonIn, base));
        }
        
        Individual ro = findRO(model, base);
        


        List<Agent> creators = getAgents(base, ro, createdBy);
        if (! creators.isEmpty()) {
            manifest.setCreatedBy(creators);            
        }
        RDFNode created = ro.getPropertyValue(createdOn);
        manifest.setCreatedOn(literalAsFileTime(created));

        List<Agent> authors = getAgents(base, ro, authoredBy);
        if (! authors.isEmpty()) {
            manifest.setAuthoredBy(authors);
        }
        RDFNode authored = ro.getPropertyValue(authoredOn);
        manifest.setAuthoredOn(literalAsFileTime(authored));
        
        
        for (Individual aggrResource : listObjectProperties(ro, aggregates)) {
            String uriStr = aggrResource.getURI();
            PathMetadata meta = new PathMetadata();
            if (uriStr != null) {
                meta.setUri(relativizeFromBase(uriStr, base));
            }
            Resource proxy = aggrResource.getPropertyResourceValue(proxyFor);
            if (proxy != null && proxy.getURI() != null) {
                meta.setProxy(relativizeFromBase(proxy.getURI(), base));
            }

            creators = getAgents(base, aggrResource, createdBy);
            if (! creators.isEmpty()) {
                manifest.setCreatedBy(creators);            
            }
            meta.setCreatedOn(literalAsFileTime(aggrResource.getPropertyValue(createdOn)));

            
            for (Individual standard : listObjectProperties(aggrResource, conformsTo)) {
                if (standard.getURI() != null) {
                    meta.setConformsTo(relativizeFromBase(standard.getURI(), base));
                }
            }
            
            RDFNode mediaType = aggrResource.getPropertyValue(format);            
            if (mediaType != null && mediaType.isLiteral()) {
                meta.setMediatype(mediaType.asLiteral().getLexicalForm());
            }
            
            manifest.getAggregates().add(meta);
        }
        
        
        
//        model.write(System.out, "TURTLE");
        
    }

    private FileTime literalAsFileTime(RDFNode rdfNode) {
        if (rdfNode == null) { 
            return null;
        }
        if (! rdfNode.isLiteral()) { 
            logger.warning("Expected literal. not " + rdfNode);
        }
        Literal literal = rdfNode.asLiteral();
        Object value = literal.getValue();
        if (! (value instanceof XSDDateTime)) {
            logger.warning("Literal not an XSDDateTime, but: " + value.getClass() + " " + value);
            return null;
        }        
        XSDDateTime dateTime = (XSDDateTime) value;
        long millis = dateTime.asCalendar().getTimeInMillis();                            
        return FileTime.fromMillis(millis);
    }

    private List<Agent> getAgents(URI base, Individual in, ObjectProperty property) {
        List<Agent> creators = new ArrayList<>();
        for (Individual agent : listObjectProperties(in, property)) {
            Agent a = new Agent();
            if (agent.getURI() != null) {
                a.setUri(relativizeFromBase(agent.getURI(), base));
                
                RDFNode name = agent.getPropertyValue(foafName);
                if (name != null && name.isLiteral()) {
                    a.setName(name.asLiteral().getLexicalForm());
                }
            }                
            creators.add(a);
        }
        return creators;
    }

    private URI relativizeFromBase(String uriStr, URI base) {
        return ROOT.resolve(base.relativize(URI.create(uriStr)));
    }

    protected static URI makeBaseURI() throws URISyntaxException {
        return new URI("app", UUID.randomUUID().toString(), "/", (String)null);
    }




    private Set<Individual> listObjectProperties(Individual in,
            ObjectProperty prop) {
        LinkedHashSet<Individual> results = new LinkedHashSet<>();
        try (ClosableIterable<RDFNode> props = iterate(in.listPropertyValues(prop))) {
            for (RDFNode node : props) {
                if (! node.isResource() || ! node.canAs(Individual.class)) {
                    continue;
                }
                results.add(node.as(Individual.class));
            }
        }
        return results;
    }

    private Individual findRO(OntModel model, URI base) {
        try (ClosableIterable<? extends OntResource> instances = iterate(aggregation.listInstances())) {
            for (OntResource o : instances) {
                System.out.println("Woo " + o);
                return o.asIndividual();
            }
        }
        // Fallback - resolve as "/"
        // TODO: Ensure it's an Aggregation?
        return model.getIndividual(base.toString());
    }

}
