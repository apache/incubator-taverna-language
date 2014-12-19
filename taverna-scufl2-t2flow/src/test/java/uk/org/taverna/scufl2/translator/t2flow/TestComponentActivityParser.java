package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.org.taverna.scufl2.translator.t2flow.defaultactivities.ComponentActivityParser.ACTIVITY_URI;

import java.net.URL;
import java.util.Iterator;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestComponentActivityParser {
	private static final String WF_SIMPLE_COMPONENT = "/component_simple.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	private WorkflowBundle parseWorkflow(String wfPath) throws Exception {
		URL wfResource = getClass().getResource(wfPath);
		assertNotNull("could not find workflow " + wfPath, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		return researchObj;
	}

	@Test
	public void parseSimpleTell() throws Exception {
		WorkflowBundle researchObj = parseWorkflow(WF_SIMPLE_COMPONENT);
		Profile profile = researchObj.getMainProfile();
		assertNotNull("could not find profile in bundle", profile);

		Processor comp = researchObj.getMainWorkflow().getProcessors()
				.getByName("combiner");
		assertNotNull("could not find processor 'combiner'", comp);

		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(comp, profile);

		Activity act = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, act.getType());

		ObjectNode resource = config.getJsonAsObjectNode();
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());

		int length = 0;
		Iterator<?> i = resource.fieldNames();
		while (i.hasNext()) {
			i.next();
			length++;
		}
		assertEquals("must be exactly 4 items in the translated component", 4,
				length);

		assertEquals("http://www.myexperiment.org", resource
				.get("registryBase").textValue());
		assertEquals("SCAPE Utility Components", resource.get("familyName")
				.textValue());
		assertEquals("MeasuresDocCombiner", resource.get("componentName")
				.textValue());
		assertEquals(1, resource.get("componentVersion").asInt());

		assertEquals(2, comp.getInputPorts().size());
		assertEquals(1, comp.getOutputPorts().size());
	}
}
