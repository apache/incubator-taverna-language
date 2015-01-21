package org.apache.taverna.scufl2.api.core;

/*
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
 */


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.junit.Test;

public class ControlLinkCompareTest {
	@SuppressWarnings("unchecked")
	@Test
	public void expectedOrder() throws Exception {

		Workflow wf = new Workflow();		
		wf.setName("wf");
		
		Processor a = new Processor(wf, "a");
		Processor b = new Processor(wf, "b");
		Processor c = new Processor(wf, "c");
		Processor d = new Processor(wf, "d");

		BlockingControlLink b_blocks_c = new BlockingControlLink(c, b);
		BlockingControlLink a_blocks_c = new BlockingControlLink(c, a);
		BlockingControlLink a_blocks_b = new BlockingControlLink(b, a);
		BlockingControlLink b_blocks_d = new BlockingControlLink(d, b);
		BlockingControlLink a_blocks_d = new BlockingControlLink(d, a);
		
		ArrayList<ControlLink> links = new ArrayList<ControlLink>(wf.getControlLinks());
		assertEquals(Arrays.asList(a_blocks_b, a_blocks_c, a_blocks_d, b_blocks_c, b_blocks_d), links);
		Collections.shuffle(links);
		Collections.sort(links);
		assertEquals(Arrays.asList(a_blocks_b, a_blocks_c, a_blocks_d, b_blocks_c, b_blocks_d), links);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nullSupport() throws Exception {
		Workflow wf = new Workflow();		
		wf.setName("wf");
		
		
		
		Processor a = new Processor(wf, "a");
		Processor b = new Processor(wf, "b");
		Processor c = new Processor(wf, "c");
		Processor d = new Processor(wf, "d");

		BlockingControlLink b_blocks_c = new BlockingControlLink(c, b);
		BlockingControlLink null_blocks_c = new BlockingControlLink();
		null_blocks_c.setBlock(c);
		null_blocks_c.setParent(wf);
		BlockingControlLink a_blocks_b = new BlockingControlLink(b, a);
		BlockingControlLink b_blocks_null = new BlockingControlLink();
		b_blocks_null.setUntilFinished(b);
		b_blocks_null.setParent(wf);
		
		BlockingControlLink null_blocks_null = new BlockingControlLink();		
		null_blocks_null.setParent(wf);
		
		
		ArrayList<ControlLink> links = new ArrayList<ControlLink>(wf.getControlLinks());
		assertEquals(Arrays.asList(null_blocks_null, null_blocks_c, a_blocks_b, b_blocks_null, b_blocks_c), links);				

		Collections.shuffle(links);		
		Collections.sort(links);
		
		BlockingControlLink a_blocks_d_no_parent = new BlockingControlLink();
		a_blocks_d_no_parent.setBlock(d);
		a_blocks_d_no_parent.setUntilFinished(a);
		// no setParent
		links.add(a_blocks_d_no_parent);
		Collections.shuffle(links);		
		Collections.sort(links);
		
		assertEquals(Arrays.asList(null_blocks_null, null_blocks_c, a_blocks_b, a_blocks_d_no_parent, b_blocks_null, b_blocks_c), links);		
	}

}
