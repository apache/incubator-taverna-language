package org.purl.wf4ever.robundle.manifest;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "uri", "orcid", "name" })
public class Agent {
    private URI uri;
    private String name;
    private URI orcid;

    public URI getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public URI getOrcid() {
        return orcid;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrcid(URI orcid) {
        this.orcid = orcid;
    }
}
