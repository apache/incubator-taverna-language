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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.ro.vocabs.prov;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Before;
import org.junit.Test;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;


public class TestRoEvoSerializer {
	private static final String HELLOWORLD_T2FLOW = "helloanyone.t2flow";
	
	ROEvoSerializer roEvo = new ROEvoSerializer();
	WorkflowBundleIO io = new WorkflowBundleIO();

	private WorkflowBundle helloWorld;
	
	@Before
	public void loadHello() throws ReaderException, IOException {
		InputStream helloStream = getClass().getResourceAsStream("/" + HELLOWORLD_T2FLOW);
		assertNotNull(helloStream);
		helloWorld = io.readBundle(helloStream, "application/vnd.taverna.t2flow+xml");
		assertNotNull(helloWorld);
		assertEquals("/2010/workflow/01348671-5aaa-4cc2-84cc-477329b70b0d/",
				helloWorld.getMainWorkflow().getIdentifier().getPath());
	}
	
	@Test
	public void workflowUUIDs() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		roEvo.workflowHistory(helloWorld.getMainWorkflow(), os);
		System.out.write(os.toByteArray());
		assertTrue(500 < os.size());
		String ttl = os.toString("UTF-8");
		assertTrue(ttl.contains("01348671-5aaa-4cc2-84cc-477329b70b0d"));
		assertTrue(ttl.contains("VersionableResource"));
		assertTrue(ttl.contains("Entity"));
		
		OntModel m = ModelFactory.createOntologyModel();
		m.read(new ByteArrayInputStream(os.toByteArray()), "http://example.com/", "Turtle");
		Resource mainWf = m.getResource(helloWorld.getMainWorkflow().getIdentifier().toASCIIString());		
		Resource older = mainWf.getProperty(prov.wasRevisionOf).getResource();
		Resource oldest = older.getProperty(prov.wasRevisionOf).getResource();
		assertNull(oldest.getProperty(prov.wasRevisionOf));
		
	}
	
}
