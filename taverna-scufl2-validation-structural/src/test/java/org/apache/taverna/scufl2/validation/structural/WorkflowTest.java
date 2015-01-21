package org.apache.taverna.scufl2.validation.structural;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.validation.structural.ReportStructuralValidationListener;
import org.apache.taverna.scufl2.validation.structural.StructuralValidator;
import org.apache.taverna.scufl2.validation.structural.ValidatorState;
import org.junit.Test;



public class WorkflowTest {
	
	@Test
	public void testIncompleteWorkflow() throws ReaderException, IOException {
    	ReportStructuralValidationListener l = new ReportStructuralValidationListener();
		WorkflowBundle wb = new WorkflowBundle();
		Workflow w = new Workflow();
		wb.setMainWorkflow(w);
	    StructuralValidator sv = new StructuralValidator();
			sv.checkStructure(wb, l);
			@SuppressWarnings("unused")
			ValidatorState vs = sv.getValidatorState();
			assertEquals(1, l.getIncompleteWorkflows().size());
			assert(l.getIncompleteWorkflows().contains(w));	    
	}
	

}
