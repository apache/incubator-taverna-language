package uk.org.taverna.scufl2.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class CloningIT {
    @Test
    public void cloneIterationStack() throws Exception {
        WorkflowBundleIO io = new WorkflowBundleIO();
        
        WorkflowBundle wf = io.readBundle(getClass().getResource("/clone-error.wfbundle"), null);
        AbstractCloneable clone = wf.clone();
        
        
    }
}
