package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.util.GregorianCalendar;
import java.util.List;

import org.purl.wf4ever.robundle.Bundle;

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
    
    public void populateFromBundle(Bundle bundle) {
        // TODO Auto-generated method stub
        
    }
    
    
}
