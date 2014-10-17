package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "uri", "folder", "filename", "aggregatedBy",
		"aggregatedOn" })
public class Proxy {

	private URI uri;
	private Path folder;
	private String filename;
	private Agent aggregatedBy;
	private FileTime aggregatedOn;

	public Agent getAggregatedBy() {
		return aggregatedBy;
	}

	public FileTime getAggregatedOn() {
		return aggregatedOn;
	}

	public String getFilename() {
		return filename;
	}

	public Path getFolder() {
		return folder;
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

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public void setURI() {
		setURI(URI.create("urn:uuid:" + UUID.randomUUID()));
	}
}
