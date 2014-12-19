package uk.org.taverna.scufl2.api.configurations;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLinkCompareTest;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class ConfigurationTest extends ExampleWorkflow {
	
	@Before
	public void makeBundle() {
		makeWorkflowBundle();
	}
	
	/**
	 * Similar bug to {@link DataLinkCompareTest#dataLinkNotAddedTwice()}
	 */
	@Test
	public void configurationNotAddedTwice() throws Exception {
		Configuration c1a = new Configuration("c1");
		Profile p1 = new Profile("p1");
		p1.getConfigurations().add(c1a);		
		c1a.setParent(p1);
		p1.getConfigurations().add(c1a);
		
		
		Configuration c1b = new Configuration("c1");
		Profile p2 = new Profile("p2");
		p2.getConfigurations().add(c1b);		
		c1b.setParent(p2);
		p2.getConfigurations().add(c1b);
		
		
		WorkflowBundle bundle = new WorkflowBundle();
		p1.setParent(bundle);
		p2.setParent(bundle);
		new Scufl2Tools().setParents(bundle);
		assertEquals(1, p1.getConfigurations().size());
		assertEquals(1, p2.getConfigurations().size());
		
	}
	
	@Test
	public void configurationNotAddedTwiceExample() throws Exception {
		Profile p = workflowBundle.getMainProfile();
		assertEquals(1, p.getConfigurations().size());
		new Scufl2Tools().setParents(workflowBundle);
		assertEquals(1, p.getConfigurations().size());
	}
	
}
