package org.apache.taverna.robundle;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.junit.Test;

public class TestExample {
	@Test
	public void example() throws Exception {
		// Create a new (temporary) RO bundle
		Bundle bundle = Bundles.createBundle();

		// Get the inputs
		Path inputs = bundle.getRoot().resolve("inputs");
		Files.createDirectory(inputs);

		// Get an input port:
		Path in1 = inputs.resolve("in1");

		// Setting a string value for the input port:
		Bundles.setStringValue(in1, "Hello");

		// And retrieving it
		assertTrue(Bundles.isValue(in1));
		assertEquals("Hello", Bundles.getStringValue(in1));

		// Or just use the regular Files methods:
		int lines = 0;
		for (String line : Files.readAllLines(in1, Charset.forName("UTF-8"))) {
			assertEquals("Hello", line);
			lines++;
		}
		assertEquals(1, lines);

		// Binaries and large files are done through the Files API
		try (OutputStream out = Files.newOutputStream(in1,
				StandardOpenOption.APPEND)) {
			out.write(32);
		}
		// Or Java 7 style
		Path localFile = Files.createTempFile("", ".txt");
		Files.copy(in1, localFile, StandardCopyOption.REPLACE_EXISTING);
		//System.out.println("Written to: " + localFile);

		Files.copy(localFile, bundle.getRoot().resolve("out1"));

		// Representing references
		URI ref = URI.create("http://example.com/external.txt");
		Path out3 = bundle.getRoot().resolve("out3");
		Bundles.setReference(out3, ref);
		if (Bundles.isReference(out3)) {
			URI resolved = Bundles.getReference(out3);
			assertNotNull(resolved);
			//System.out.println(resolved);
		}

		// Saving a bundle:
		Path zip = Files.createTempFile("bundle", ".zip");
		Bundles.closeAndSaveBundle(bundle, zip);
		// NOTE: From now "bundle" and its Path's are CLOSED
		// and can no longer be accessed

		//System.out.println("Saved to " + zip);

		// Loading a bundle back from disk
		try (Bundle bundle2 = Bundles.openBundle(zip)) {
			assertEquals(zip, bundle2.getSource());
		}

		// if (Desktop.isDesktopSupported()) {
		// // Open ZIP file for browsing
		// Desktop.getDesktop().open(zip.toFile());
		// }
	}
}
