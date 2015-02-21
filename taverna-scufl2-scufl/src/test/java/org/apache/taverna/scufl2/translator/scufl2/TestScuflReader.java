package org.apache.taverna.scufl2.translator.scufl2;
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


import static org.apache.taverna.scufl2.translator.scufl.ScuflReader.APPLICATION_VND_TAVERNA_SCUFL_XML;
import static org.junit.Assert.assertEquals;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;


public class TestScuflReader {

	private static final String WORKFLOW10_XML = "/workflow10.xml";
	WorkflowBundleIO io = new WorkflowBundleIO();

	@Test
	public void guessMediaType() throws Exception {
		byte[] firstBytes = new byte[1024];
		getClass().getResourceAsStream(WORKFLOW10_XML).read(firstBytes);
		assertEquals(APPLICATION_VND_TAVERNA_SCUFL_XML,
				io.guessMediaTypeForSignature(firstBytes));
		// Mess up the namespace
		firstBytes[70] = 32;
		assertEquals(null, io.guessMediaTypeForSignature(firstBytes));
	}

}
