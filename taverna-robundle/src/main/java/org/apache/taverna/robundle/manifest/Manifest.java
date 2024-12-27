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

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.attribute.FileTime.fromMillis;
import static org.apache.taverna.robundle.Bundles.uriToBundlePath;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.manifest.combine.CombineManifest;
import org.apache.taverna.robundle.manifest.odf.ODFManifest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonPropertyOrder(value = { "@context", "id", "manifest", "conformsTo", "createdOn",
		"createdBy", "createdOn", "authoredOn", "authoredBy",
		"retrievedFrom", "retrievedOn", "retrievedBy",
		"history", "aggregates", "annotations", "@graph" })
public class Manifest {
	public abstract class FileTimeMixin {
		@Override
		@JsonValue
		public abstract String toString();
	}

	public abstract class PathMixin {
		@Override
		@JsonValue
		public abstract String toString();
	}

	private static Logger logger = Logger.getLogger(Manifest.class
			.getCanonicalName());

	private static final String MANIFEST_JSON = "manifest.json";

	private static final String META_INF = "/META-INF";

	private static final String MIMETYPE = "/mimetype";
	private static final String RO = "/.ro";
	private static URI ROOT = URI.create("/");

	public static FileTime now() {
		return fromMillis(new GregorianCalendar().getTimeInMillis());
	}


	private Map<URI, PathMetadata> aggregates = new LinkedHashMap<>();
	private List<PathAnnotation> annotations = new ArrayList<>();
	private List<Agent> authoredBy = new ArrayList<>();
	private FileTime authoredOn;
	private Bundle bundle;
	private Agent createdBy = null;
	private FileTime createdOn = now();
	private URI retrievedFrom = null;
	private Agent retrievedBy = null;
	private FileTime retrievedOn = null;
	private List<String> graph;
	private List<Path> history = new ArrayList<>();
	private URI id = URI.create("/");
	private List<Path> manifest = new ArrayList<>();
	private List<URI> conformsTo = new ArrayList<>();

	public Manifest(Bundle bundle) {
		this.bundle = bundle;
	}

	public List<PathMetadata> getAggregates() {
		return new ArrayList<>(aggregates.values());
	}

	public PathMetadata getAggregation(Path file) {
		URI fileUri = file.toUri();
		return getAggregation(fileUri);
	}

	public PathMetadata getAggregation(URI uri) {
		uri = relativeToBundleRoot(uri);
		PathMetadata metadata = aggregates.get(uri);
		if (metadata == null) {
			metadata = new PathMetadata();
			if (!uri.isAbsolute() && uri.getFragment() == null) {
				Path path = uriToBundlePath(bundle, uri);
				metadata.setFile(path);
				metadata.setMediatype(guessMediaType(path));
			} else {
				metadata.setUri(uri);
			}
			aggregates.put(uri, metadata);
		}
		return metadata;
	}

	public List<PathAnnotation> getAnnotations() {
		return annotations;
	}

	@JsonIgnore
	public Optional<PathAnnotation> getAnnotation(URI annotation) {
		return getAnnotations().stream()
				.filter(a -> annotation.equals(a.getUri()))
				.findAny();
	}

	@JsonIgnore
	public List<PathAnnotation> getAnnotations(final URI about) {
		final URI aboutAbs;
		URI manifestBase = getBaseURI().resolve(RO + "/" + MANIFEST_JSON);
		if (about.isAbsolute()) {
			aboutAbs = about;
		} else {
			aboutAbs = manifestBase.resolve(about);
		}
		// Compare absolute URIs against absolute URIs
		return getAnnotations().stream()
				.filter(a -> a.getAboutList().stream()
						.map(manifestBase::resolve)
						.filter(aboutAbs::equals)
						.findAny().isPresent())
				.collect(Collectors.toList());
	}

	@JsonIgnore
	public List<PathAnnotation> getAnnotations(Path about) {
		return getAnnotations(about.toUri());
	}

	public List<Agent> getAuthoredBy() {
		return authoredBy;
	}

	public FileTime getAuthoredOn() {
		return authoredOn;
	}

	@JsonIgnore
	public URI getBaseURI() {
		return getBundle().getRoot().toUri();
	}

	@JsonIgnore
	public Bundle getBundle() {
		return bundle;
	}

	public List<URI> getConformsTo() {
		return conformsTo;
	}


