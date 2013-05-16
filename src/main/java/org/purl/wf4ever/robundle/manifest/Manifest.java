package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.util.GregorianCalendar;
import java.util.List;

public class Manifest {
    URI id;
    List<Path> manifest;
    GregorianCalendar createdOn;
    List<Agent> createdBy;
    GregorianCalendar authoredOn;
    List<Agent> authoredBy;
    List<Path> history;
    List<PathMetadata> aggregates;
    List<PathAnnotation> annotations;
    List<String> graph;
    
    
}
