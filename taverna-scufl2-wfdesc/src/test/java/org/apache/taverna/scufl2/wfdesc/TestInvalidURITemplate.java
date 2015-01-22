package org.apache.taverna.scufl2.wfdesc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.wfdesc.ROEvoSerializer;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;


public class TestInvalidURITemplate {
	private static final String ENM_v21 = "enm-v21.t2flow";
	
	ROEvoSerializer roEvo = new ROEvoSerializer();
	WorkflowBundleIO io = new WorkflowBundleIO();

	private WorkflowBundle localDependency;

	private ByteArrayOutputStream output = new ByteArrayOutputStream();
	
	@Before
	public void loadENM() throws ReaderException, IOException, WriterException, RDFParseException, RepositoryException, QueryEvaluationException, MalformedQueryException {
		InputStream localStream = getClass().getResourceAsStream("/" + ENM_v21);
		assertNotNull(localStream);
		localDependency = io.readBundle(localStream, "application/vnd.taverna.t2flow+xml");
		assertNotNull(localDependency);
	}
	
	
	@Test
	public void wfdesc() throws Exception {		
 		io.writeBundle(localDependency, output, "text/vnd.wf4ever.wfdesc+turtle");
 		
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		RepositoryConnection con = myRepository.getConnection();
		String root = "app:///";
		//System.out.write(output.toByteArray());
		con.add(new ByteArrayInputStream(output.toByteArray()), root, RDFFormat.TURTLE);
		
		assertFalse(con.prepareBooleanQuery(QueryLanguage.SPARQL, 
				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>  " +
				"PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#>  " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
				"PREFIX roterms: <http://purl.org/wf4ever/roterms#>  " +
				"ASK { " +
				"?ws a wfdesc:Process, wf4ever:RESTService ;" +
				"  rdfs:label \"raster_upload_service\" ; " + 
				"  wf4ever:rootURI ?rootURI . " +
			    "}").evaluate());
		
	}
	
}
