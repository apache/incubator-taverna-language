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
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;


import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.Quad;

public class AnnotationTools {
	private static final String EXAMPLE_DATA_PREDICATE = "http://biocatalogue.org/attribute/exampleData";
	public static final URI EXAMPLE_DATA = URI.create(EXAMPLE_DATA_PREDICATE);
	private static final String TITLE_PREDICATE = "http://purl.org/dc/terms/title";
	public static final URI TITLE = URI.create(TITLE_PREDICATE);
	private static final String DESCRIPTION_PREDICATE = "http://purl.org/dc/terms/description";
	public static final URI DESCRIPTION = URI.create(DESCRIPTION_PREDICATE);
	private static final String CREATOR_PREDICATE = "http://purl.org/dc/elements/1.1/creator";
	public static final URI CREATOR = URI.create(CREATOR_PREDICATE);

	private static Logger logger = Logger.getLogger(AnnotationTools.class
			.getCanonicalName());

	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private URITools uritools = new URITools();

	public Dataset annotationDatasetFor(Child<?> workflowBean) {
		Dataset dataset = DatasetFactory.createMem();
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
			Lang lang = RDFLanguages.contentTypeToLang(contentType);
			if (lang == null) {
				lang = RDFLanguages.filenameToLang(path);
			}
			if (lang == null) {
				logger.warning("Can't find media type of annotation body: "
						+ ann.getBody());
				continue;
			}
			Model model = ModelFactory.createDefaultModel();
			try (InputStream inStream = bundle.getResources()
					.getResourceAsInputStream(path)) {
				RDFDataMgr.read(model, inStream, bodyUri, lang);
			} catch (IOException e) {
				logger.warning("Can't read annotation body: " + path);
				continue;
			}
			dataset.addNamedModel(annUri.toString(), model);
		}

		return dataset;
	}

	public String getTitle(Child<?> workflowBean) {
		return getLiteral(workflowBean, TITLE_PREDICATE);
	}

	private String getLiteral(Child<?> workflowBean, String propertyUri) {
		Dataset annotations = annotationDatasetFor(workflowBean);
		URI beanUri = uritools.uriForBean(workflowBean);
		Node subject = NodeFactory.createURI(beanUri.toString());
		Node property = NodeFactory.createURI(propertyUri);

		Iterator<Quad> found = annotations.asDatasetGraph().find(null, subject,
				property, null);
		if (!found.hasNext()) {
			return null;
		}
		return found.next().getObject().toString(false);
	}

	public String getCreator(Child<?> workflowBean) {
		return getLiteral(workflowBean, CREATOR_PREDICATE);
	}

	public String getExampleValue(Child<?> workflowBean) {
		return getLiteral(workflowBean, EXAMPLE_DATA_PREDICATE);
	}

	public String getDescription(Child<?> workflowBean) {
		return getLiteral(workflowBean, DESCRIPTION_PREDICATE);
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
			Child<?> subject, URI predicate, String value) throws IOException {
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
		URI annotatedSubject = uritools.relativeUriForBean(subject, annotation);
		StringBuilder turtle = new StringBuilder();
		turtle.append("<");
		turtle.append(annotatedSubject.toASCIIString());
		turtle.append("> ");

		turtle.append("<");
		turtle.append(predicate.toASCIIString());
		turtle.append("> ");

		// A potentially multi-line string
		turtle.append("\"\"\"");
		// Escape existing \ to \\
		String escaped = value.replace("\\", "\\\\");
		// Escape existing " to \" (beware Java's escaping of \ and " below)
		escaped = escaped.replace("\"", "\\\"");
		turtle.append(escaped);
		turtle.append("\"\"\"");
		turtle.append(" .");
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
