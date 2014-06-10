package uk.org.taverna.scufl2.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;

public class TestAnnotationTools {

	WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	WorkflowBundle bundle;
	AnnotationTools annotations = new AnnotationTools();
	
	@Before
	public void loadBundle() throws ReaderException, IOException {
		URL url = getClass().getResource("/helloanyone.t2flow");
		assertNotNull(url);
		bundle = bundleIO.readBundle(url, null);
	}
	
	@Test
	public void getTitle() {
		assertEquals("Hello Anyone", annotations.getTitle(bundle.getMainWorkflow()));
	}
	
	@Test
	public void getCreator() {
		assertEquals("Stian Soiland-Reyes", annotations.getCreator(bundle.getMainWorkflow()));
	}

	
	@Test
	public void getDescription() {
		InputWorkflowPort name = bundle.getMainWorkflow().getInputPorts().getByName("name");
		String desc = annotations.getDescription(name);
		assertEquals("Your name for the greeting", desc);
	}
	
	@Test
	public void getExample() throws Exception {
		InputWorkflowPort name = bundle.getMainWorkflow().getInputPorts().getByName("name");
		String example = annotations.getExampleValue(name);
		assertEquals("World!", example);
		
	}
	
}
