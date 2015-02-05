package org.apache.taverna.robundle.utils;

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


import static java.nio.file.Files.createTempDirectory;

import java.io.IOException;
import java.nio.file.Path;

public class TemporaryFiles {
	public static Path temporaryBundle() throws IOException {
		Path tempDir = createTempDirectory("robundle");
		tempDir.toFile().deleteOnExit();
		/*
		 * Why inside a tempDir? Because ZipFileSystemProvider creates
		 * neighbouring temporary files per file that is written to zip, which
		 * could mean a lot of temporary files directly in /tmp - making it
		 * difficult to clean up
		 */
		Path bundle = tempDir.resolve("robundle.zip");
		bundle.toFile().deleteOnExit();
		return bundle;
	}
}
