package uk.org.taverna.scufl2.api;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestProfile {
    @Test
    public void profileName() throws Exception {
        Profile p = new Profile();
        UUID uuid = UUID.fromString(p.getName());
        assertEquals(4, uuid.version());
        
    }
    
    @Test
    public void workflow() throws Exception {
        Workflow wf = new Workflow();
        UUID uuid = UUID.fromString(wf.getName());
        assertEquals(4, uuid.version());
    }
    
    @Test
    public void workflowBundle() throws Exception {
        WorkflowBundle wfBundle = new WorkflowBundle();
        UUID uuid = UUID.fromString(wfBundle.getName());
        assertEquals(4, uuid.version());
    }
}
