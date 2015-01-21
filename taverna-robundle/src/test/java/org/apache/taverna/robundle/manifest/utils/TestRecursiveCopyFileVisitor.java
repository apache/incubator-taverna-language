package org.apache.taverna.robundle.manifest.utils;

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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.taverna.robundle.Bundles;
import org.junit.After;
import org.junit.Test;

public class TestRecursiveCopyFileVisitor {

	// TODO: Test NOFOLLOW and follow of symlinks

	private Set<Path> tmps = new LinkedHashSet<>();

	@After
	public void deleteTmps() throws IOException {
		IOException lastEx = null;
		for (Path p : tmps) {
			try {
				Bundles.deleteRecursively(p);
			} catch (IOException e) {
				lastEx = e;
			}
		}
		if (lastEx != null)
			throw lastEx;
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void copyRecursivelyAlreadyExists() throws Exception {
		Path orig = tempDir("orig");
		Path dest = tempDir("dest");
		Bundles.copyRecursively(orig, dest);
	}

	protected Path tempDir(String name) throws IOException {
		Path dir = Files.createTempDirectory(name);
		tmps.add(dir);
		return dir;
	}

	@Test
	public void copyRecursivelyReplace() throws Exception {
		Path orig = tempDir("orig");
		Files.createFile(orig.resolve("file"));
		Path dest = tempDir("dest");
		Bundles.copyRecursively(orig, dest, StandardCopyOption.REPLACE_EXISTING);
		assertTrue(Files.isRegularFile(dest.resolve("file")));
		// Second copy should also be OK
		Bundles.copyRecursively(orig, dest, StandardCopyOption.REPLACE_EXISTING);
	}

	@Test
	public void copyRecursively() throws Exception {
		Path orig = tempDir("orig");
		Files.createFile(orig.resolve("1"));
		Files.createDirectory(orig.resolve("2"));
		Files.createFile(orig.resolve("2/1"));
		Files.createDirectory(orig.resolve("2/2"));
		List<String> hello = Arrays.asList("Hello");

		Charset ascii = Charset.forName("ASCII");
		Files.write(orig.resolve("2/2/1"), hello, ascii);

		Files.createDirectory(orig.resolve("2/2/2"));
		Files.createFile(orig.resolve("3"));

		Path dest = tempDir("dest");
		Files.delete(dest);
		Bundles.copyRecursively(orig, dest);

		assertTrue(Files.isDirectory(dest.resolve("2")));
		assertTrue(Files.isDirectory(dest.resolve("2/2")));
		assertTrue(Files.isDirectory(dest.resolve("2/2")));
		assertTrue(Files.isDirectory(dest.resolve("2/2/2")));
		assertTrue(Files.isRegularFile(dest.resolve("1")));
		assertTrue(Files.isRegularFile(dest.resolve("2/1")));
		assertTrue(Files.isRegularFile(dest.resolve("2/2/1")));
		assertTrue(Files.isRegularFile(dest.resolve("3")));
		assertEquals(hello, Files.readAllLines(dest.resolve("2/2/1"), ascii));
	}
}
