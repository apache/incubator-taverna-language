package uk.org.taverna.scufl2.api.annotation;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class TestAnnotations {
	
	@Test
	public void listAnnotations() throws Exception {
		WorkflowBundle wfBundle = new WorkflowBundle();
		UCFPackage resources = wfBundle.getResources();
		assertTrue(resources.listResources(wfBundle.getAnnotationResourcesFolder()).isEmpty());
		
		resources.addResource("Dummy", wfBundle.getAnnotationResourcesFolder() + "test.txt", "text/plain");
		Map<String, ResourceEntry> annotations = resources.listResources(wfBundle.getAnnotationResourcesFolder());
		assertEquals(1, annotations.size());
		assertEquals("test.txt", annotations.keySet().iterator().next());
	}
	
	public void addAnnotation() {
		WorkflowBundle wfBundle = new WorkflowBundle();
		Annotation ann = new Annotation();
		wfBundle.getAnnotations().add(ann);
		
	}
	
}
