package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "file", "uri", "folder", "mediatype", "createdOn",
        "createdBy", "conformsTo", "proxy" })
public class PathMetadata {
    private List<Agent> createdBy = new ArrayList<>();
    private FileTime createdOn;
    private Path file;
    private Path folder;
    private String mediatype;
    private URI proxy;
    private URI uri;
    private URI conformsTo;

    protected PathMetadata() {
    }

    @JsonCreator
    public PathMetadata(String uriStr) {
        setUri(URI.create(uriStr));
    }
    
    public URI getConformsTo() {
        return conformsTo;
    }
    
    public List<Agent> getCreatedBy() {
        return createdBy;
    }

    public FileTime getCreatedOn() {
        return createdOn;
    }

    public Path getFile() {
        return file;
    }

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
    
    public void setConformsTo(URI conformsTo) {
        this.conformsTo = conformsTo;
    }    

    public void setCreatedBy(List<Agent> createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(FileTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public void setFolder(Path folder) {
        this.folder = folder;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public void setProxy() {
        setProxy(URI.create("urn:uuid:" + UUID.randomUUID()));
    }
    
    public void setProxy(URI proxy) {
        this.proxy = proxy;
    }

    public void setUri(URI uri) {
        this.uri = uri;
        if (! uri.isAbsolute()) {
            // TODO: How to create a Path without knowing the root?
//            file = uri;
//            this.uri = null;
        }
    }
    
    @Override
    public String toString() {
        if (getUri() != null) { 
            return getUri().toString();
        }
        if (getFile() != null) {
            return getFile().toString();
        }
        if (getProxy() != null) { 
            return getProxy().toString();
        }
        return "PathMetadata <null>";
    }


    

}
