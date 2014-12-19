package uk.org.taverna.scufl2.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;

public class CloningIT {
    @Test
    public void cloneIterationStack() throws Exception {
        WorkflowBundleIO io = new WorkflowBundleIO();
        
        WorkflowBundle wf = io.readBundle(getClass().getResource("/clone-error.wfbundle"), null);

        Processor proc = wf.getMainWorkflow().getProcessors().getByName("Beanshell");
        IterationStrategyStack stack = proc.getIterationStrategyStack();
        IterationStrategyTopNode root = stack.get(0);
        assertNotSame(stack, root);
        assertNotEquals(stack, root);
        System.out.println(stack);
        System.out.println(root);
        @SuppressWarnings("unused")
		AbstractCloneable clone = wf.clone();
        
        
    }
}
