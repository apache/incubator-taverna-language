package org.apache.taverna.scufl2.wfdesc;
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


import static org.apache.taverna.scufl2.wfdesc.WfdescReader.TEXT_VND_WF4EVER_WFDESC_TURTLE;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.TestWorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Before;
import org.junit.Test;


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
		
		// FIXME: Update test to use Jena
		
//		Repository myRepository = new SailRepository(new MemoryStore());
//		myRepository.initialize();
//		RepositoryConnection con = myRepository.getConnection();
//		con.add(bundleFile, bundleFile.toURI().toASCIIString(), RDFFormat.TURTLE);
////		assertTrue(con.prepareTupleQuery(QueryLanguage.SPARQL,
//		assertTrue(con.prepareBooleanQuery(QueryLanguage.SPARQL, 
//				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>  " +
//				"ASK { " +
//				"?wf a wfdesc:Workflow, wfdesc:Process ;" +
//				"  wfdesc:hasInput ?yourName; " +
//				"  wfdesc:hasOutput ?results; " +
//				"  wfdesc:hasDataLink ?link1; " +
//				"  wfdesc:hasDataLink ?link2; " +
//				"  wfdesc:hasDataLink ?link3; " +
//				"  wfdesc:hasSubProcess ?hello ; " +
//				"  wfdesc:hasSubProcess ?wait4me . " +
//				"?hello a wfdesc:Process ;" +
//				"  wfdesc:hasInput ?name; " +
//				"  wfdesc:hasOutput ?greeting . " +
//				"?wait4me a wfdesc:Process ." +
//				"?yourName a wfdesc:Input . " +
//				"?results a wfdesc:Output . " +
//				"?name a wfdesc:Input . " +
//				"?greeting a wfdesc:Output . " +
//				"?link1 a wfdesc:DataLink ; " +
//				"  wfdesc:hasSource ?yourName ; " +
//				"  wfdesc:hasSink ?results . " +
//				"?link2 a wfdesc:DataLink ; " +
//				"  wfdesc:hasSource ?yourName ; " +
//				"  wfdesc:hasSink ?name . " +
//				"?link3 a wfdesc:DataLink ; " +
//				"  wfdesc:hasSource ?greeting ; " +
//				"  wfdesc:hasSink ?results ." +
//			    "}").evaluate());

	}

}
