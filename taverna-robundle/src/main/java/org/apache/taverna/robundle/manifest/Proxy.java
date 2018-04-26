package org.apache.taverna.robundle.manifest;

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
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "uri", "folder", "filename", "aggregatedBy",
		"aggregatedOn" })
public class Proxy {

	private Agent aggregatedBy;
	private FileTime aggregatedOn;
	private String filename;
	private Path folder;
	private URI uri;

	public Agent getAggregatedBy() {
		return aggregatedBy;
	}

	public FileTime getAggregatedOn() {
		return aggregatedOn;
	}

	public String getFilename() {
		return filename;
	}

	@JsonIgnore
	public Path getFolder() {
		return folder;
	}
	
	@JsonProperty("folder")
	public String getFolderName() {
		String folderName = getFolder().toString();
		if (! folderName.endsWith("/")) {
			return folderName + "/";
		}
		return folderName;
	}
	
	public URI getURI() {
		return uri;
	}

	public void setAggregatedBy(Agent aggregatedBy) {
		this.aggregatedBy = aggregatedBy;
	}

	public void setAggregatedOn(FileTime aggregatedOn) {
		this.aggregatedOn = aggregatedOn;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setFolder(Path folder) {
		this.folder = folder;
	}

	public void setURI() {
		setURI(URI.create("urn:uuid:" + UUID.randomUUID()));
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}
}
