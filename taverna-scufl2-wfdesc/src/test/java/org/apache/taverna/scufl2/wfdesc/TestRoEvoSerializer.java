package org.apache.taverna.scufl2.wfdesc;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.wfdesc.ROEvoSerializer;
import org.junit.Before;
import org.junit.Test;


public class TestRoEvoSerializer {
	private static final String HELLOWORLD_T2FLOW = "helloanyone.t2flow";
	
	ROEvoSerializer roEvo = new ROEvoSerializer();
	WorkflowBundleIO io = new WorkflowBundleIO();

	private WorkflowBundle helloWorld;
	
	@Before
	public void loadHello() throws ReaderException, IOException {
		InputStream helloStream = getClass().getResourceAsStream("/" + HELLOWORLD_T2FLOW);
		assertNotNull(helloStream);
		helloWorld = io.readBundle(helloStream, "application/vnd.taverna.t2flow+xml");
		assertNotNull(helloWorld);
	}
	
	
	@Test
	public void workflowUUIDs() throws Exception {		
		roEvo.workflowHistory(helloWorld.getMainWorkflow(), System.out);
		
	}
	
}
