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
        String name = p.getName();
        assertTrue(name.startsWith("pf-"));
        String uuidStr = name.replaceAll("^pf-", "");        
        UUID uuid = UUID.fromString(uuidStr);
        assertEquals(4, uuid.version());
        
    }
    
    @Test
    public void workflow() throws Exception {
        Workflow wf = new Workflow();
        String name = wf.getName();
        assertTrue(name.startsWith("wf-"));
        String uuidStr = name.replaceAll("^wf-", "");        
        UUID uuid = UUID.fromString(uuidStr);
        assertEquals(4, uuid.version());
    }
    
    @Test
    public void workflowBundle() throws Exception {
        WorkflowBundle wfBundle = new WorkflowBundle();
        String name = wfBundle.getName();
        System.out.println(name);
        System.out.println(wfBundle.getIdentifier());
        System.out.println(wfBundle.getCurrentRevision());
        assertTrue(name.startsWith("wf-"));
        String uuidStr = name.replaceAll("^wf-", "");        
        UUID uuid = UUID.fromString(uuidStr);
        assertEquals(4, uuid.version());
    }
}
