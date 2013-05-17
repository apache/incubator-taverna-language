package org.purl.wf4ever.robundle.manifest;

import java.net.URI;

public class Agent {
    private URI homepage;
    private URI id;
    private String name;
    private URI orcid;

    public URI getHomepage() {
        return homepage;
    }

    public URI getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public URI getOrcid() {
        return orcid;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrcid(URI orcid) {
        this.orcid = orcid;
    }
}
