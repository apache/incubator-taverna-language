/**
 *
 */
package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

/**
 * @author alanrw
 *
 */
public class TestT2FlowTranslator {

	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void translateSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		// parser.setStrict(true);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());
	}

}
