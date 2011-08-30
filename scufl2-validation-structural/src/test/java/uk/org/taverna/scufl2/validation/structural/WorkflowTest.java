package uk.org.taverna.scufl2.validation.structural;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.validation.ValidationException;


public class WorkflowTest {
	
	@Test
	public void testIncompleteWorkflow() throws ReaderException, IOException {
    	ReportStructuralValidationListener l = new ReportStructuralValidationListener();
		WorkflowBundle wb = new WorkflowBundle();
		Workflow w = new Workflow();
		wb.setMainWorkflow(w);
	    StructuralValidator sv = new StructuralValidator();
			sv.checkStructure(wb, l);
			ValidatorState vs = sv.getValidatorState();
			assertEquals(1, l.getIncompleteWorkflows().size());
			assert(l.getIncompleteWorkflows().contains(w));	    
	}
	

}
