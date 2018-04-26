package org.apache.taverna.robundle.manifest.odf;

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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.PathMetadata;
import org.junit.Test;

public class TestODFManifest {
	@Test
	public void openHelloWorld() throws Exception {
		try (InputStream is = getClass().getResourceAsStream("/helloworld.wfbundle")) {			
			assertNotNull(is);
			try (Bundle bundle = Bundles.openBundle(is)) {
				assertEquals("application/vnd.taverna.scufl2.workflow-bundle",
						Bundles.getMimeType(bundle));
				Path t2flow = bundle
						.getPath("history/8781d5f4-d0ba-48a8-a1d1-14281bd8a917.t2flow");
				assertEquals("application/vnd.taverna.t2flow+xml", bundle
						.getManifest().getAggregation(t2flow).getMediatype());
				Path manifestRdf = bundle.getPath("META-INF/manifest.xml");
				assertTrue(Files.exists(manifestRdf));
				assertTrue(bundle.getManifest().getManifest().contains(manifestRdf));
			}
			}
	}

	@Test
	public void openODTDocument() throws Exception {
		try (InputStream is = getClass().getResourceAsStream("/document.odt")) {			
			assertNotNull(is);
			try (Bundle bundle = Bundles.openBundle(is)) {
				assertEquals("application/vnd.oasis.opendocument.text",
						Bundles.getMimeType(bundle));
	
				Path contentXml = bundle.getPath("content.xml");
				Manifest manifest = bundle.getManifest();
				assertEquals("text/xml", manifest.getAggregation(contentXml)
						.getMediatype());
				PathMetadata rootMeta = manifest.getAggregation(bundle.getRoot());
				assertEquals("1.2", rootMeta.getConformsTo() + "");
				assertEquals("application/vnd.oasis.opendocument.text",
						rootMeta.getMediatype());
			}
		}
	}

}
