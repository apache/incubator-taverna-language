package org.apache.taverna.tavlang.test;

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

import java.util.HashMap;

import org.apache.taverna.tavlang.tools.Tools.ConvertionTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTools {

	private ConvertionTools tools;
	private HashMap<String, String> table = new HashMap<String, String>();
	@Before
	public void setParameters(){
		
		table.put("structure", "text/vnd.taverna.scufl2.structure");
		table.put("iwir", "application/vnd.shiwa.iwir+xml");
		table.put("json", "application/ld+json");
		table.put("wfdesc", "text/vnd.wf4ever.wfdesc+turtle");
		table.put("wfbundle", "application/vnd.taverna.scufl2.workflow-bundle");
		table.put("robundle", null);
	}
	
	@Test
	public void testStructure() {
		String result = ConvertionTools.valueOf("structure").getMediaType(tools);
		assertEquals(result, table.get("structure"));
		
	}
	
	@Test
	public void testWfdesc(){
		assertEquals(ConvertionTools.valueOf("wfdesc").getMediaType(tools), table.get("wfdesc"));
	}
	
	@Test
	public void testIwir(){
		assertEquals(ConvertionTools.valueOf("iwir").getMediaType(tools), table.get("iwir"));
	}
	
	@Test
	public void testWfbundle(){
		assertEquals(ConvertionTools.valueOf("wfbundle").getMediaType(tools), table.get("wfbundle"));
	}
	
	@Test
	public void testJson(){
		assertEquals(ConvertionTools.valueOf("json").getMediaType(tools), table.get("json"));
	}
	
	@Test
	public void testRo(){
		assertEquals(ConvertionTools.valueOf("robundle").getMediaType(tools), table.get("robundle"));
	}
	
	@After
	public void clear(){
		this.table.clear();
	}

}
