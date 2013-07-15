package org.purl.wf4ever.robundle.manifest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.jsonldjava.core.JSONLD;
import com.github.jsonldjava.core.JSONLDProcessingError;
import com.github.jsonldjava.core.JSONLDTripleCallback;
import com.github.jsonldjava.impl.JenaTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class RDFToManifest {
    private static final URI ROOT = URI.create("/");
    private static final String PROV = "http://www.w3.org/ns/prov#";
    private static final String PROV_O = "http://www.w3.org/ns/prov-o#";
    private static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";
    private static final String PAV = "http://purl.org/pav/";

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

    public RDFToManifest() {
        loadOntologies();
    }
    
    protected void loadOntologies() {
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


    
    
    protected static Model jsonLdAsJenaModel(InputStream jsonIn) throws IOException,
            JSONLDProcessingError {
        Object input = JSONUtils.fromInputStream(jsonIn);
        JSONLDTripleCallback callback = new JenaTripleCallback();
        Model model = (Model)JSONLD.toRDF(input, callback);
        return model;
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
        checkNotNull(createdBy);
                
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

    public void readTo(InputStream resourceAsStream, Manifest manifest) throws IOException, JSONLDProcessingError {
        OntModel model = new RDFToManifest().getOntModel();
        try (InputStream jsonIn = getClass().getResourceAsStream("/manifest.json")) {   
            model.add(jsonLdAsJenaModel(jsonIn));
        }
        
        Individual ro = findRO(model);
        for (Individual in : listObjectProperties(ro, aggregates)) {
            String uriStr = in.getURI();
            PathMetadata meta = new PathMetadata();
            if (uriStr != null) {
                URI uri = ROOT.resolve(uriStr);
                if (! uri.isAbsolute()) {
                    // TODO: Also check for app:// absolute URIs
                    meta.setFile(uri);
                } else {
                    meta.setUri(uri);
                }
            }
            Resource proxy = in.getPropertyResourceValue(proxyFor);
            if (proxy != null && proxy.getURI() != null) {
                meta.setProxy(ROOT.resolve(proxy.getURI()));
            }
            
            List<Agent> creators = new ArrayList<>();
            for (Individual agent : listObjectProperties(in, createdBy)) {
                Agent a = new Agent();
                if (agent.getURI() != null) {
                    a.setUri(ROOT.resolve(agent.getURI()));
                    
                    RDFNode name = agent.getPropertyValue(foafName);
                    if (name != null && name.isLiteral()) {
                        a.setName(name.asLiteral().getLexicalForm());
                    }
                }
                
                creators.add(a);
            }
            if (! creators.isEmpty()) {
                manifest.setCreatedBy(creators);
            }
            
            manifest.getAggregates().add(meta);
        }
        
        
        
//        model.write(System.out, "TURTLE");
        
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

    private Individual findRO(OntModel model) {
        try (ClosableIterable<? extends OntResource> instances = iterate(aggregation.listInstances())) {
            for (OntResource o : instances) {
                System.out.println("Woo " + o);
                return o.asIndividual();
            }
        }
        // Fallback - resolve as "/"
        // TODO: Ensure it's an Aggregation?
        return model.getIndividual("/");
    }

}
