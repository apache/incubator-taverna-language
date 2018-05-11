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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "uri", "about", "content" })
public class PathAnnotation {
	private List<URI> about = new ArrayList<>();
	private URI content;
	private URI uri;

	public void generateAnnotationId() {
		setUri(URI.create("urn:uuid:" + UUID.randomUUID()));
	}

	@JsonIgnore
	public URI getAbout() {
		if (about.isEmpty()) {
			return null;
		} else {
			return about.get(0);
		}
	}

	@JsonIgnore
	public List<URI> getAboutList() {
		return about;
	}

	@JsonProperty("about")
	public Object getAboutObject() {
		if (about.isEmpty()) {
			return null;
		}
		if (about.size() == 1) {
			return about.get(0);
		} else {
			return about;
		}
	}

	@Deprecated
	@JsonIgnore
	public URI getAnnotion() {
		return getAnnotation();
	}

	@Deprecated
	@JsonIgnore
	public URI getAnnotation() {
		return getUri();
	}

	public URI getContent() {
		return content;
	}

	public URI getUri() {
		return uri;
	}

	private URI relativizePath(Path path) {
		return relativizeUri(
				URI.create(path.toUri().getRawPath()));
	}

	private URI relativizeUri(URI relativeOrAbsolute) {
		// NOTE: We can't fix absolute URIs within the RO bundle here, as
		// we don't know the root. We can however get rid of "/.ro/"
		// if it is present.
		return URI.create("/.ro/").relativize(relativeOrAbsolute);
	}

	public void setAbout(List<URI> about) {
		if (about == null) {
			throw new NullPointerException("about list can't be null");
		}
		// Relativize if needed.
		// Note: This will also ensure we make a copy of the list.
		this.about = about.stream()
				.map(this::relativizeUri)
				.collect(Collectors.toList());
	}

	public void setAbout(Path path) {
		setAbout(relativizePath(path));
	}

	public void setAbout(URI about) {
		this.about.clear();
		if (about != null) {
			this.about.add(relativizeUri(about));
		}
	}

	@Deprecated
	public void setAnnotation(URI annotation) {
		setUri(annotation);
	}

	public void setContent(Path path) {
		this.content = relativizePath(path);
	}

	public void setContent(URI content) {
		this.content = relativizeUri(content);
	}

	public void setUri(URI uri) {
		// Normally absolute urn:uuid: URIs, but just in case..
		this.uri = relativizeUri(uri);
	}

	@Override
	public String toString() {
		return "Annotation: " + getContent() + " about " + getAboutList();
	}
}
