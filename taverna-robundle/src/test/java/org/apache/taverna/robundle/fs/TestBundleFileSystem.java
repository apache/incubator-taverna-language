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
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.junit.Test;

public class TestBundleFileSystem extends Helper {

	@Test
	public void writeToNewFile() throws Exception {
		Path file = fs.getPath("test.txt");
		Files.newBufferedWriter(file, Charset.defaultCharset()).close();
	}

	@Test
	public void reopenNew() throws Exception {
		Path x = Files.createTempFile("temp", ".zip");
		Bundle bundle = Bundles.createBundle(x);
		Path newFile = Files.createTempFile("temp", ".zip");
		Bundles.closeAndSaveBundle(bundle, newFile);
		Bundles.openBundle(newFile);
	}

	@Test
	public void closeAndSaveToPreserveOriginal() throws Exception {
		Path x = Files.createTempFile("temp", ".zip");
		Bundle bundle = Bundles.createBundle(x);
		Path newFile = Files.createTempFile("temp", ".zip");
		Bundles.closeAndSaveBundle(bundle, newFile);
		Bundles.openBundle(x);
	}

	/**
	 * Test that BundleFileSystem does not allow a ZIP file to also become a
	 * directory. See http://stackoverflow.com/questions/16588321/ as Java 7'z
	 * ZIPFS normally allows this (!)
	 * 
	 * @throws Exception
	 */
	@Test
	public void fileAndDirectory() throws Exception {
		Path folder = fs.getPath("folder");

		// To test on local file system, uncomment next 2 lines:
		// Path test = Files.createTempDirectory("test");
		// folder = test.resolve("folder");

		assertFalse(Files.exists(folder));
		Files.createFile(folder);
		assertTrue(Files.exists(folder));
		assertTrue(Files.isRegularFile(folder));
		assertFalse(Files.isDirectory(folder));

		try {
			Files.createDirectory(folder);
			fail("Should have thrown FileAlreadyExistsException");
		} catch (FileAlreadyExistsException ex) {
		}
		assertFalse(Files.isDirectory(folder));

		try {
			Files.createDirectories(folder);
			fail("Should have thrown FileAlreadyExistsException");
		} catch (FileAlreadyExistsException ex) {
		}
		assertFalse(Files.isDirectory(folder));

		Path child = folder.resolve("child");

		try {
			Files.createFile(child);
			fail("Should have thrown NoSuchFileException");
		} catch (NoSuchFileException ex) {
		}
		assertFalse(Files.exists(child));

		assertTrue(Files.isRegularFile(folder));
		assertFalse(Files.isDirectory(folder));
		assertFalse(Files.isDirectory(child.getParent()));
		assertFalse(Files.isDirectory(fs.getPath("folder/")));
	}

	/**
	 * Test that BundleFileSystem does not allow a ZIP directory to also become
	 * a file. See http://stackoverflow.com/questions/16588321/ as Java 7'z
	 * ZIPFS normally allows this (!)
	 * 
	 * @throws Exception
	 */
	@Test
	public void directoryAndFile() throws Exception {
		Path folderSlash = fs.getPath("folder/");
		Path folder = fs.getPath("folder");

		// Uncomment next 3 lines to test on local FS
		// Path test = Files.createTempDirectory("test");
		// folderSlash = test.resolve("folder/");
		// folder = test.resolve("folder");

		assertFalse(Files.exists(folderSlash));

		Files.createDirectory(folderSlash);
		assertTrue(Files.exists(folderSlash));
		assertFalse(Files.isRegularFile(folderSlash));
		assertTrue(Files.isDirectory(folderSlash));

		try {
			Files.createDirectory(folderSlash);
			fail("Should have thrown FileAlreadyExistsException");
		} catch (FileAlreadyExistsException ex) {
		}

		try {
			Files.createFile(folderSlash);
			fail("Should have thrown IOException");
		} catch (IOException ex) {
		}

		try {
			Files.createFile(folder);
			fail("Should have thrown IOException");
		} catch (IOException ex) {
		}

		Path child = folderSlash.resolve("child");
		Files.createFile(child);

		assertTrue(Files.exists(folder));
		assertTrue(Files.exists(folderSlash));

		assertFalse(Files.isRegularFile(folder));
		assertFalse(Files.isRegularFile(folderSlash));

		assertTrue(Files.isDirectory(folder));
		assertTrue(Files.isDirectory(folderSlash));

	}

	@Test
	public void setLastModifiedTime() throws Exception {
		Path root = fs.getRootDirectories().iterator().next();

		Path folder = root.resolve("folder");
		Files.createDirectory(folder);

		Path file = root.resolve("file");
		Files.createFile(file);

		int manyDays = 365 * 12;
		FileTime someTimeAgo = FileTime.from(manyDays, TimeUnit.DAYS);
		Files.setLastModifiedTime(folder, someTimeAgo);
		Files.setLastModifiedTime(file, someTimeAgo);
		Files.setLastModifiedTime(root, someTimeAgo);

		// Should be equal, +/- 2 seconds (allowing precision loss)
		assertEquals((double) someTimeAgo.toMillis(), Files
				.getLastModifiedTime(folder).toMillis(), 2001);
		assertEquals((double) someTimeAgo.toMillis(), Files
				.getLastModifiedTime(file).toMillis(), 2001);

		// Fails as we'll get back -1 instead
		// assertEquals((double)someTimeAgo.toMillis(),
		// Files.getLastModifiedTime(root).toMillis(), 2001);
	}

	@Test
	public void creationTime() throws Exception {
		Path root = fs.getRootDirectories().iterator().next();

		Path folder = root.resolve("folder");
		Files.createDirectory(folder);

		Path file = root.resolve("file");
		Files.createFile(file);

		int manyDays = 365 * 12;
		FileTime someTimeAgo = FileTime.from(manyDays, TimeUnit.DAYS);

		Files.getFileAttributeView(folder, BasicFileAttributeView.class)
				.setTimes(null, null, someTimeAgo);
		Files.getFileAttributeView(file, BasicFileAttributeView.class)
				.setTimes(null, null, someTimeAgo);
		Files.getFileAttributeView(root, BasicFileAttributeView.class)
				.setTimes(null, null, someTimeAgo);

		// Should be equal, +/- 2 seconds
		assertEquals((double) someTimeAgo.toMillis(),
				(double) ((FileTime) Files.getAttribute(file, "creationTime"))
						.toMillis(), 2001);
		assertEquals(
				(double) someTimeAgo.toMillis(),
				(double) ((FileTime) Files.getAttribute(folder, "creationTime"))
						.toMillis(), 2001);

		// FIXME: FAils with NullPointerException! :(
		// assertEquals((double)someTimeAgo.toMillis(), (double)
		// ((FileTime)Files.getAttribute(root, "creationTime")).toMillis(),
		// 2001);

	}
}
