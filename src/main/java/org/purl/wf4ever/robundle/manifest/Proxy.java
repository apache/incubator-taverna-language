package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "proxy", "folder", "filename", "aggregatedBy", "aggregatedOn" })

public class Proxy {
	
	private URI proxy;
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
	public URI getProxy() {
		return proxy;
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
	public void setProxy(URI proxy) {
		this.proxy = proxy;
	}
	
}
