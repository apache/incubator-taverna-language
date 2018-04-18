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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestZipFS {

	private static Path zip;
	private FileSystem fs;

	@Test
	public void fileChannelCreateNew() throws Exception {
		Path test = fs.getPath("test.txt");
		EnumSet<StandardOpenOption> options = EnumSet.<StandardOpenOption> of(
				StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
		fs.provider().newFileChannel(test, options);
	}

	@Test
	public void fileChannelCreate() throws Exception {
		try {
			Path test = fs.getPath("test.txt");
			FileChannel.open(test, StandardOpenOption.WRITE,
					StandardOpenOption.CREATE).close();
		} catch (NoSuchFileException ex) {
			System.err.println("Unexpected exception");
			ex.printStackTrace();
			// Bug in JDK
		}
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void fileChannelCreateFails() throws Exception {
		Path test = fs.getPath("test.txt");
		Files.createFile(test);
		FileChannel.open(test, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE_NEW).close();
	}

	@Test
	public void fileChannelTruncate() throws Exception {
		Path test = fs.getPath("test.txt");
		Files.write(test, new byte[1024]);
		assertEquals(1024, Files.size(test));
		FileChannel.open(test, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING).close();
		assertEquals(0, Files.size(test));
	}

	/**
	 * Verifies http://stackoverflow.com/questions/16588321/ as both ZIP format
	 * and Java 7 ZIPFS allows a folder and file to have the same name.
	 * <p>
	 * This JDK bug is fixed in JDK9, which throws
	 * FileAlreadyExistsException as expected 
	 * (this test returns early if that is the case). 
	 */
	@Test
	public void directoryOrFile() throws Exception {
		Path folder = fs.getPath("folder");
		assertFalse(Files.exists(folder));
		Files.createFile(folder);
		assertTrue(Files.exists(folder));
		assertTrue(Files.isRegularFile(folder));
		assertFalse(Files.isDirectory(folder));

		try {
			Path folderCreated = Files.createDirectory(folder);
			assertEquals(folder, folderCreated);
			folder = folderCreated;
			System.out.println(folder + " " + folderCreated);

			// Disable for now, just to see where this leads
			//fail("Should have thrown FileAlreadyExistsException");
		} catch (FileAlreadyExistsException ex) {
			// Bug was fixed in JDK9, no need to test this anymore
			return;
		}

		// For some reason the second createDirectory() fails correctly
		try {
			Files.createDirectory(folder);
			fail("Should have thrown FileAlreadyExistsException");
		} catch (FileAlreadyExistsException ex) {
		}

		Path child = folder.resolve("child");
		Files.createFile(child);

		// Look, it's both a file and folder!
		// Can this be asserted?
		assertTrue(Files.isRegularFile(folder));
		// Yes, if you include the final /
		assertTrue(Files.isDirectory(fs.getPath("folder/")));
		// But not the parent
		// assertTrue(Files.isDirectory(child.getParent()));
		// Or the original Path
		// assertTrue(Files.isDirectory(folder));

		fs.close();
		// What if we open it again.. can we find both?
		try (FileSystem fs2 = FileSystems.newFileSystem(zip, null)) {
			assertTrue(Files.isRegularFile(fs2.getPath("folder")));
			assertTrue(Files.isRegularFile(fs2.getPath("folder/child")));
			assertTrue(Files.isDirectory(fs2.getPath("folder/")));

			// We can even list the folder
			try (DirectoryStream<Path> s = Files.newDirectoryStream(fs2
					.getPath("folder/"))) {
				boolean found = false;
				for (Path p : s) {
					found = p.endsWith("child");
				}
				assertTrue("Did not find 'child'", found);
			}
			// But if we list the root, do we find "folder" or "folder/"?
			Path root = fs2.getRootDirectories().iterator().next();
			try (DirectoryStream<Path> s = Files.newDirectoryStream(root)) {
				List<String> paths = new ArrayList<>();
				for (Path p : s) {
					paths.add(p.toString());
				}
				// We find both!
				assertEquals(2, paths.size());
				assertTrue(paths.contains("/folder"));
				assertTrue(paths.contains("/folder/"));
			}
			// SO does that mean this is a feature, and not a bug?
			// See http://stackoverflow.com/questions/16588321/ for more
		}

	}

	@Test
	public void setLastModifiedTime() throws Exception {
		Path root = fs.getRootDirectories().iterator().next();

		Path folder = root.resolve("folder");
		Files.createDirectory(folder);

		Path file = root.resolve("file");
		Files.createFile(file);

		FileTime someTimeAgo = FileTime.from(365 * 12, TimeUnit.DAYS);
		Files.setLastModifiedTime(folder, someTimeAgo);
		Files.setLastModifiedTime(file, someTimeAgo);
		try {
			Files.setLastModifiedTime(root, someTimeAgo);
		} catch (NoSuchFileException ex) {
			System.err
					.println("Unexpected failure of setLastModifiedTime on root");
			ex.printStackTrace();
		}
	}

	@Before
	public void tempZipFS() throws Exception {
		zip = Files.createTempFile("test", ".zip");
		Files.delete(zip);
		System.out.println(zip);
		URI jar = new URI("jar", zip.toUri().toString(), null);
		Map<String, Object> env = new HashMap<>();
		env.put("create", "true");
		fs = FileSystems.newFileSystem(jar, env);
	}

	@After
	public void deleteTempFS() throws IOException {
		fs.close();
		Files.deleteIfExists(zip);
	}

}
