package org.purl.wf4ever.robundle.manifest;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "uri", "orcid", "name" })
public class Agent {
    private String name;
    private URI orcid;
    private URI uri;

    public String getName() {
        return name;
    }

    public URI getOrcid() {
        return orcid;
    }

    public URI getUri() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrcid(URI orcid) {
        this.orcid = orcid;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
