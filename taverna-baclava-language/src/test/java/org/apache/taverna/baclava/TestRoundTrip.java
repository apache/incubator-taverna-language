package org.apache.taverna.baclava;

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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

public class TestRoundTrip {
	
	private static String[] fileNames = new String[] {"example1.xml", "example2.xml"};

	@Test
	public void testRoundTrips() throws JAXBException, FileNotFoundException {
		for (String fileName : fileNames) {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(fileName).getFile());
			DataThingMapType o = BaclavaReader.readBaclava(new FileReader(file));
		
			StringWriter initialWriter = new StringWriter();
			BaclavaWriter.writeBaclava(o,  initialWriter);
			String initialString = initialWriter.toString();
			
			DataThingMapType reread = BaclavaReader.readBaclava(new StringReader(initialString));
			StringWriter rewriteWriter = new StringWriter();
			BaclavaWriter.writeBaclava(reread, rewriteWriter);
			String rewrittenString = rewriteWriter.toString();
			Assert.assertEquals(initialString, rewrittenString);
		}

	}


}
