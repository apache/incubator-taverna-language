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

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;


public class TestWfdescReader {

	WorkflowBundleIO io = new WorkflowBundleIO();
	
	@Test
	public void guessType() throws Exception {		
		assertEquals("application/vnd.taverna.t2flow+xml", 
				guessMediaTypeForResource("/helloworld.t2flow"));
		assertEquals("application/vnd.taverna.t2flow+xml", 
				guessMediaTypeForResource("/rdf-in-example-annotation.t2flow"));

		assertEquals("text/vnd.wf4ever.wfdesc+turtle", 
				guessMediaTypeForResource("/helloworld.wfdesc.ttl"));
		
		assertNull( 
				guessMediaTypeForResource("/allTypes.links.sparql.json"));
	}
	
	private String guessMediaTypeForResource(String resource) throws IOException {
		byte[] firstBytes = new byte[1024];
		InputStream s = getClass().getResourceAsStream(resource);
		assertNotNull("Could not find " + resource, s);
		try {
			s.read(firstBytes);
		} finally { 
			s.close();
		}
		return io.guessMediaTypeForSignature(firstBytes);
	}
	
}
