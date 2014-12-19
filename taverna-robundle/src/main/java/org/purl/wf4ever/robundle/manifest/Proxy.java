package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.UUID;

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

	public void setURI() {
		setURI(URI.create("urn:uuid:" + UUID.randomUUID()));
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}
}
