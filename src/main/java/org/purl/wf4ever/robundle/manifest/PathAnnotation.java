package org.purl.wf4ever.robundle.manifest;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "annotation", "about", "content" })
public class PathAnnotation {
    private URI about;
    private URI annotation;
    private URI content;

    public URI getAbout() {
        return about;
    }

    public URI getAnnotation() {
        return annotation;
    }

    public URI getContent() {
        return content;
    }

    public void setAbout(URI about) {
        this.about = about;
    }

    public void setAnnotation(URI annotation) {
        this.annotation = annotation;
    }

    public void setContent(URI content) {
        this.content = content;
    }
}
