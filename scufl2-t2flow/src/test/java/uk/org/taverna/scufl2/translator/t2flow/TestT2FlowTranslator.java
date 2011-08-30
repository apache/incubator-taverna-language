/**
 *
 */
package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * @author alanrw
 *
 */
public class TestT2FlowTranslator {

	private static final String AS_T2FLOW = "/as.t2flow";

	private static final String SLEEPERS_T2FLOW = "/sleepers.t2flow";
	
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void translateSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());
	}
	
	@Test
	public void readControlLinks() throws Exception {
		URL wfResource = getClass().getResource(SLEEPERS_T2FLOW);
		assertNotNull("Could not find workflow " + SLEEPERS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());
		Workflow mainWorkflow = bundle.getMainWorkflow();
		NamedSet<Processor> processors = mainWorkflow.getProcessors();
		assertEquals(4, mainWorkflow.getControlLinks().size());
		
		List<BlockingControlLink> blocking2 = scufl2Tools.controlLinksBlocking(processors.getByName("second_sleeper"));
		assertEquals(1, blocking2.size());
		assertEquals("first_sleeper", blocking2.get(0).getUntilFinished().getName());
		
		List<BlockingControlLink> waiting2 = scufl2Tools.controlLinksWaitingFor(processors.getByName("second_sleeper"));
		assertEquals(1, waiting2.size());
		assertEquals("third_sleeper", waiting2.get(0).getBlock().getName());
		
		
		List<BlockingControlLink> blocking3 = scufl2Tools.controlLinksBlocking(processors.getByName("third_sleeper"));
		assertEquals(2, blocking3.size());
		assertEquals("second_sleeper", blocking3.get(0).getUntilFinished().getName());
		assertEquals("second_sleeper_2", blocking3.get(1).getUntilFinished().getName());
		
		
		
	}

}
