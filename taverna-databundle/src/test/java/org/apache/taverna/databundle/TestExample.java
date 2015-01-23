package org.apache.taverna.databundle;
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
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NavigableMap;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.databundle.ErrorDocument;
import org.apache.taverna.robundle.Bundle;
import org.junit.Test;

public class TestExample {
	@Test
	public void example() throws Exception {
		// Create a new (temporary) data bundle
		Bundle dataBundle = DataBundles.createBundle();

		// Get the inputs
		Path inputs = DataBundles.getInputs(dataBundle);

		// Get an input port:
		Path portIn1 = DataBundles.getPort(inputs, "in1");

		// Setting a string value for the input port:
		DataBundles.setStringValue(portIn1, "Hello");

		// And retrieving it
		if (DataBundles.isValue(portIn1)) {
			System.out.println(DataBundles.getStringValue(portIn1));
		}

		// Or just use the regular Files methods:
		for (String line : Files
				.readAllLines(portIn1, Charset.forName("UTF-8"))) {
			System.out.println(line);
		}

		// Binaries and large files are done through the Files API
		try (OutputStream out = Files.newOutputStream(portIn1,
				StandardOpenOption.APPEND)) {
			out.write(32);
		}
		// Or Java 7 style
		Path localFile = Files.createTempFile("", ".txt");
		Files.copy(portIn1, localFile, StandardCopyOption.REPLACE_EXISTING);
		System.out.println("Written to: " + localFile);

		// Either way works, of course
		Path outputs = DataBundles.getOutputs(dataBundle);
		Files.copy(localFile,
				DataBundles.getPort(outputs, "out1"));


		// When you get a port, it can become either a value or a list
		Path port2 = DataBundles.getPort(inputs, "port2");
		DataBundles.createList(port2); // empty list		
		if (DataBundles.isList(port2)) {
			List<Path> list = DataBundles.getList(port2);
			assertTrue(list.isEmpty());
		}

		// Adding items sequentially
		Path item0 = DataBundles.newListItem(port2);
		DataBundles.setStringValue(item0, "item 0");
		DataBundles.setStringValue(DataBundles.newListItem(port2), "item 1");
		DataBundles.setStringValue(DataBundles.newListItem(port2), "item 2");
		
		
		// Set and get by explicit position:
		DataBundles.setStringValue(DataBundles.getListItem(port2, 12), "item 12");
		System.out.println(DataBundles.getStringValue(DataBundles.getListItem(port2, 2)));
		
		// The list is sorted numerically (e.g. 2, 5, 10) and
		// will contain nulls for empty slots
		System.out.println(DataBundles.getList(port2));

		// Ports can be browsed as a map by port name
		NavigableMap<String, Path> ports = DataBundles.getPorts(inputs);
		System.out.println(ports.keySet());

		// Is there anything known about a port?
		if (! DataBundles.isMissing(DataBundles.getPort(outputs, "out3"))) {
			// DataBundles.getPorts(outputs).containsKey("out3")
			
		}
		
		// Representing errors		
		Path out2 = DataBundles.getPort(outputs, "out2");		
		DataBundles.setError(out2, "Something did not work", "A very\n long\n error\n trace");		

		// Retrieving errors
		if (DataBundles.isError(out2)) {
			ErrorDocument error = DataBundles.getError(out2);
			System.out.println("Error: " + error.getMessage());
		}
		
		// Representing references
		URI ref = URI.create("http://example.com/external.txt");
		Path out3 = DataBundles.getPort(outputs, "out3");
		System.out.println(DataBundles.setReference(out3, ref));
		if (DataBundles.isReference(out3)) {
			URI resolved = DataBundles.getReference(out3);
			System.out.println(resolved);
		}

		
		
		
		// Saving a data bundle:
		Path zip = Files.createTempFile("databundle", ".zip");
		DataBundles.closeAndSaveBundle(dataBundle, zip);
		// NOTE: From now dataBundle and its Path's are CLOSED 
		// and can no longer be accessed
		
		
		System.out.println("Saved to " + zip);
		if (Desktop.isDesktopSupported()) {
			// Open ZIP file for browsing
			Desktop.getDesktop().open(zip.toFile());
		}
		
		// Loading a data bundle back from disk
		try (Bundle dataBundle2 = DataBundles.openBundle(zip)) {
			assertEquals(zip, dataBundle2.getSource());
			Path loadedInputs = DataBundles.getInputs(dataBundle2);
			
			for (Path port : DataBundles.getPorts(loadedInputs).values()) {
				if (DataBundles.isValue(port)) {
					System.out.print("Value " + port + ": ");
					System.out.println(DataBundles.getStringValue(port));
				} else if (DataBundles.isList(port)) {
					System.out.print("List " + port + ": ");
					for (Path item : DataBundles.getList(port)) {
						// We'll assume depth 1 here
						System.out.print(DataBundles.getStringValue(item));
						System.out.print(", ");
					}
					System.out.println();
				}				
			}			
		}				
	}
}
