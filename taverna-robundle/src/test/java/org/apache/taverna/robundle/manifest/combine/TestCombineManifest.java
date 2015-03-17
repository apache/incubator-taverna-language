package org.apache.taverna.robundle.manifest.combine;

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
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.robundle.manifest.Agent;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.PathMetadata;
import org.junit.Test;

public class TestCombineManifest {
	@Test
	public void convertAslanidi() throws Exception {
		Path file = Files.createTempFile("aslanidi", ".zip");
		try (InputStream src = getClass().getResourceAsStream(
				"/combine/aslanidi_purkinje_model_skeleton.zip")) {
			Files.copy(src, file, StandardCopyOption.REPLACE_EXISTING);
		}
		System.out.println(file);
		try (Bundle bundle = Bundles.openBundle(file)) {
			Manifest manifest = bundle.getManifest();
			Path manifestXml = bundle.getRoot().resolve("manifest.xml");
			assertTrue("manifest.xml not listed in " + manifest.getManifest(),
					manifest.getManifest().contains(manifestXml));

			// List<Agent> manifestCreator = manifest.getCreatedBy();
			// Agent createdBy = manifestCreator.get(0);
			// assertEquals("Gary Mirams", createdBy.getName());
			// assertEquals("mbox:gary.mirams@cs.ox.ac.uk", createdBy.getUri());
			// assertEquals("2014-02-06T22:01:58Z",
			// manifest.getCreatedOn().toString());
			//
			Path csvPath = bundle.getRoot().resolve(
					"outputs_degree_of_block.csv");
			PathMetadata csv = manifest.getAggregation(csvPath);
			assertEquals("text/csv", csv.getMediatype());
			Agent csvCreator = csv.getCreatedBy();
			assertEquals("Gary Mirams", csvCreator.getName());
			assertEquals("mbox:gary.mirams@cs.ox.ac.uk", csvCreator.getUri()
					.toString());
			assertEquals("2014-02-06T22:01:58Z", csv.getCreatedOn().toString());

		}
	}

	@Test
	public void convertBoris() throws Exception {
		Path file = Files.createTempFile("Boris", ".omex");
		try (InputStream src = getClass().getResourceAsStream(
				"/combine/Boris-skeleton.omex")) {
			Files.copy(src, file, StandardCopyOption.REPLACE_EXISTING);
		}
		//System.out.println(file);
		try (Bundle bundle = Bundles.openBundle(file)) {
			Manifest manifest = bundle.getManifest();
			Path manifestXml = bundle.getRoot().resolve("manifest.xml");
			assertTrue("manifest.xml not listed in " + manifest.getManifest(),
					manifest.getManifest().contains(manifestXml));

			assertEquals("2013-05-28T16:50:43.999Z", manifest.getCreatedOn()
					.toString());

			// Can't test these - as Boris.omex manifest.xml only
			// list these as unconnected bnodes
			// List<Agent> manifestCreator = manifest.getCreatedBy();
			// Agent createdBy = manifestCreator.get(0);
			// assertEquals("Frank Bergmann", createdBy.getName());
			// assertEquals("mbox:fbergman@caltech.edu", createdBy.getUri());
			//
		}

	}

	@Test
	public void convertDirectoryMadness() throws Exception {
		Path file = Files.createTempFile("DirectoryMadness", ".omex");
		try (InputStream src = getClass().getResourceAsStream(
				"/combine/DirectoryMadness-skeleton.omex")) {
			Files.copy(src, file, StandardCopyOption.REPLACE_EXISTING);
		}
		//System.out.println(file);
		try (Bundle bundle = Bundles.openBundle(file)) {
			Manifest manifest = bundle.getManifest();
			Path manifestXml = bundle.getRoot().resolve("manifest.xml");
			assertTrue("manifest.xml not listed in " + manifest.getManifest(),
					manifest.getManifest().contains(manifestXml));

			Path boris = bundle.getRoot().resolve("BorisEJB.xml");
			PathMetadata borisMeta = bundle.getManifest().getAggregation(boris);
			assertEquals(
					URI.create("http://identifiers.org/combine.specifications/sbml"),
					borisMeta.getConformsTo());
			// dcterms:modified
			assertEquals("2013-04-05T12:50:56Z", borisMeta.getCreatedOn()
					.toString());

			Path paperPdf = bundle.getRoot().resolve("paper")
					.resolve("Kholodenko2000.pdf");
			PathMetadata paperMeta = bundle.getManifest().getAggregation(
					paperPdf);
			assertEquals("application/pdf", paperMeta.getMediatype());

			URI biomd = URI
					.create("http://www.ebi.ac.uk/biomodels-main/BIOMD0000000010");
			PathMetadata biomdMeta = bundle.getManifest().getAggregation(biomd);
			assertEquals(
					URI.create("http://identifiers.org/combine.specifications/sbml"),
					biomdMeta.getConformsTo());

		}

	}

	@Test
	public void convertDirectoryMadnessZipped() throws Exception {
		Path file = Files.createTempFile("DirectoryMadnessZipped", ".omex");
		try (InputStream src = getClass().getResourceAsStream(
				"/combine/DirectoryMadnessZipped-skeleton.omex")) {
			Files.copy(src, file, StandardCopyOption.REPLACE_EXISTING);
		}
		//System.out.println(file);
		try (Bundle bundle = Bundles.openBundle(file)) {

		}
	}

}