	@JsonProperty(value = "@context")
	public List<Object> getContext() {
		ArrayList<Object> context = new ArrayList<>();
		context.add(URI.create("https://w3id.org/bundle/context"));
		return context;
	}

	public Agent getCreatedBy() {
		return createdBy;
	}

	public FileTime getCreatedOn() {
		return createdOn;
	}

	public URI getRetrievedFrom() {
		return retrievedFrom;
	}

	public Agent getRetrievedBy() {
		return retrievedBy;
	}

	public FileTime getRetrievedOn() {
		return retrievedOn;
	}

	public List<String> getGraph() {
		return graph;
	}

	public List<Path> getHistory() {
		return history;
	}

	public URI getId() {
		return id;
	}

	public List<Path> getManifest() {
		return manifest;
	}

	/**
	 * Guess media type based on extension
	 *
	 * @see http://wf4ever.github.io/ro/bundle/#media-types
	 *
	 * @param file
	 *            A Path to a file
	 * @return media-type, e.g. <code>application/xml</code> or
	 *         <code>text/plain; charset="utf-8"</code>
	 */
	public String guessMediaType(Path file) {
		if (file.getFileName() == null)
			return null;
		String filename = file.getFileName().toString()
				.toLowerCase(Locale.ENGLISH);
		if (filename.endsWith(".txt"))
			return "text/plain; charset=\"utf-8\"";
		if (filename.endsWith(".ttl"))
			return "text/turtle; charset=\"utf-8\"";
		if (filename.endsWith(".rdf") || filename.endsWith(".owl"))
			return "application/rdf+xml";
		if (filename.endsWith(".json"))
			return "application/json";
		if (filename.endsWith(".jsonld"))
			return "application/ld+json";
		if (filename.endsWith(".xml"))
			return "application/xml";

		// A few extra, common ones

		if (filename.endsWith(".png"))
			return "image/png";
		if (filename.endsWith(".svg"))
			return "image/svg+xml";
		if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
			return "image/jpeg";
		if (filename.endsWith(".pdf"))
			return "application/pdf";
		return "application/octet-stream";
	}

	public void populateFromBundle() throws IOException {
		final Set<Path> potentiallyEmptyFolders = new LinkedHashSet<>();

		final Set<URI> existingAggregationsToPrune = new HashSet<>(
				aggregates.keySet());

		walkFileTree(bundle.getRoot(), new SimpleFileVisitor<Path>() {
			@SuppressWarnings("deprecation")
			@Override
			public FileVisitResult visitFile(Path path,
											 BasicFileAttributes attrs) throws IOException {
				URI fileUri = path.toUri();
				if (!aggregates.containsKey(fileUri)) {
					aggregates.put(fileUri, new PathMetadata());
				}
				aggregates.get(fileUri).setFile(path);
				if (attrs.isDirectory()) {
					potentiallyEmptyFolders.add(path);
				}
				return CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir,
													  IOException exc) throws IOException {
				// Remove any aggregations to directories that no longer exist
				if (potentiallyEmptyFolders.contains(dir)) {
					aggregates.entrySet().removeIf(e -> e.getKey()
							.equals(dir.toUri()));
				}
				return CONTINUE;
			}
		});

		// Update `createdOn` timestamp
		setCreatedOn(now());

		// After loading the files into aggregates, ensure those URIs that
		// aren't file-based are removed
		aggregates.keySet().removeAll(existingAggregationsToPrune);
	}

	/**
	 * Remove references to the old URI scheme from the manifest
	 *
	 * @param uri URI to be replaced with arcp://
	 * @return URI with the scheme replaced by arcp://
	 */
	public URI relativeToBundleRoot(URI uri) {
		// Replace app:// URIs with arcp:// URIs
		if (uri.toString().startsWith("app://")) {
			uri = URI.create("arcp://" + uri.toString().substring(6)); // Convert app:// to arcp://
		}
		uri = ROOT.resolve(bundle.getRoot().toUri().relativize(uri));
		return uri;
	}

	// Save to output file
	public void saveToManifest(Writer out) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(INDENT_OUTPUT);
		mapper.configure(FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(WRITE_NULL_MAP_VALUES, false);
		mapper.configure(WRITE_EMPTY_JSON_ARRAYS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.writeValue(out, this);
	}
}
