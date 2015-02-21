package org.apache.taverna.robundle.fs;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.robundle.fs.BundleFileTypeDetector;
import org.junit.Test;

public class TestBundleFileTypeDetector {
	@Test
	public void detectRoBundle() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();
		try (Bundle bundle = Bundles.createBundle()) {
			assertEquals("application/vnd.wf4ever.robundle+zip",
					detector.probeContentType(bundle.getSource()));
		}
	}

	@Test
	public void detectEmptyZip() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();

		Path zip = Files.createTempFile("test", ".bin");
		zip.toFile().deleteOnExit();
		try (ZipOutputStream zout = new ZipOutputStream(
				Files.newOutputStream(zip))) {
			ZipEntry entry = new ZipEntry("e");
			zout.putNextEntry(entry);
			zout.closeEntry();

		}
		assertEquals("application/zip", detector.probeContentType(zip));
	}

	@Test
	public void detectNonZip() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();

		Path file = Files.createTempFile("test", ".bin");
		file.toFile().deleteOnExit();
		Files.write(file, Arrays.asList("This is just some text",
				"added here to make the file", "larger than 38 bytes"), Charset
				.forName("UTF8"));
		assertTrue(Files.size(file) > 38);
		assertNull(detector.probeContentType(file));
	}

	@Test
	public void detectEmpty() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();

		Path file = Files.createTempFile("test", ".bin");
		file.toFile().deleteOnExit();
		assertEquals(0, Files.size(file));
		assertNull(detector.probeContentType(file));
	}

	@Test
	public void detectorSPI() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {
			assertEquals("application/vnd.wf4ever.robundle+zip",
					Files.probeContentType(bundle.getSource()));
		}

	}

}
