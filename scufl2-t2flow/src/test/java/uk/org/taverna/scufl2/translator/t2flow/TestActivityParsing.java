package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestActivityParsing {

    private static final String WF_FASTA_AND_PSCAN = "/fasta_and_pscan.t2flow";
    private static final String WF_FASTA_PSCAN_AND_DBFETCH = "/fasta_pscan_and_dbfetch.t2flow";
    private static final String WF_SIMPLE_FASTA = "/simple_fasta.t2flow";

    
    private static Scufl2Tools scufl2Tools = new Scufl2Tools();

    @Test
    public void fastaPscan() throws Exception {
        URL wfResource = getClass().getResource(WF_FASTA_AND_PSCAN);
        assertNotNull("Could not find workflow " + WF_FASTA_AND_PSCAN,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());

        Profile p = wfBundle.getMainProfile();
        for (Configuration c : p.getConfigurations()) {
            System.out.println(c.getConfigures());
            System.out.println(c.getJson());
        }
    }

    @Test
    public void fastaPscanDbfetch() throws Exception {
        URL wfResource = getClass().getResource(WF_FASTA_PSCAN_AND_DBFETCH);
        assertNotNull("Could not find workflow " + WF_FASTA_PSCAN_AND_DBFETCH,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());

        Profile p = wfBundle.getMainProfile();
        for (Configuration c : p.getConfigurations()) {
            System.out.println(c.getConfigures());
            System.out.println(c.getJson());
        }
    }

    @Test
    public void simpleFasta() throws Exception {
        URL wfResource = getClass().getResource(WF_SIMPLE_FASTA);
        assertNotNull("Could not find workflow " + WF_SIMPLE_FASTA,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());

        Profile p = wfBundle.getMainProfile();
        for (Configuration c : p.getConfigurations()) {
            System.out.println(c.getConfigures());
            System.out.println(c.getJson());
        }
    }

}
