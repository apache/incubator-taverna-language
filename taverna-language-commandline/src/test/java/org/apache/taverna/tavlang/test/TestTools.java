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
import org.junit.Test;
import org.junit.*;

/*
 * Test cases for the Tools Class.
 * Input :- The output file format
 * Output:- The relevant writer media type for that file format
 * 
 * */

public class TestTools {

	private ConvertionTools tools;
	private HashMap<String, String> map = new HashMap<>();
	
	 
	@Before
	public void setMap(){
		this.map.put("wfdesc", "text/vnd.wf4ever.wfdesc+turtle");
		this.map.put("iwir", "application/vnd.shiwa.iwir+xml");
		this.map.put("json", "application/ld+json");
		this.map.put("wfbundle", "application/vnd.taverna.scufl2.workflow-bundle");
		this.map.put("robundle", null);
	}
	
	
	@Test
	public void testWfdesc() {
		assertEquals(map.get("wfdesc"), ConvertionTools.valueOf("wfdesc").getMediaType(tools));
	}

	@Test
	public void testIwir() {
		assertEquals(map.get("iwir"), ConvertionTools.valueOf("iwir").getMediaType(tools));
	}

	@Test
	public void testJson() {
		assertEquals(map.get("json"), ConvertionTools.valueOf("json").getMediaType(tools));
	}


	@Test
	public void testWfbundle() {
		assertEquals(map.get("wfbundle"), ConvertionTools.valueOf("wfbundle").getMediaType(tools));
	}
	
	
	@Test
	public void testRobundle() {
		assertEquals(map.get("robundle"), ConvertionTools.valueOf("robundle").getMediaType(tools));
	}

}
