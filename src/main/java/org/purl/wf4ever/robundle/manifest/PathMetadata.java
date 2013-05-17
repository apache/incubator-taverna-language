package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "file", "uri", "folder", "mediatype", "createdOn", "createdBy", "proxy"})
public class PathMetadata {
    private List<Agent> createdBy;
    private FileTime createdOn;
    private URI file;
    private Path folder;
    private String mediatype;
    private URI proxy;
    private URI uri;

    public List<Agent> getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public FileTime getCreatedOn() {
        return createdOn;
    }
    
    public URI getFile() {
        return file;
    }

    @JsonIgnore
    public Path getFolder() {
        return folder;
    }

    public String getMediatype() {
        return mediatype;
    }

    public URI getProxy() {
        return proxy;
    }
        
    public URI getUri() {
        return uri;
    }

    public void setCreatedBy(List<Agent> createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(FileTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setFile(URI file) {
        this.file = file;
    }

    public void setFolder(Path folder) {
        this.folder = folder;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public void setProxy(URI proxy) {
        this.proxy = proxy;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

}
