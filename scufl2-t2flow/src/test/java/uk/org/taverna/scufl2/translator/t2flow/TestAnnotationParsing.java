package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestAnnotationParsing {

	private static final String WF_ANNOTATED = "/annotated2.2.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WF_ANNOTATED);
		assertNotNull("Could not find workflow " + WF_ANNOTATED,
				wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser
				.parseT2Flow(wfResource.openStream());
		// System.out.println(researchObj.getProfiles().iterator().next()
		// .getConfigurations());
		List<String> revisions = Arrays.asList("bb902d82-b0e4-46fc-bed5-950a3b38bb98", 
				"9e1f7ffd-3bf9-4ba8-9c63-03b79b1858ad");
		
		for (Revision rev : wfBundle.getMainWorkflow().getWorkflowIdentifier()) {
			
		}
		
		
		
	}

}
