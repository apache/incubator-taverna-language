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


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class BundlePath implements Path {

	private final BundleFileSystem fs;

	private final Path zipPath;

	protected BundlePath(BundleFileSystem fs, Path zipPath) {
		if (fs == null || zipPath == null) {
			throw new NullPointerException();
		}
		this.fs = fs;
		this.zipPath = zipPath;
	}

	public int compareTo(Path other) {
		return zipPath.compareTo(fs.unwrap(other));
	}

	public boolean endsWith(Path other) {
		return zipPath.endsWith(fs.unwrap(other));
	}

	public boolean endsWith(String other) {
		return zipPath.endsWith(other);
	}

	public boolean equals(Object other) {
		if (!(other instanceof BundlePath)) {
			return false;
		}
		BundlePath bundlePath = (BundlePath) other;
		return zipPath.equals(fs.unwrap(bundlePath));
	}

	public BundlePath getFileName() {
		return fs.wrap(zipPath.getFileName());
	}

	@Override
	public BundleFileSystem getFileSystem() {
		return fs;
	}

	public BundlePath getName(int index) {
		return fs.wrap(zipPath.getName(index));
	}

	public int getNameCount() {
		return zipPath.getNameCount();
	}

	public BundlePath getParent() {
		return fs.wrap(zipPath.getParent());
	}

	public BundlePath getRoot() {
		return fs.wrap(zipPath.getRoot());
	}

	protected Path getZipPath() {
		return zipPath;
	}

	public int hashCode() {
		return zipPath.hashCode();
	}

	public boolean isAbsolute() {
		return zipPath.isAbsolute();
	}

	public Iterator<Path> iterator() {
		return fs.wrapIterator(zipPath.iterator());
	}

	public BundlePath normalize() {
		return fs.wrap(zipPath.normalize());
	}

	public WatchKey register(WatchService watcher, Kind<?>... events)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	public WatchKey register(WatchService watcher, Kind<?>[] events,
			Modifier... modifiers) throws IOException {
		throw new UnsupportedOperationException();
	}

	public BundlePath relativize(Path other) {
		return fs.wrap(zipPath.relativize(fs.unwrap(other)));
	}

	public BundlePath resolve(Path other) {
		return fs.wrap(zipPath.resolve(fs.unwrap(other)));
	}

	public BundlePath resolve(String other) {
		return fs.wrap(zipPath.resolve(other));
	}

	public BundlePath resolveSibling(Path other) {
		return fs.wrap(zipPath.resolveSibling(fs.unwrap(other)));
	}

	public BundlePath resolveSibling(String other) {
		return fs.wrap(zipPath.resolveSibling(other));
	}

	public boolean startsWith(Path other) {
		return zipPath.startsWith(fs.unwrap(other));
	}

	public boolean startsWith(String other) {
		return zipPath.startsWith(other);
	}

	public BundlePath subpath(int beginIndex, int endIndex) {
		return fs.wrap(zipPath.subpath(beginIndex, endIndex));
	}

	public BundlePath toAbsolutePath() {
		return fs.wrap(zipPath.toAbsolutePath());
	}

	public File toFile() {
		throw new UnsupportedOperationException();
	}

	public BundlePath toRealPath(LinkOption... options) throws IOException {
		return fs.wrap(zipPath.toRealPath(options));
	}

	/**
	 * Note: This method is used by JSON serialization and should return a valid
	 * relative path from .ro/ or /
	 */
	public String toString() {
		if (zipPath.isAbsolute() && zipPath.startsWith("/.ro/")) {
			Path base = fs.getRootDirectory().zipPath.resolve(".ro");
			return base.relativize(zipPath).toString();
		} else {
			return zipPath.toString();
		}
	}

	public URI toUri() {
		Path abs = zipPath.toAbsolutePath();
		URI pathRel;
		try {
			pathRel = new URI(null, null, abs.toString(), null);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't create URL for " + zipPath,
					e);
		}
		return fs.getBaseURI().resolve(pathRel);
	}

}
