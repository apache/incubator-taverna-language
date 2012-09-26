package uk.org.taverna.scufl2.api.annotation;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class TestAnnotations {
	
	
	public void addAnnotation() {
		WorkflowBundle wfBundle = new WorkflowBundle();
		Annotation ann = new Annotation();
		wfBundle.getAnnotations().add(ann);
		
	}
	
}
