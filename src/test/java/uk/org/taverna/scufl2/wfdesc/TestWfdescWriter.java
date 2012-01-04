package uk.org.taverna.scufl2.wfdesc;

import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.wfdesc.WfdescReader.TEXT_VND_WF4EVER_WFDESC_TURTLE;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class TestWfdescWriter {
	protected WorkflowBundle workflowBundle;
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();

	@Before
	public void makeExampleWorkflow() {
		workflowBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}
	

	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("wfdesc", ".ttl");
		bundleFile.deleteOnExit();
//		System.out.println(bundleFile);
		return bundleFile;
	}

	@Test
	public void writeBundleToFile() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(workflowBundle, bundleFile,
				TEXT_VND_WF4EVER_WFDESC_TURTLE);
		FileUtils.readFileToString(bundleFile, "utf-8");
		
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		RepositoryConnection con = myRepository.getConnection();
		con.add(bundleFile, bundleFile.toURI().toASCIIString(), RDFFormat.TURTLE);
//		assertTrue(con.prepareTupleQuery(QueryLanguage.SPARQL,
		assertTrue(con.prepareBooleanQuery(QueryLanguage.SPARQL, 
				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>  " +
				"ASK { " +
				"?wf a wfdesc:Workflow, wfdesc:Process ;" +
				"  wfdesc:hasInput ?yourName; " +
				"  wfdesc:hasOutput ?results; " +
				"  wfdesc:hasDataLink ?link1; " +
				"  wfdesc:hasDataLink ?link2; " +
				"  wfdesc:hasDataLink ?link3; " +
				"  wfdesc:hasSubProcess ?hello ; " +
				"  wfdesc:hasSubProcess ?wait4me . " +
				"?hello a wfdesc:Process ;" +
				"  wfdesc:hasInput ?name; " +
				"  wfdesc:hasOutput ?greeting . " +
				"?wait4me a wfdesc:Process ." +
				"?yourName a wfdesc:Input . " +
				"?results a wfdesc:Output . " +
				"?name a wfdesc:Input . " +
				"?greeting a wfdesc:Output . " +
				"?link1 a wfdesc:DataLink ; " +
				"  wfdesc:hasSource ?yourName ; " +
				"  wfdesc:hasSink ?results . " +
				"?link2 a wfdesc:DataLink ; " +
				"  wfdesc:hasSource ?yourName ; " +
				"  wfdesc:hasSink ?name . " +
				"?link3 a wfdesc:DataLink ; " +
				"  wfdesc:hasSource ?greeting ; " +
				"  wfdesc:hasSink ?results ." +
			    "}").evaluate());
	}

}
