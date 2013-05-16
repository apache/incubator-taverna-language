package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.util.GregorianCalendar;
import java.util.List;

public class PathMetadata {
    URI file;
    URI uri;
    Path folder;
    String mediatype;
    GregorianCalendar createdOn;
    List<Agent> createdBy;
    URI proxy;

}
