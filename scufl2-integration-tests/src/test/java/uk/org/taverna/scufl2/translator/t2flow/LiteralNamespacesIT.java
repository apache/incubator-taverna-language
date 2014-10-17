package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class LiteralNamespacesIT {

	/* From http://dev.mygrid.org.uk/issues/browse/SCUFL2-120 */

	private static final String WF_APICONSUMER = "/apiconsumer.t2flow";
	private static WorkflowBundleIO workflowBundleIO = new WorkflowBundleIO();
	
	
	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WF_APICONSUMER);
		assertNotNull("Could not find workflow " + WF_APICONSUMER,
				wfResource);

		File bundleFile = File.createTempFile("test", "wfbundle");		
		
		WorkflowBundle wfBundle = workflowBundleIO.readBundle(wfResource, null);		
		workflowBundleIO.writeBundle(wfBundle, bundleFile, "application/vnd.taverna.scufl2.workflow-bundle");
		wfBundle = workflowBundleIO.readBundle(bundleFile, null);
		@SuppressWarnings("unused")
		String profile = wfBundle.getResources().getResourceAsString("profile/taverna-2.3.0.rdf");		
	}
	
}
