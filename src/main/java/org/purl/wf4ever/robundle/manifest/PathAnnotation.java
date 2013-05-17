package org.purl.wf4ever.robundle.manifest;

import java.net.URI;

public class PathAnnotation {
    private URI annotation;
    private URI about;
    private URI content;

    public URI getAnnotation() {
        return annotation;
    }

    public URI getAbout() {
        return about;
    }

    public URI getContent() {
        return content;
    }

    public void setAnnotation(URI annotation) {
        this.annotation = annotation;
    }

    public void setAbout(URI about) {
        this.about = about;
    }

    public void setContent(URI content) {
        this.content = content;
    }
}
