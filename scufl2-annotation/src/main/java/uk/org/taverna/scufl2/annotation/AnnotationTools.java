package uk.org.taverna.scufl2.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.Quad;

public class AnnotationTools {

	private static Logger logger = Logger.getLogger(AnnotationTools.class
			.getCanonicalName());

	Scufl2Tools scufl2Tools = new Scufl2Tools();
	URITools uritools = new URITools();

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
		return getLiteral(workflowBean, "http://purl.org/dc/terms/title");
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
		return getLiteral(workflowBean,
				"http://purl.org/dc/elements/1.1/creator");
	}

	public String getExampleValue(Child<?> workflowBean) {
		return getLiteral(workflowBean,
				"http://biocatalogue.org/attribute/exampleData");
	}

	public String getDescription(Child<?> workflowBean) {
		return getLiteral(workflowBean, "http://purl.org/dc/terms/description");
	}
	
	
}
