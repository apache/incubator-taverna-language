package org.purl.wf4ever.wfdesc.scufl2;

import java.io.OutputStream;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.sail.memory.MemoryStore;

import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WriterException;

public class ROEvoSerializer {
	private URITools uriTools = new URITools();
	
	public void workflowHistory(Workflow mainWorkflow, OutputStream output) throws WriterException {	
		Repository repository = new SailRepository(new MemoryStore());
		try {
			repository.initialize();
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't initialize memory SAIL repository", e1);
		}
		ValueFactory factory = repository.getValueFactory();
	
		
		RepositoryConnection con;
		try {
			con = repository.getConnection();
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't get repository connection", e1);
		}

		Revision revision = mainWorkflow.getCurrentRevision();
		Revision previous = revision.getPreviousRevision();
		addRevision(factory, con, revision);
		while (previous != null) {
			addRevision(factory, con, previous);
			addPrevious(factory, con, revision, previous);			
			revision = previous;
			previous = revision.getPreviousRevision();
		}
		
		java.net.URI baseURI = Workflow.WORKFLOW_ROOT;
		
		try {
			con.setNamespace("roevo", "http://purl.org/wf4ever/roevo#");
			con.setNamespace("prov", "http://www.w3.org/ns/prov#");
//			con.setNamespace("wfdesc",
//					"http://purl.org/wf4ever/wfdesc#");
//			con.setNamespace("wf4ever",
//					"http://purl.org/wf4ever/wf4ever#");
			con.setNamespace("rdfs",
					"http://www.w3.org/2000/01/rdf-schema#");

			
			con.export(new OrganizedRDFWriter(
					new TurtleWriterWithBase(output, baseURI)));
		} catch (OpenRDFException e) {
			throw new WriterException("Can't write to output", e);
		}
		
	}

	private void addRevision(ValueFactory factory, RepositoryConnection con,
			Revision revision) {
		URI revisionURI = factory.createURI(revision.getResourceURI().toASCIIString());			
		
		URI version = factory.createURI("http://purl.org/wf4ever/roevo#Version");		
		URI entity = factory.createURI("http://www.w3.org/ns/prov#Entity");		
		try {
			con.add(revisionURI, RDF.TYPE, version);
			con.add(revisionURI, RDF.TYPE, entity);
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't add triple to repository", e1);
		}
		
	}

	private void addPrevious(ValueFactory factory, RepositoryConnection con,
			Revision revision, Revision previous) {
		URI revisionURI = factory.createURI(revision.getResourceURI().toASCIIString());			
		URI previousURI = factory.createURI(previous.getResourceURI().toASCIIString());
		
		URI prev = factory.createURI("http://www.w3.org/ns/prov#wasRevisionOf");
		
		try {
			con.add(revisionURI, prev, previousURI);
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't add triple to repository", e1);
		}
	}
	
}
