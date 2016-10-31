package org.apache.taverna.scufl2.annotation;
/*
 *
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
 *
*/


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.jena.JenaIRI;
import org.apache.commons.rdf.jena.JenaLiteral;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.commons.rdf.jena.experimental.JenaRDFParser;
import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class AnnotationTools {
    
    private static JenaRDF rdf = new JenaRDF();
    	
	public static final IRI EXAMPLE_DATA = rdf.createIRI("http://biocatalogue.org/attribute/exampleData");
	public static final IRI TITLE = rdf.createIRI("http://purl.org/dc/terms/title");
	public static final IRI DESCRIPTION = rdf.createIRI("http://purl.org/dc/terms/description");
	public static final IRI CREATOR = rdf.createIRI("http://purl.org/dc/elements/1.1/creator");

	private static Logger logger = Logger.getLogger(AnnotationTools.class
			.getCanonicalName());

	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private URITools uritools = new URITools();

	public Dataset annotationDatasetFor(Child<?> workflowBean) {
		Dataset dataset = rdf.createDataset();
		for (Annotation ann : scufl2Tools.annotationsFor(workflowBean)) {
			WorkflowBundle bundle = ann.getParent();
			URI annUri = uritools.uriForBean(ann);
			String bodyUri = bundle.getGlobalBaseURI().resolve(ann.getBody())
					.toASCIIString();

			if (ann.getBody().isAbsolute()) {
				logger.info("Skipping absolute annotation body URI: "
						+ ann.getBody());
				// TODO: Optional loading of external annotation bodies
				continue;
			}
			String path = ann.getBody().getPath();

			ResourceEntry resourceEntry = bundle.getResources()
					.getResourceEntry(path);
			if (resourceEntry == null) {
				logger.warning("Can't find annotation body: " + path);
				continue;
			}
			String contentType = resourceEntry.getMediaType();
			
			try (InputStream inStream = bundle.getResources()
					.getResourceAsInputStream(path)) {
			    
			    Optional<Graph> graph = graphForAnnotation(dataset, annUri);
			    
			            
			    JenaRDFParser parser = new JenaRDFParser()
			            .base(bodyUri).source(inStream)
			            .contentType(contentType)
			            .target(graph.get());
                // TODO: Do multiple parsings in one go to speed up outer
                // for-loop? Would need thread-safe Dataset backed by say TDB in memory			    
			    try {
                    parser.parse().get();
                } catch (IllegalStateException | InterruptedException | ExecutionException e) {
                    logger.warning("Can't parse annotation body: " + path);
                    continue;
                }
			} catch (IOException e) {
				logger.warning("Can't read annotation body: " + path);
				continue;
			}
		}

		return dataset;
	}

    private Optional<Graph> graphForAnnotation(Dataset dataset, URI annUri) {
        IRI graphUri = uritools.asIRI(annUri);
        Optional<Graph> graph = dataset.getGraph(graphUri);
        
        if (! graph.isPresent()) {
            // Need a dummy quad first? 
            JenaIRI example = rdf.createIRI("http://example.com/");
            dataset.add(graphUri, example, example, example);
            graph = dataset.getGraph(graphUri);
            if (! graph.isPresent()) {
                logger.severe("Can't create named graph: " + graphUri.getIRIString());
            }
            // Remove dummy triple :)  This will crash if the above can't create graph. 
            graph.get().remove(example, example, example);
        }
        
        return graph;
    }

	public String getTitle(Child<?> workflowBean) {
		return getLiteral(workflowBean, TITLE).orElse(null);
	}

	private Optional<String> getLiteral(Child<?> workflowBean, IRI property) {
	    // TODO: Cache dataset PARSING!
		Dataset annotations = annotationDatasetFor(workflowBean);
		IRI beanIRI = uritools.asIRI(uritools.uriForBean(workflowBean));		
		return annotations.stream(null, beanIRI, property, null).map(Quad::getObject)
		        // Pick any Literal property value, if it exist
		    .filter(Literal.class::isInstance).map(Literal.class::cast)
		    .map(Literal::getLexicalForm).findAny();	
	}

	public String getCreator(Child<?> workflowBean) {
	    // TODO: Also support dcterms:creator and foaf:name ?
		return getLiteral(workflowBean, CREATOR).orElse(null);
	}

	public String getExampleValue(Child<?> workflowBean) {
	    // TODO: Also support example value as a path?
		return getLiteral(workflowBean, EXAMPLE_DATA).orElse(null);
	}

	public String getDescription(Child<?> workflowBean) {	    
		return getLiteral(workflowBean, DESCRIPTION).orElse(null);
	}

	/**
	 * Create a new annotation attached to the given 
	 * @param workflowBundle
	 * @param subject
	 * @param predicate
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public Annotation createNewAnnotation(WorkflowBundle workflowBundle,
			Child<?> subject, IRI predicate, String value) throws IOException {
		Object parent = subject.getParent();
		while (parent instanceof Child)
			parent = ((Child<?>) parent).getParent();
		if (parent != workflowBundle)
			throw new IllegalStateException(
					"annotations can only be added to bundles that their subjects are already a member of");
		if (predicate == null)
			throw new IllegalArgumentException(
					"annotation predicate must be non-null");
		if (value == null)
			throw new IllegalArgumentException(
					"annotation value must be non-null");
		
		// Add the annotation
		Annotation annotation = new Annotation();
		Calendar now = new GregorianCalendar();
		annotation.setParent(workflowBundle);

		String path = "annotation/" + annotation.getName() + ".ttl";
		URI bodyURI = URI.create(path);

		annotation.setTarget(subject);
		annotation.setAnnotatedAt(now);
		// annotation.setAnnotator();//FIXME
		annotation.setSerializedAt(now);
		IRI annotatedSubject = uritools.asIRI(uritools.relativeUriForBean(subject, annotation));
		
		
		StringBuilder turtle = new StringBuilder();
		turtle.append(annotatedSubject.ntriplesString());

		turtle.append(" ");
		turtle.append(predicate.ntriplesString());

		turtle.append(" ");
		JenaLiteral literal = rdf.createLiteral(value);
		turtle.append(literal.ntriplesString());
		
		turtle.append(" .\n");
		
		// TODO: Save with Jena instead
		try {
			workflowBundle.getResources().addResource(turtle.toString(), path,
					"text/turtle");
		} catch (IOException e) {
			workflowBundle.getAnnotations().remove(annotation);
			throw e;
		}
        annotation.setBody(bodyURI);
        return annotation;
	}
}
