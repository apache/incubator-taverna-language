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


import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.robundle.manifest.Agent;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.PathAnnotation;
import org.junit.Test;

public class MotifAnalysisIT {
	@Test
	public void motifAnalysis() throws Exception {

		// The new RO bundle
		Path ro = Files.createTempFile("motifAnalysis", ".robundle.zip");
		try (Bundle bundle = Bundles.createBundle(ro)) {

			Path orig = Paths.get(getClass().getResource("/motifAnalysis.zip")
					.toURI());

			// Copy the motifAnalysis/ folder
			try (FileSystem origfs = FileSystems.newFileSystem(orig, null)) {
				Path origFolder = origfs.getPath("motifAnalysis/");
				Bundles.copyRecursively(origFolder, bundle.getRoot(),
						StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.COPY_ATTRIBUTES);
			}

			// TODO: Generating manifest should be automatic!

			// Generate manifest
			Manifest manifest = bundle.getManifest();

			// attributions

			// Stian made the RO bundle
			Agent stian = new Agent();
			stian.setUri(URI.create("http://soiland-reyes.com/stian/#me"));
			stian.setOrcid(URI.create("http://orcid.org/0000-0001-9842-9718"));
			stian.setName("Stian Soiland-Reyes");
			manifest.setCreatedBy(stian);
			// RO bundle was created "now"
			manifest.setCreatedOn(Files.getLastModifiedTime(ro));

			// but it was *authored* by Daniel et al

			Agent daniel = new Agent();
			daniel.setUri(URI
					.create("http://delicias.dia.fi.upm.es/members/DGarijo/#me"));
			daniel.setOrcid(URI.create("http://orcid.org/0000-0003-0454-7145"));
			daniel.setName("Daniel Garijo");

			List<Agent> authors = new ArrayList<>();
			authors.add(daniel);
			authors.add(new Agent("Pinar Alper"));
			authors.add(new Agent("Khalid Belhajjame"));
			authors.add(new Agent("Oscar Corcho"));
			authors.add(new Agent("Yolanda Gil"));
			authors.add(new Agent("Carole Goble"));
			manifest.setAuthoredBy(authors);

			// when was the RO authored? We'll say when the README was made..
			Path readme = bundle.getRoot().resolve("README.txt");
			manifest.setAuthoredOn(Files.getLastModifiedTime(readme));

			// And who made the README file?
			manifest.getAggregation(readme).setCreatedBy(daniel);
			manifest.getAggregation(readme).setMediatype("text/plain");

			// Annotations

			PathAnnotation readmeAnnotation = new PathAnnotation();
			readmeAnnotation.setAbout(URI.create("/"));
			readmeAnnotation.setContent(URI.create("/README.txt"));
			readmeAnnotation.generateAnnotationId();
			manifest.getAnnotations().add(readmeAnnotation);

			PathAnnotation website = new PathAnnotation();
			website.setAbout(URI.create("/"));
			website.setContent(URI
					.create("http://www.oeg-upm.net/files/dgarijo/motifAnalysisSite/"));
			website.generateAnnotationId();
			manifest.getAnnotations().add(website);

			// Write out manifest
			// This is now done automatically on close()
			// manifest.writeAsJsonLD();
		}

		System.out.println("Generated " + ro);
		// if (Desktop.isDesktopSupported()) {
		// Desktop.getDesktop().open(ro.toFile());
		// }
	}

}
