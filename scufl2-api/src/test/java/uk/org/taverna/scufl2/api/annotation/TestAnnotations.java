package uk.org.taverna.scufl2.api.annotation;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestAnnotations {
	
	
	public void addAnnotation() {
		WorkflowBundle wfBundle = new WorkflowBundle();
		Annotation ann = new Annotation();
		wfBundle.getAnnotations().add(ann);
		
	}
	
}
