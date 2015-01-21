package org.apache.taverna.scufl2.api.common;

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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.junit.Test;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestAbstractNamed {

	@Test
	public void replaceOnRename() throws Exception {
		Workflow wf = new Workflow();
		Processor fish = new Processor(wf, "fish");
		@SuppressWarnings("unused")
		Processor soup = new Processor(wf, "soup");
		assertEquals(2, wf.getProcessors().size());

		assertEquals(new HashSet(Arrays.asList("fish", "soup")), wf
				.getProcessors().getNames());
		fish.setName("soup");
		assertEquals(new HashSet(Arrays.asList("soup")), wf
				.getProcessors().getNames());
		assertEquals(1, wf.getProcessors().size());

		assertEquals(fish, wf.getProcessors().iterator().next());
		assertEquals(fish, wf.getProcessors().getByName("soup"));
		assertNull(wf.getProcessors().getByName("fish"));
	}
	
	
	@Test(expected=NullPointerException.class)
    public void nameNull() throws Exception {
        Processor p = new Processor();
        p.setName(null);
    }
	
    @Test(expected=IllegalArgumentException.class)
    public void nameEmpty() throws Exception {
        Processor p = new Processor();
        p.setName("");
    }
	
    @Test(expected=IllegalArgumentException.class)
    public void nameWithNewline() throws Exception {
        Processor p = new Processor();
        p.setName("new\nline");
    }
    

    @Test(expected=IllegalArgumentException.class)
    public void nameWithControlChar() throws Exception {
        Processor p = new Processor();
        p.setName("no\bell");
    }

    @Test(expected=IllegalArgumentException.class)
    public void nameWithColon() throws Exception {
        Processor p = new Processor();
        p.setName("not:url");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void nameWithSlash() throws Exception {
        Processor p = new Processor();
        p.setName("no/slash");
    }
    
    @Test
    public void nameWithSpace() throws Exception {
        Processor p = new Processor();
        p.setName("space allowed");
    }

    
	@Test
	public void setName() throws Exception {
		Workflow wf = new Workflow();
		Processor p = new Processor();
		p.setName("fish");
		p.setName("soup");
		p.setParent(wf);
	}

	@Test
	public void setNameWithParent() throws Exception {
		Workflow wf = new Workflow();
		Processor p = new Processor();
		p.setName("fish");
		p.setParent(wf);
		assertTrue(wf.getProcessors().contains(p));
		assertTrue(wf.getProcessors().containsName("fish"));
		assertFalse(wf.getProcessors().containsName("soup"));
		p.setName("soup");
		assertFalse(wf.getProcessors().containsName("fish"));
		assertTrue(wf.getProcessors().containsName("soup"));
	}

}
