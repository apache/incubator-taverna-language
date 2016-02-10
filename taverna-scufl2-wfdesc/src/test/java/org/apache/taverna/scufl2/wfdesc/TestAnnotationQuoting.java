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


import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Before;


public class TestAnnotationQuoting {
	private static final String T3_1226 = "T3-1226-annotations-with-quotes.t2flow";
	
	ROEvoSerializer roEvo = new ROEvoSerializer();
	WorkflowBundleIO io = new WorkflowBundleIO();

	private WorkflowBundle workflow;

	private ByteArrayOutputStream output = new ByteArrayOutputStream();
	
	@Before
	public void loadDepdenency() throws Exception {
		InputStream localStream = getClass().getResourceAsStream("/" + T3_1226);
		assertNotNull(localStream);
		workflow = io.readBundle(localStream, "application/vnd.taverna.t2flow+xml");
		assertNotNull(workflow);
	}
	
	// FIXME: Update test to use Jena
	
//	@Test
//	public void wfdesc() throws Exception {		
// 		io.writeBundle(workflow, output, "text/vnd.wf4ever.wfdesc+turtle");
// 		
//		Repository myRepository = new SailRepository(new MemoryStore());
//		myRepository.initialize();
//		RepositoryConnection con = myRepository.getConnection();
//		String root = "app://600aac93-0ea8-4e9d-9593-081149e31d5a/";
//		//System.out.write(output.toByteArray());
//		con.add(new ByteArrayInputStream(output.toByteArray()), root, RDFFormat.TURTLE);
//		
//		TupleQueryResult results = con.prepareTupleQuery(QueryLanguage.SPARQL, 
//				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>  " +
//				"PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#>  " +
//				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
//				"PREFIX roterms: <http://purl.org/wf4ever/roterms#>  " +
//				"PREFIX dc: <http://purl.org/dc/elements/1.1/>  " +
//				"PREFIX dcterms: <http://purl.org/dc/terms/>  " + 				
//				"PREFIX biocat: <http://biocatalogue.org/attribute/>  " +
//				"SELECT ?author ?title ?desc ?portDesc ?example  " +
//				"WHERE {  " +
//				"?wf a wfdesc:Workflow ; " +
//				"  dc:creator ?author ; " +				
//				"  dcterms:title ?title ; " +
//				"  dcterms:description ?desc ; " +
//				"  wfdesc:hasInput ?in . " +
//				"?in a wfdesc:Input ; " +
//				"  dcterms:description ?portDesc ; " +
//				"  biocat:exampleData ?example . " +				
//			    "}").evaluate();
//
//		
//		
//		assertTrue("wfdesc not in expected structure", results.hasNext());
//		BindingSet bind = results.next();
//		assertEquals("Stian Soiland-Reyes", bind.getValue("author").stringValue());
//		assertEquals("T3-1226 test with 'single quote'", bind.getValue("title").stringValue());
//		// Note: The quotes below are only escaped in this Java source code
//		assertEquals("This comment contains \"\"\"triple quotes\"\"\" inside.", bind.getValue("desc").stringValue());
//		assertEquals("\"quote at the start", bind.getValue("portDesc").stringValue());
//		assertEquals("quote at the end\"", bind.getValue("example").stringValue());
//		
//	}
	
}
