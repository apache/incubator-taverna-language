package uk.org.taverna.scufl2.api;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

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
}
