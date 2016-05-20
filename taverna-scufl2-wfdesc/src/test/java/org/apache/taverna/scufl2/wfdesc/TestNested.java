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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Before;
import org.junit.Test;


public class TestNested {

	private static final String NESTED_T2FLOW = "/nested.t2flow";
	private File nestedWfdesc;

	private URL nestedT2flow;

	@Before
	public void loadT2flow() throws Exception {
		nestedT2flow = getClass().getResource(NESTED_T2FLOW);
		assertNotNull("Could not load " + NESTED_T2FLOW, nestedT2flow);
	}

	@Before
	public void tempFile() throws IOException {
		nestedWfdesc = File.createTempFile("scufl2-wfdesc", ".ttl");
		nestedWfdesc.delete();
//		allTypesWfdesc.deleteOnExit();
		// System.out.println(nestedWfdesc);
	}

	@Test
	public void convert() throws Exception {
		assertFalse(nestedWfdesc.exists());
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wfBundle = io.readBundle(nestedT2flow, null);
		io.writeBundle(wfBundle, nestedWfdesc,
				"text/vnd.wf4ever.wfdesc+turtle");
		assertTrue(nestedWfdesc.exists());

		// FIXME: Update test to use Jena
		
//		Repository myRepository = new SailRepository(new MemoryStore());
//		myRepository.initialize();
//		RepositoryConnection con = myRepository.getConnection();
//		con.add(nestedWfdesc, nestedWfdesc.toURI().toASCIIString(),
//				RDFFormat.TURTLE);
//
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//		SPARQLResultsJSONWriter writer = new SPARQLResultsJSONWriter(out);
//		con.prepareTupleQuery(
//				QueryLanguage.SPARQL,
//				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> "
//						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
//						+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
//						+ "SELECT ?wf ?proc ?procType ?procLabel "
//						+ "WHERE {"
//						+ "	?wf a wfdesc:Workflow;"
//						+ "       wfdesc:hasSubProcess ?nested . "
//						+ " ?nested a wfdesc:Workflow ;" +
//						"         wfdesc:hasSubProcess ?proc ."
//						+ " ?proc rdfs:label ?procLabel ."
//						// Ignore non-specific types
//						+ "OPTIONAL { ?proc a ?procType . FILTER (?procType != wfdesc:Description && ?procType != wfdesc:Process && ?procType != owl:Thing) }"
//						+ "} " + "ORDER BY ?wf ").evaluate(writer);
//		 //System.out.println(out.toString());
//
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode jsonNode = mapper.readValue(out.toByteArray(), JsonNode.class);
//		String oldWf = null;
//		for (JsonNode binding : jsonNode.path("results").path("bindings")) {
//			String wf = binding.path("wf").path("value").asText();
//			if (!wf.equals(oldWf)) {
//				//System.out.println(wf);
//				oldWf = wf;
//			}
//			String proc = binding.path("proc").path("value").asText();
//			assertNotNull(proc);
//			String procType = binding.path("procType").path("value").asText();
//			String procTypeshort = null;
//			if (procType != null) {
//			    procTypeshort = URI.create(procType).getFragment();
//			}
//			String procLabel = binding.path("procLabel").path("value").asText();
//			assertNotNull(procLabel);
//			assertNotNull(procTypeshort);
//			//System.out.println(" Processor " + procLabel + " (" + procTypeshort
//			//		+ ")");
//			//System.out.println("   " + proc + " " + procType);
//		}
//
//		out.reset();
//
//		con.prepareTupleQuery(
//				QueryLanguage.SPARQL,
//				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> "
//						+ " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
//						+ " PREFIX owl: <http://www.w3.org/2002/07/owl#> "
//						+ " SELECT ?wf ?fromProc ?toProc ?fromProcLabel ?toProcLabel "
//						+ " WHERE {" + "	?wf a wfdesc:Workflow;"
//						+ "       wfdesc:hasSubProcess ?fromProc,?toProc ;"
//						+ "       wfdesc:hasDataLink ?link . "
//						+ " ?link wfdesc:hasSource ?fromPort; "
//						+ "      wfdesc:hasSink ?toPort ."
//						+ " ?fromProc wfdesc:hasOutput ?fromPort ;"
//						+ "           rdfs:label ?fromProcLabel ."
//						+ " ?toProc wfdesc:hasInput ?toPort ;"
//						+ "         rdfs:label ?toProcLabel ." + "} "
//						+ "ORDER BY ?wf ").evaluate(writer);
//		 //System.out.println(out.toString());
//		jsonNode = mapper.readValue(out.toByteArray(), JsonNode.class);
//		for (JsonNode binding : jsonNode.path("results").path("bindings")) {
//			String wf = binding.path("wf").path("value").asText();
//			if (!wf.equals(oldWf)) {
//				//System.out.println(wf);
//				oldWf = wf;
//			}
//			String fromProcLabel = binding.path("fromProcLabel").path("value")
//					.asText();
//			assertNotNull(fromProcLabel);
//			String toProcLabel = binding.path("toProcLabel").path("value")
//					.asText();
//			assertNotNull(toProcLabel);
//			String fromProc = binding.path("fromProc").path("value").asText();
//			assertNotNull(fromProc);
//			String toProc = binding.path("toProc").path("value").asText();
//			assertNotNull(toProc);
//			//System.out.print(" " + fromProcLabel);
//			//System.out.println(" -> " + toProcLabel);
//			//System.out.println("    " + fromProc + " -> " + toProc);
//		}

	}

}
