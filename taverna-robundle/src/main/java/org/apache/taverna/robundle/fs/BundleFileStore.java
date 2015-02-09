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


import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class BundleFileStore extends FileStore {

	// private final BundleFileSystem fs;
	private final FileStore origFileStore;

	protected BundleFileStore(BundleFileSystem fs, FileStore origFileStore) {
		if (fs == null || origFileStore == null) {
			throw new NullPointerException();
		}
		// this.fs = fs;
		this.origFileStore = origFileStore;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException {
		return origFileStore.getAttribute(attribute);
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(
			Class<V> type) {
		return origFileStore.getFileStoreAttributeView(type);
	}

	@Override
	public long getTotalSpace() throws IOException {
		return origFileStore.getTotalSpace();
	}

	@Override
	public long getUnallocatedSpace() throws IOException {
		return origFileStore.getUnallocatedSpace();
	}

	@Override
	public long getUsableSpace() throws IOException {
		return origFileStore.getUsableSpace();
	}

	@Override
	public boolean isReadOnly() {
		return origFileStore.isReadOnly();
	}

	@Override
	public String name() {
		return origFileStore.name();
	}

	@Override
	public boolean supportsFileAttributeView(
			Class<? extends FileAttributeView> type) {
		return origFileStore.supportsFileAttributeView(type);
	}

	@Override
	public boolean supportsFileAttributeView(String name) {
		return origFileStore.supportsFileAttributeView(name);
	}

	@Override
	public String toString() {
		return origFileStore.toString();
	}

	@Override
	public String type() {
		return "bundle";
	}

}
