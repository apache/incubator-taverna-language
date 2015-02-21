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
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestExample1 {
	
	private static DataThingMapType dt;
	
	private static String[] expectedKeys = new String[] {"status", "proteinIDs", "output"};
	
	private static String[] expectedMimeTypes = new String[] {"text/plain", "text/plain", "application/octet-stream"};

	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, JAXBException {
		ClassLoader classLoader = TestExample1.class.getClassLoader();
		File file = new File(classLoader.getResource("example1.xml").getFile());
		dt = BaclavaReader.readBaclava(new FileReader(file));
	}
	
	@Test
	public void checkRead() {
		Assert.assertNotNull("Unable to parse example1.xml", dt);
	}

	@Test
	public void checkNumberOfDataThings() {
		Assert.assertEquals("There should be three data things", 3, dt.getDataThing().size());
	}
	
	@Test
	public void checkDataThingKeys() {
		for (String k : expectedKeys) {
			Assert.assertNotNull(k + " should be there", getDataThingTypeByKey(k));
		}
	}
	
	@Test
	public void checkDataThingMimeTypes() {
		for (int i = 0; i < expectedKeys.length; i++) {
			String k = expectedKeys[i];
			DataThingType d = getDataThingTypeByKey(k);
			MyGridDataDocumentType doc = d.getMyGridDataDocument();
			Assert.assertNotNull("myGridDataDocument of " + k + " is missing", doc);
			MetadataType meta = doc.getMetadata();
			Assert.assertNotNull("metadata of " + k + " is missing", meta);
			MimeTypesType types = meta.getMimeTypes();
			Assert.assertNotNull("mimetypes of " + k + " is missing", types);
			List<String> mimeTypes = types.getMimeType();
			Assert.assertNotNull("mimetypes of " + k + " is null", mimeTypes);
			Assert.assertNotEquals("mimetypes of " + k + " is empty", 0, mimeTypes);
			String firstMimeType = mimeTypes.get(0);
			Assert.assertEquals("mimetype of " + k + " is wrong", expectedMimeTypes[i], firstMimeType);
		}
	}
	
	private DataThingType getDataThingTypeByKey(String key) {
		DataThingType result = null;
		for (DataThingType d : dt.getDataThing()) {
			if (d.getKey().equals(key)) {
				result = d;
				break;
			}
		}
		return result;
	}
}
