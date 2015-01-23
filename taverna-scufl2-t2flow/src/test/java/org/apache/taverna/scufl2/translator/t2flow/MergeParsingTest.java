package org.apache.taverna.scufl2.translator.t2flow;
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

import java.net.URL;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


public class MergeParsingTest {

	private static final String MERGE_FUN = "/merge_fun.t2flow";
	private static final String MERGE_THEN_DATAFLOW = "/merge_then_dataflow_link.t2flow";
	private static final String DATAFLOW_THEN_MERGE = "/dataflow_link_then_merge.t2flow";
	private static final String MISSING_MERGE = "/missing_merge.t2flow";

	@Test
	public void mergeFun() throws Exception {
		URL wfResource = getClass().getResource(MERGE_FUN);
		assertNotNull("Could not find workflow " + MERGE_FUN, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		@SuppressWarnings("unused")
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());		
	}
	
	@Test(expected=ReaderException.class)
	public void mergeThenDataflow() throws Exception {
		URL wfResource = getClass().getResource(MERGE_THEN_DATAFLOW);
		assertNotNull("Could not find workflow " + MERGE_THEN_DATAFLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		parser.parseT2Flow(wfResource.openStream());
	}
	
	@Test(expected=ReaderException.class)
	public void dataflowThenMerge() throws Exception {
		URL wfResource = getClass().getResource(DATAFLOW_THEN_MERGE);
		assertNotNull("Could not find workflow " + DATAFLOW_THEN_MERGE, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		parser.parseT2Flow(wfResource.openStream());
	}
	
	@Test(expected=ReaderException.class)
	public void missingMerge() throws Exception {
		URL wfResource = getClass().getResource(MISSING_MERGE);
		assertNotNull("Could not find workflow " + MISSING_MERGE, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		parser.parseT2Flow(wfResource.openStream());
	}
	
	
}
