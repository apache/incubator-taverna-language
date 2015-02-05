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


import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;
import static org.apache.taverna.robundle.Bundles.getManifestPath;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.apache.taverna.robundle.fs.BundleFileSystem;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.RDFToManifest;
import org.apache.taverna.robundle.manifest.combine.CombineManifest;
import org.apache.taverna.robundle.manifest.odf.ODFManifest;

public class Bundle implements Closeable {
	private boolean deleteOnClose;
	private Manifest manifest;
	private final Path root;

	public Bundle(Path root, boolean deleteOnClose) {
		this.root = root;
		this.setDeleteOnClose(deleteOnClose);
	}

	@Override
	public void close() throws IOException {
		close(isDeleteOnClose());
	}

	protected void close(boolean deleteOnClose) throws IOException {
		if (!getFileSystem().isOpen())
			return;

		if (!deleteOnClose) {
			// update manifest
			getManifest().populateFromBundle();
			getManifest().writeAsJsonLD();
			if (ODFManifest.containsManifest(this))
				getManifest().writeAsODFManifest();
			if (CombineManifest.containsManifest(this))
				getManifest().writeAsCombineManifest();
		} else {
			/*
			 * FIXME: Enable this if closing temporary bundles is slow doing
			 * closing (as those files are being compressed):
			 * RecursiveDeleteVisitor.deleteRecursively(getRoot());
			 */
		}
		getFileSystem().close();
		if (deleteOnClose)
			deleteIfExists(getSource());
	}

	public FileSystem getFileSystem() {
		return getRoot().getFileSystem();
	}

	public Manifest getManifest() throws IOException {
		if (manifest == null)
			synchronized (this) {
				if (manifest == null)
					manifest = readOrPopulateManifest();
			}
		return manifest;
	}

	public Path getPath(String path) {
		return getRoot().resolve(path);
	}

	public Path getRoot() {
		return root;
	}

	public Path getSource() {
		BundleFileSystem fs = (BundleFileSystem) getFileSystem();
		return fs.getSource();
	}

	public boolean isDeleteOnClose() {
		return deleteOnClose;
	}

	protected Manifest readOrPopulateManifest() throws IOException {
		Manifest newManifest = new Manifest(this);
		Path manifestPath = getManifestPath(this);
		if (exists(manifestPath)) {
			try (InputStream manifestStream = newInputStream(manifestPath)) {
				new RDFToManifest().readTo(manifestStream, newManifest,
						manifestPath.toUri());
			}
			// TODO: Also support reading manifest.rdf?
		} else if (ODFManifest.containsManifest(this)) {
			new ODFManifest(newManifest).readManifestXML();
		} else if (CombineManifest.containsManifest(this)) {
			new CombineManifest(newManifest).readCombineArchive();
		} else {
			// Fallback (might be a fresh or 3rd party bundle), populate from
			// zip content
			newManifest.populateFromBundle();
		}
		return newManifest;
	}

	public void setDeleteOnClose(boolean deleteOnClose) {
		this.deleteOnClose = deleteOnClose;
	}
}
